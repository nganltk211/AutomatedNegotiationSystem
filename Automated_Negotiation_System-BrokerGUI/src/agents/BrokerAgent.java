package agents;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gui.OfferConfirmationGUI;
import io.JsonIO;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;
import model.Car;
import model.CarList;
import model.MultipleMessage;
import model.NegotiationLog;
import model.NegotiationLogList;

/**
 * Class as representation of an broker-agent
 */
public class BrokerAgent extends Agent implements BrokerAgentInterface{

	private static final long serialVersionUID = -1539612606764155406L;
	private ObjectMapper o = new ObjectMapper(); // object supporting converting object to json-form
	private CarList catalog = new CarList(); // broker's catalog
	private JsonIO jsonDB = new JsonIO("./DataBase/JsonDB.txt");
	private JsonIO negotiationDB = new JsonIO("./DataBase/NegotiationDB.txt");
	private static final double COMMISION = 100; // fix-commission for each successful negotiation
	private double receivedCommision; // broker's commission from successful negotiations 
	private MultiAgentManager multiAgentMng; // deals with request from many buyers for a same car
	
	public BrokerAgent() {
		registerO2AInterface(BrokerAgentInterface.class, this);
	}
	
	public CarList getCatalog() {
		return catalog;
	}
	
	/**
	 * Method for setting up a broker agent
	 */
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Broker-agent " + getAID().getName() + " is ready.");
		jsonDB.clearFile(); // clear the data in file when restarting.
		//catalog = jsonDB.readFile();
		multiAgentMng = new MultiAgentManager(this);
		
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
	
	/**
	 * This Behavior (Receive confirmation from buyer agent/dealer) does the agent handshake after both agents accept offers
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
					String buyerName = msg0.getInReplyTo();
					System.out.println(buyerName);
					receivedCommision += COMMISION;
					System.out.println("\nBroker confirm !!! " + agentName + " confirm sell at price: " + offerPrice + "\nBroker Commision: " + receivedCommision);
					final double offer = offerPrice;
					offerPrice -= COMMISION;
					System.out.println("Price of the car after eliminating Broker commission: " + offerPrice);
					
					negotiationDB.openFileReader();
					// reads the negotiation session from the text file
					NegotiationLogList session = o.readValue(negotiationDB.readLine(), NegotiationLogList.class);
					negotiationDB.closeFileReader();
							
					// start the confirmation gui
					new Thread(() -> {
						Platform.runLater(() -> {
							OfferConfirmationGUI confirm = new OfferConfirmationGUI(offer, session, agentName, buyerName);
						});
					}).start();
					
					//Updates JsonDB/CarList
					for(Car c : catalog) {
						if(negotiatedCar.getCarId() == c.getCarId()) {
							c.setcarStatus(true);
						}
					}
					
					String jsonInString = o.writeValueAsString(catalog);
					jsonDB.clearFile();
					jsonDB.writeToFile(jsonInString);
					multiAgentMng.removeCarFromList(negotiatedCar); // remove the car from controlling list
					multiAgentMng.removeBuyerFromList(buyerName);
				} catch (IOException e) {
					e.printStackTrace();
				}								
			} else {
				block();
			}		
		}
	}
	

	/**
	 * This behavior of the broker agent is for receiving the list of cars from dealer agent and save it in his catalog.
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
				// update catalog/jsonFile
				jsonDB.writeToFile(carlist); 
				catalog = jsonDB.readFile();
				System.out.println("Broker: Broker cataloge: \n" + catalog);
			} else {
				block();
			}
		}
	}
	
	/**
	 * This behavior of the broker agent is for handling with a car request from the buyer.
	 */
	private class DealWithRequestFromBuyers extends CyclicBehaviour {

		@Override
		public void action() {
			//MessageTemplate for the Conversation between buyer and broker (the broker receive a car request from the buyer)
			MessageTemplate mt2 = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-broker-buyer"),
					MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			ACLMessage msg2 = myAgent.receive(mt2);
			if (msg2 != null) {
				// Message received. Process it
				String desiredCarJson = msg2.getContent();
				try {
					Car desiredCar = o.readValue(desiredCarJson, Car.class);
					System.out.println("Broker: Receive a request from Buyer:\n" + desiredCar + "\n");

					CarList listOfPossibleCar = new CarList();
					// get cars from catalog, which pass with buyer's demand
					listOfPossibleCar.addAll(getListOfPossibleCars(desiredCar)); 

					if (listOfPossibleCar.size() > 0) {
						// Send back a list of cars to the buyer 
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
					e1.printStackTrace();
				}
			} else {
				block();
			}
		}
	}
	
	/**
	 * This behavior of the broker agent is for connecting the buyer with dealer.
	 */
	private class RequestFromBuyersToConnectToDealer extends CyclicBehaviour {
		
		@Override
		public void action() {
			// MessageTemplate for the Conversation between buyer and broker 
			// (the broker receive a request from the buyer to connect with a dealer)
			
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-broker-buyer"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt); // Receive message (chosen car and first offer) from the buyer
			if (msg != null) {
				String buyerName = msg.getSender().getName();
				System.out.println("Broker: Receive a choosen car from buyer " + msg.getSender().getName());
				String choosenCarListJson = msg.getContent();
				String firstOfferPrice = msg.getReplyWith();
				try {
					CarList choosenCarList = o.readValue(choosenCarListJson, CarList.class);
					System.out.println(choosenCarList + "\n");
					// adds a car with interested buyer to the controlling list
					multiAgentMng.addBuyer(choosenCarList,buyerName, Double.parseDouble(firstOfferPrice));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				block();
			}
		}
		
	}
	
	/**
	 * This method is for filtering cars, which the buyer is looking for, from broker's catalog
	 * @param desiredCar
	 * @return list of car after filtering
	 */
	private List<Car> getListOfPossibleCars(Car desiredCar){
		List<Car> filterList = new CarList();
		filterList.addAll(catalog); // get all cars from broker's catalog
		if (desiredCar.getManufacture() != null) {	
			// retain only cars, which have the same manufacture with desiredCar
			filterList.retainAll(catalog.stream().filter(car -> car.getManufacture().equals(desiredCar.getManufacture())).collect(Collectors.toList()));
		}
		if (desiredCar.getModel() != null) {
			// retain only cars, which have the same model with desiredCar
			filterList.retainAll(catalog.stream().filter(car -> car.getModel().equals(desiredCar.getModel())).collect(Collectors.toList()));
		}
		if (desiredCar.getTransmission() != null) {
			// retain only cars, which have the same transmission with desiredCar
			filterList.retainAll(catalog.stream().filter(car -> car.getTransmission().equals(desiredCar.getTransmission())).collect(Collectors.toList()));
		}
		if (desiredCar.getBodyType() != null) {
			// retain only cars, which have the same body type with desiredCar
			filterList.retainAll(catalog.stream().filter(car -> car.getBodyType().equals(desiredCar.getBodyType())).collect(Collectors.toList()));
		}
		if (desiredCar.getFuelType() != null) {
			// retain only cars, which have the same fuel type with desiredCar
			filterList.retainAll(catalog.stream().filter(car -> car.getFuelType().equals(desiredCar.getFuelType())).collect(Collectors.toList()));
		}
		if (desiredCar.getColor() != null) {
			// retain only cars, which have the same color with desiredCar
			filterList.retainAll(catalog.stream().filter(car -> car.getColor().equals(desiredCar.getColor())).collect(Collectors.toList()));
		}
		List<Car> endList = new CarList();
		for (Car c : filterList) {
			// only get the cars, which has price lower than buyer max price or get all cars when the buyer didn't give a max price.
			// another condition: only get not sold cars
			if ((c.getMinprice() <= desiredCar.getMaxprice() || desiredCar.getMaxprice() == 0) && !c.getcarStatus()) {
				endList.add(c);
			}
		}
		
		return endList;
	}
	
	/**
	 * Method for sending a list of interested in a same car buyers to a dealer
	 * (this method will be call when the broker click the menu item "Send request" or on the button "Send all requests")
	 */
	@Override
	public void sendBuyerListDataToDealer(MultipleMessage message) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				try {		
					// Send message (chosen car, first offer price, name of the buyer agent) to the dealer, who offer the chosen car
					ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
					mess.addReceiver(AgentSupport.findAgentWithName(myAgent, message.getCar().getAgent()));
					mess.setContent(o.writeValueAsString(message));
					mess.setConversationId("car-trade-broker-seller");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}		
			}	
		});
	}

	@Override
	public MultiAgentManager getMultiAgentManager() {
		return multiAgentMng;
	}
	
	@Override
	public double getCommision() {
		return receivedCommision;
	}
}
