package agents;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gui.DealerGUI;
import gui.NegotiationBotGUI;
import jade.core.AID;
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

/**
 * Class as representation of an dealer agent
 */
public class DealerAgent extends Agent {

	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private ObjectMapper o = new ObjectMapper(); // for converting in json-format
	private Map<String, Double> lastOfferList; // list to store the buyer name with his lastOffer
	private int numberOfBuyers; // number of buyer, who want to buy a same car
	private boolean multiple = false; // true if more than one buyer want to negotiate a car
	private boolean bestBuyerFound = false; // true if the beast buyer is found
	
	/**
	 * Method for setting up a buyer agent
	 */
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Dealer-agent " + getAID().getName() + " is ready.");
		lastOfferList = new Hashtable<>();
		// starts the DealerGUI
		new Thread(() -> {
			Platform.runLater(() -> {
				DealerGUI guiDealer = new DealerGUI(this);
			});
		}).start();

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();

		// using to find the AID of the broker agent
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				sd.setType("car-broker");
				template.addServices(sd);
				try {
					// search agent with service "car-broker"
					DFAgentDescription[] result = DFService.search(myAgent, template);
					brokerAgent = result[0].getName();
				} catch (FIPAException fe) {
					System.err.println("Problem while searching an agent by its public service ");
				}
			}
		});

		// Adding behaviors to the dealer agent
		addBehaviour(new StartTheNegotiationWithBuyer());
		addBehaviour(new NegotiationWithBuyer());
		addBehaviour(new EndTheNegotiation());
		addBehaviour(new GetRefuseMessageFromTheBuyer());
	}

	/**
	 * Method for the Agent clean-up
	 */
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Dealer-agent " + getAID().getName() + " terminating.");
	}

	/**
	 * The dealer agent sent a list of cars he want to sell to the broker
	 * 
	 * @param carList
	 */
	public void sendListOfCarToBroker(CarList carList) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println("Dealer : Trying to send a list of cars to Broker\n");
				ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
				mess.addReceiver(brokerAgent);
				// to set information of dealer agent to the offered cars
				for (Car c : carList) {
					c.setAgent(myAgent.getName());
				}
				String jsonInString;
				try {
					// sends list of cars to the broker
					jsonInString = o.writeValueAsString(carList);
					mess.setContent(jsonInString);
					mess.setConversationId("car-trade-dealer-broker");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	// ------------------------------------------------------------------------------------------------------------
	// Negotiation part

	/**
	 * This behavior is for receiving an inform from the broker to start a
	 * negotiation with a buyer
	 */
	private class StartTheNegotiationWithBuyer extends CyclicBehaviour {

		@Override
		public void action() {
			// MessageTemplate for the Conversation between dealer and broker
			// (dealer receive inform from the broker to start a negotiation with a buyer)
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-broker-seller"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println("Dealer: Receive an offer from the broker");
				// Receives the message (negotiated car, buyer name, first-offer price from
				// buyer)
				String content = msg.getContent();
				MultipleMessage messg;
				try {
					messg = o.readValue(content, MultipleMessage.class);
					Map<String, Double> buyerList = messg.getBuyerList();
					numberOfBuyers = buyerList.keySet().size(); // number of interested buyers
					if (numberOfBuyers > 1) {
						multiple = true;
					}
					Car choosenCars = messg.getCar();
					System.out.println(choosenCars + "\n");
					System.out.println("Dealer: Trying to create a negotiation with following buyer: ");
					for (String key : buyerList.keySet()) {
						System.out.println(key);
					}
					// starting to make an first offer to the buyer
					addBehaviour(new NegotiationBehaviourWithBuyerFirstOffer(messg));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * This behavior is for making the buyer an first offer
	 */
	private class NegotiationBehaviourWithBuyerFirstOffer extends OneShotBehaviour {
		private Map<String, Double> buyerList;
		private Car negotiatedCar;

		public NegotiationBehaviourWithBuyerFirstOffer(MultipleMessage brokerMessage) {
			buyerList = brokerMessage.getBuyerList();
			negotiatedCar = brokerMessage.getCar();
		}

		@Override
		public void action() {
			for (String buyerName : buyerList.keySet()) {
				ACLMessage mess = new ACLMessage(ACLMessage.PROPOSE);
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, buyerName));
				// This is automated negotiation
				if (!negotiatedCar.getisNegotiatable()) {
					double offerPrice = negotiatedCar.getMaxprice();
					System.out.println(myAgent.getName() + ": First offer to the buyer: " + offerPrice + "\n");
					String jsonInString;
					try {
						jsonInString = o.writeValueAsString(negotiatedCar);
						mess.setContent(jsonInString);
						mess.setReplyWith(String.valueOf(offerPrice));
						mess.setConversationId("car-negotiation");
						mess.setInReplyTo("0"); // time-step
						mess.setPostTimeStamp(System.currentTimeMillis());
						myAgent.send(mess);
					} catch (JsonProcessingException e) {
						System.err.println("Problem by converting an object o json-format");
					}
				} else {
					// manual negotiation
					double offerPrice = buyerList.get(buyerName);
					new Thread(() -> {
						Platform.runLater(() -> {
							NegotiationBotGUI bot = new NegotiationBotGUI(myAgent, buyerName, negotiatedCar,
									offerPrice, 0);
						});
					}).start();
				}
			}
		}
	}

	/**
	 * This behavior is for the negotiation with the buyer. In case of manual
	 * negotiation, a GUI will be shown with the offer price from the buyer and
	 * options for buyer to accept or decline the offer. In case of automated
	 * negotiation the dealer AI will decide to accept the offer or make a
	 * counter-offer
	 */
	private class NegotiationWithBuyer extends CyclicBehaviour {
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-negotiation"),
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String content = msg.getContent();
				Car messObject;
				try {
					messObject = o.readValue(content, Car.class);
					if (messObject.getisNegotiatable()) {
						// for manual negotiation: receive offer from the buyer and start the GUI for
						// the dealer to make a counter-offer
						String buyerName = msg.getSender().getName();
						double offerPrice = Double.parseDouble(msg.getReplyWith());
						int step = Integer.parseInt(msg.getInReplyTo());
						System.out.println("Dealer: Receive offer from the buyer: " + offerPrice);
						// start the NegotiationBotGUI
						new Thread(() -> {
							Platform.runLater(() -> {
								NegotiationBotGUI bot = new NegotiationBotGUI(myAgent, buyerName, messObject,
										offerPrice, step);
							});
						}).start();
					} else {
						// for automated Negotiation: receive offer from the buyer and decide to accept
						// or make a counter-offer
						int stepD = Integer.parseInt(msg.getInReplyTo()) + 1;
						String buyerName = msg.getSender().getName();
						if (stepD <= messObject.getSteps()) {
							double offerPrice = Double.parseDouble(msg.getReplyWith());
							System.out.println("Dealer: Receive offer from the buyer: " + offerPrice);
							int nextPrice = Algorithms.offer(messObject.getMaxprice(), messObject.getMinprice(), stepD,
									messObject.getSteps(), messObject.getBeeta());
							if (nextPrice <= offerPrice) {
								// accept the offer from the buyer
								if (!multiple) {
									acceptOffer(buyerName, messObject, offerPrice);
								} else {
									findTheBestOffer(buyerName, messObject, offerPrice);
								}	
							} else {
								// make a counter-offer to the buyer
								makeACounterOffer(buyerName, messObject, nextPrice, stepD);
							}
						} else {
							// when reaching the deadline
							endTheNegotiationBecauseOfOutOfStep(buyerName);
							findTheBestOffer(buyerName, messObject, 0.0); // add this buyer to the list
						}
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
	 * The dealer agent makes a counter-offer to the buyer agent
	 *
	 * @param opponentAgentName
	 *            : name of the buyer agent
	 * @param negotiatedCar
	 * @param price:
	 *            offer-price
	 * @param step: dealer-step
	 */
	public void makeACounterOffer(String opponentAgentName, Car negotiatedCar, double price, int step) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.PROPOSE);
				System.out.println(myAgent.getName() + ": Counter offer to the buyer: " + price + "\n");
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, opponentAgentName));
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setReplyWith(String.valueOf(price));
					mess.setInReplyTo(String.valueOf(step));
					mess.setConversationId("car-negotiation");
					mess.setPostTimeStamp(System.currentTimeMillis());
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	/**
	 * Send negotiation confirmation message to broker agent
	 * 
	 * @param car
	 *            : negotiated car
	 * @param price
	 *            : accepted price
	 */
	private void confirmSell(Car car, double price) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
				System.out.println(myAgent.getName() + " : Confirm  car sell with broker: " + price);
				mess.addReceiver(brokerAgent);
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(car);
					mess.setContent(jsonInString);
					mess.setReplyWith(String.valueOf(price));
					mess.setConversationId("confirm_sell");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}

			}
		});
	}

	/**
	 * This method will be called, when the dealer accepts the offer from the buyer
	 *
	 * @param opponentAgentName
	 *            : buyer agent name
	 * @param negotiatedCar
	 * @param price
	 */
	public void acceptOffer(String opponentAgentName, Car negotiatedCar, double price) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				System.out.println(myAgent.getName() + ": Accept offer from the buyer: " + opponentAgentName + " at price: " + price);
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, opponentAgentName));
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setReplyWith(String.valueOf(price));
					mess.setConversationId("car-negotiation");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	/**
	 * Method to find the buyer (the one who is willing to pay the most)
	 * @param opponentAgentName : buyer name
	 * @param negotiatedCar 
	 * @param price : offer price
	 */
	public void findTheBestOffer(String opponentAgentName, Car negotiatedCar, double price) {
		lastOfferList.put(opponentAgentName, price); // add to the list
		// when all best offers of each buyer are calculated
		if (lastOfferList.size() == numberOfBuyers) {			 
			 Map.Entry<String, Double> entry = lastOfferList.entrySet().iterator().next();
			 String bestBuyer= entry.getKey();
			 double bestOffer= entry.getValue();
			 for (Entry<String, Double> element : lastOfferList.entrySet()) {
				// find the best buyer by comparing the last offer price
				 if (element.getValue() > bestOffer) { 
					 bestOffer = element.getValue();
					 bestBuyer = element.getKey();
				 }
			 }
			 if (bestOffer != 0) { // when a best buyer is found
				 System.out.println("Dealer: Best buyer: " + bestBuyer + " with offer: " + bestOffer);
				 // send accept message to the best buyer
				 acceptOffer(bestBuyer,negotiatedCar,bestOffer);	
				 bestBuyerFound = true;
				// remove the best buyer from the list
				 lastOfferList.remove(bestBuyer, bestOffer); 
			 }		 
			 // send reject message to remaining buyers
			 for (Entry<String, Double> element : lastOfferList.entrySet()) {
				 if (element.getValue() != 0) {
					 reachNoAgreement(element.getKey());
				 }		 
			 }
			 lastOfferList.clear();
		}	
	}
	
	/**
	 * Behavior to send a refuse request to the buyer in case no agreement is
	 * reached
	 * 
	 * @param opponentAgentName:
	 *            buyer agent name
	 */
	public void reachNoAgreement(String opponentAgentName) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.REFUSE);
				System.out.println(myAgent.getName() + ": Can not offer the car to " + opponentAgentName);
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, opponentAgentName));
				mess.setContent("No Agreement");
				mess.setConversationId("car-negotiation-refuse");
				myAgent.send(mess);
			}
		});
	}

	/**
	 * This behavior is for receiving refuse message from the dealer in case of no agreement
	 */
	private class GetRefuseMessageFromTheBuyer extends CyclicBehaviour {

		@Override
		public void action() {
			// Define template of the received message, which need to be matched to the sent
			// message from the dealer
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-negotiation-refuse"),
					MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String content = msg.getContent();
				try {
					Car negotiatedCar = o.readValue(content, Car.class);
					findTheBestOffer(msg.getSender().getName(), negotiatedCar, 0.0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else {
				block();
			}
		}
	}
	
	/**
	 * This behavior will be executed, when the negotiation is at the end, which
	 * means that the buyer accept the offer from the dealer.
	 */
	private class EndTheNegotiation extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-negotiation"),
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String content = msg.getContent();
				try {
					Car negotiatedCar = o.readValue(content, Car.class);
					double offerPrice = Double.parseDouble(msg.getReplyWith());
					if (!multiple || bestBuyerFound) {
						System.out.println("\nEnd of the negotiation : ");
						System.out.println("Sold car: " + negotiatedCar);
						System.out.println("Sold price: " + offerPrice);		
						bestBuyerFound = false;
						confirmSell(negotiatedCar, offerPrice); // confirm with the broker
					} else {
						// add the buyer to the list for finding the best buyer
						findTheBestOffer(msg.getSender().getName(), negotiatedCar, offerPrice);
					}
				} catch (IOException e) {
					System.err.println("Problem by converting a json-format to an object");
				}
			} else {
				block();
			}
		}
	}
	
	/**
	 * Method for ending the negotiation because of out of time.
	 */
	public void endTheNegotiationBecauseOfOutOfStep(String buyerName) {
		reachNoAgreement(buyerName);
	}

}
