package agents;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import model.Car;
import model.CarList;

public class BrokerAgent extends Agent {

	private static final long serialVersionUID = -1539612606764155406L;
	private ObjectMapper o = new ObjectMapper();
	private CarList catalog = new CarList();

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Broker-agent " + getAID().getName() + " is ready.");

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
	}

	/**
	 * Method for the Agent clean-up
	 */
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Broker-agent " + getAID().getName() + " terminating.");
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
			MessageTemplate mt1 = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg1 = myAgent.receive(mt1);

			if (msg1 != null) {
				// Message received. Process it
				String carlist = msg1.getContent();
				try {
					//convert the list of cars in Json-form to the Object CarList
					CarList list = o.readValue(carlist, CarList.class);
					catalog.addAll(list);
					System.out.println("Broker cataloge: \n" + catalog);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				block();
			}
		}
	}
	
	private class DealWithRequestFromBuyers extends CyclicBehaviour {

		private static final long serialVersionUID = -8496805180917867030L;

		@Override
		public void action() {
			MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade"),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg2 = myAgent.receive(mt2);
			
			if (msg2 != null) {
				// Message received. Process it
				String desiredCarJson = msg2.getContent();
				try {
					Car desiredCar = o.readValue(desiredCarJson, Car.class);
					System.out.println("Receive a request from Buyer:\n" + desiredCar);

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
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						ACLMessage reply = msg2.createReply();
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setReplyWith("performative");
						reply.setContent("There are no possible car to offer! Sorry! ");
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
	
	private List<Car> getListOfPossibleCars(Car desiredCar){
		List<Car> filterList = new CarList();
		filterList.addAll(catalog);
		if (!desiredCar.getManufacture().trim().equals("")) {			
			filterList.retainAll(catalog.stream().filter(car -> car.getManufacture().equals(desiredCar.getManufacture())).collect(Collectors.toList()));
		}
		if (!desiredCar.getModel().trim().equals("")) {
			filterList.retainAll(catalog.stream().filter(car -> car.getModel().equals(desiredCar.getModel())).collect(Collectors.toList()));
		}
		if (!desiredCar.getTransmission().trim().equals("")) {
			filterList.retainAll(catalog.stream().filter(car -> car.getTransmission().equals(desiredCar.getTransmission())).collect(Collectors.toList()));
		}
		if (!desiredCar.getBodyType().trim().equals("")) {
			filterList.retainAll(catalog.stream().filter(car -> car.getBodyType().equals(desiredCar.getBodyType())).collect(Collectors.toList()));
		}
		if (!desiredCar.getFuelType().trim().equals("")) {
			filterList.retainAll(catalog.stream().filter(car -> car.getFuelType().equals(desiredCar.getFuelType())).collect(Collectors.toList()));
		}
		if (!desiredCar.getColor().trim().equals("")) {
			filterList.retainAll(catalog.stream().filter(car -> car.getColor().equals(desiredCar.getColor())).collect(Collectors.toList()));
		}
		
		for (Car c : filterList) {
			if (c.getPrice() > desiredCar.getPrice()) {
				filterList.remove(c);
			}
		}
		return filterList;
	}
}
