package agents;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gui.NegotiationBotGUI;
import io.JsonIO;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;
import model.Car;
import model.CarList;

public class BrokerAgent extends Agent {

	private static final long serialVersionUID = -1539612606764155406L;
	private ObjectMapper o = new ObjectMapper();
	private CarList catalog = new CarList();
	private JsonIO jsonDB = new JsonIO("./DataBase/JsonDB.txt");
	private static final double COMMISION = 100;
	private double recievedCommision;
	
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Broker-agent " + getAID().getName() + " is ready.");
		jsonDB.clearFile();
		// Register the car-broker service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("car-broker");
		sd.setName("JADE-car-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			System.err.println("Problem by registering the car-broker service in the yellow pages");
		}
		
		// Adding behaviors to the broker agent
		addBehaviour(new ReceiveListOfCarsFromDealer());
		addBehaviour(new DealWithRequestFromBuyers());
		addBehaviour(new RequestFromBuyersToConnectToDealer());
		addBehaviour(new ConfirmSellWithBroker());
	}

	/**
	 * Method for the Agent clean-up
	 */
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Broker-agent " + getAID().getName() + " terminating.");
	}
	
	/*
	 This Behaviour (Receive confirmation from buyer agent/dealer) does the agent handshake after both agents accept offers
	 */
	private class ConfirmSellWithBroker extends CyclicBehaviour{
		
		@Override
		public void action() {
			//MessageTemplate for the Conversation between dealer and broker (the agents confirm car sell with broker)
			MessageTemplate mt0 = MessageTemplate.and(MessageTemplate.MatchConversationId("confirm_sell"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg0 = myAgent.receive(mt0);
			

			if (msg0 != null) {
				// Message received. Process it
				String content = msg0.getContent();
				try {
					Car negotiatedCar = o.readValue(content, Car.class);
					String agentName = msg0.getSender().getName();
					double offerPrice = Double.parseDouble(msg0.getReplyWith());
					offerPrice -= COMMISION;
					recievedCommision += COMMISION;
					System.out.println("\nBroker confirm !!! " + agentName + " confirm sell including Broker Commision: " + offerPrice + "\nBroker Commision: " + recievedCommision);
					
					//Update JsonDB/CarList
					for(Car c : catalog) {
						if(negotiatedCar.getCarId() == c.getCarId()) {
							c.setcarStatus(true);
						}
					}
					
					String jsonInString = o.writeValueAsString(catalog);
					jsonDB.clearFile();
					jsonDB.writeToFile(jsonInString);
					System.out.println("Broker: Broker cataloge: \n" + catalog);
				} catch (IOException e) {
					e.printStackTrace();
				}				
				
			} else {
				block();
			}
			
		}
	}
	

	/**
	 * Class for behavior of the broker agent. 
	 * The broker is able to receive the list of cars from dealer agent and save it in his catalog.
	 */
	private class ReceiveListOfCarsFromDealer extends CyclicBehaviour {
		
		private static final long serialVersionUID = -7220993343334935627L;

		@Override
		public void action() {
			//MessageTemplate for the Conversation between dealer and broker (the dealer sends a list of Car to the broker)
			MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-dealer-broker"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg1 = myAgent.receive(mt1);

			if (msg1 != null) {
				// Message received. Process it
				String carlist = msg1.getContent();
				jsonDB.writeToFile(carlist);
				catalog = jsonDB.readFile();
				System.out.println("Broker: Broker cataloge: \n" + catalog);
			} else {
				block();
			}
		}
	}
	
	private class DealWithRequestFromBuyers extends CyclicBehaviour {

		private static final long serialVersionUID = -8496805180917867030L;

		@Override
		public void action() {
			MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-broker-buyer"),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg2 = myAgent.receive(mt2);
			//catalog = new CarList();
			if (msg2 != null) {
				//Load catalog
				//CarList list = jsonDB.readFile();
				//if (list != null ) {
				//	catalog.addAll(list);
				//}
				// Message received. Process it
				String desiredCarJson = msg2.getContent();
				try {
					Car desiredCar = o.readValue(desiredCarJson, Car.class);
					System.out.println("Broker: Receive a request from Buyer:\n" + desiredCar + "\n");

					CarList listOfPossibleCar = new CarList();
					listOfPossibleCar.addAll(getListOfPossibleCars(desiredCar));

					if (listOfPossibleCar.size() > 0) {
						// Send back a list of cars to the Buyer if possible
						String jsonInString;
						try {
							jsonInString = o.writeValueAsString(listOfPossibleCar);
							ACLMessage reply = msg2.createReply();
							reply.setPerformative(ACLMessage.INFORM);
							reply.setReplyWith("performative");
							reply.setContent(jsonInString);
							reply.setConversationId("car-offer");
							myAgent.send(reply);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
					} else {
						// when no matching car is found
						ACLMessage reply = msg2.createReply();
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setReplyWith("performative");
						reply.setContent("\nThere are no possible car to offer! Sorry! ");
						reply.setConversationId("car-offer");
						myAgent.send(reply);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				block();
			}
		}
	}
	
	private class RequestFromBuyersToConnectToDealer extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-broker-buyer"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println("Broker: Receive a choosen car from buyer " + msg.getSender().getName());
				String choosenCarJson = msg.getContent();
				String firstOfferPrice = msg.getReplyWith();
				try {
					Car choosenCar = o.readValue(choosenCarJson, Car.class);
					System.out.println(choosenCar + "\n");
					ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
					mess.addReceiver(AgentSupport.findAgentWithName(myAgent, choosenCar.getAgent()));
					mess.setContent(choosenCarJson);
					mess.setConversationId("car-trade-broker-seller");
					mess.setReplyWith(msg.getSender().getName()); // name of the buyer.
					mess.setInReplyTo(firstOfferPrice);
					myAgent.send(mess);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				block();
			}
		}
		
	}
	
	private List<Car> getListOfPossibleCars(Car desiredCar){
		List<Car> filterList = new CarList();
		filterList.addAll(catalog);
		if (desiredCar.getManufacture() != null) {			
			filterList.retainAll(catalog.stream().filter(car -> car.getManufacture().equals(desiredCar.getManufacture())).collect(Collectors.toList()));
		}
		if (desiredCar.getModel() != null) {
			filterList.retainAll(catalog.stream().filter(car -> car.getModel().equals(desiredCar.getModel())).collect(Collectors.toList()));
		}
		if (desiredCar.getTransmission() != null) {
			filterList.retainAll(catalog.stream().filter(car -> car.getTransmission().equals(desiredCar.getTransmission())).collect(Collectors.toList()));
		}
		if (desiredCar.getBodyType() != null) {
			filterList.retainAll(catalog.stream().filter(car -> car.getBodyType().equals(desiredCar.getBodyType())).collect(Collectors.toList()));
		}
		if (desiredCar.getFuelType() != null) {
			filterList.retainAll(catalog.stream().filter(car -> car.getFuelType().equals(desiredCar.getFuelType())).collect(Collectors.toList()));
		}
		if (desiredCar.getColor() != null) {
			filterList.retainAll(catalog.stream().filter(car -> car.getColor().equals(desiredCar.getColor())).collect(Collectors.toList()));
		}
		List<Car> endList = new CarList();
		for (Car c : filterList) {
			if ((c.getMinprice() <= desiredCar.getMaxprice() || desiredCar.getMaxprice() == 0) && !c.getcarStatus()) {
				endList.add(c);
			}
		}
		
		return endList;
	}
}
