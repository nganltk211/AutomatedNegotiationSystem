package agents;

import java.io.IOException;

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

/**
 * Class as representation of an dealer agent
 */
public class DealerAgent extends Agent {

	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private ObjectMapper o = new ObjectMapper();
	private NegotiationWithBuyer nb;

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Dealer-agent " + getAID().getName() + " is ready.");

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
					fe.printStackTrace();
				}
			}
		});

		// Adding behaviors to the dealer agent
		addBehaviour(new StartTheNegotiationWithBuyer());
		nb = new NegotiationWithBuyer();
		addBehaviour(nb);
		addBehaviour(new EndTheNegotiation());
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
					e.printStackTrace();
				}
			}
		});
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	// Negotiation part

	/**
	 * This behavior is for receiving an inform from the broker to start a negotiation with a buyer
	 */
	private class StartTheNegotiationWithBuyer extends CyclicBehaviour {

		@Override
		public void action() {
			//MessageTemplate for the Conversation between dealer and broker
			// (dealer receive inform from the broker to start a negotiation with a buyer)
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-broker-seller"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println("Dealer: Receive an offer from the broker");
				// Receives the message (negotiated car, buyer name, first-offer price from buyer)
				String content = msg.getContent();
				String buyer = msg.getReplyWith();
			    double offerPrice = Double.parseDouble(msg.getInReplyTo());
				try {
					Car choosenCars = o.readValue(content, Car.class);
					System.out.println(choosenCars + "\n");
					System.out.println("Dealer: Trying to create a negotiation with the buyer: " + buyer);
					// starting to make an first offer to the buyer
					addBehaviour(new NegotiationBehaviourWithBuyerFirstOffer(buyer, choosenCars, offerPrice));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This behavior is for making the buyer an first offer
	 */
	private class NegotiationBehaviourWithBuyerFirstOffer extends OneShotBehaviour {
		private String buyerAgentName;
		private Car negotiatedCar;
		private double offerPrice;

		public NegotiationBehaviourWithBuyerFirstOffer(String opponentAgentName, Car car, double firstOfferPrice) {
			buyerAgentName = opponentAgentName;
			negotiatedCar = car;
			this.offerPrice = firstOfferPrice;
			//Add this session in to the MultiAgent management list
		}

		@Override
		public void action() {
			ACLMessage mess = new ACLMessage(ACLMessage.PROPOSE);
			mess.addReceiver(AgentSupport.findAgentWithName(myAgent, buyerAgentName));
			//This is automate negotiation
			if (!negotiatedCar.getisNegotiatable()) {
				offerPrice = negotiatedCar.getMaxprice();
				System.out.println(myAgent.getName() + ": First offer to the buyer: " + offerPrice + "\n");
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setReplyWith(String.valueOf(offerPrice));
					mess.setConversationId("car-negotiation");

					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}else {
				// manual negotiation
				new Thread(() -> {
					Platform.runLater(() -> {
						NegotiationBotGUI bot = new NegotiationBotGUI(myAgent, buyerAgentName, negotiatedCar, offerPrice);
					});
				}).start();
			}
		}
	}

	/**
	 * This behavior is for the negotiation with the buyer.
	 * In case of manual negotiation, a GUI will be shown with the offer price from the buyer and
	 * options for buyer to accept or decline the offer.
	 * In case of automated negotiation the dealer AI will decide to accept the offer or make a counter-offer
	 */
	private class NegotiationWithBuyer extends CyclicBehaviour {
		private int step = 1;

		public void setStep(int step) {
			this.step = step;
		}

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
						System.out.println("Dealer: Receive offer from the buyer: " + offerPrice);
						// start the NegotiationBotGUI
						new Thread(() -> {
							Platform.runLater(() -> {
								NegotiationBotGUI bot = new NegotiationBotGUI(myAgent, buyerName, messObject,
										offerPrice);
							});
						}).start();
					} else {
						// for automated Negotiation: receive offer from the buyer and decide to accept
						// or make a counter-offer
						String buyerName = null;
						if (step <= messObject.getSteps()) {
							buyerName = msg.getSender().getName();
							double offerPrice = Double.parseDouble(msg.getReplyWith());
							System.out.println("Dealer: Receive offer from the buyer: " + offerPrice);
							int nextPrice = Algorithms.offer(messObject.getMaxprice(), messObject.getMinprice(),
									step, messObject.getSteps(), messObject.getBeeta());
							if (nextPrice <= offerPrice) {
								// accept the offer from the buyer
								acceptOffer(buyerName, messObject, offerPrice);
								step = 1;
							} else {
								// make a counter-offer to the buyer
								makeACounterOffer(buyerName, messObject, nextPrice);
								step++;
							}
						} else {
							// when reaching the deadline
							endTheNegotiationBecauseOfOutOfTime();
							step = 1;
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
					System.out.println("\nEnd of the negotiation : ");
					System.out.println("Sold car: " + negotiatedCar);
					System.out.println("Sold price: " + offerPrice);

					nb.setStep(1); // set the step to 1
				} catch (IOException e) {
					e.printStackTrace();
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
	 */
	public void makeACounterOffer(String opponentAgentName, Car negotiatedCar, double price) {
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
					mess.setConversationId("car-negotiation");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Send negotiation confirmation message to broker agent
	 * @param car : negotiated car
	 * @param price : accepted price
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
					e.printStackTrace();
				}

			}
		});
	}

	/**
	 * This method will be called, when the dealer accepts the offer from the buyer
	 *
	 * @param opponentAgentName : buyer agent name
	 * @param negotiatedCar
	 * @param price
	 */
	public void acceptOffer(String opponentAgentName, Car negotiatedCar, double price) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				System.out.println(myAgent.getName() + ": Accept offer from the buyer: " + price);
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, opponentAgentName));
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setReplyWith(String.valueOf(price));
					mess.setConversationId("car-negotiation");
					myAgent.send(mess);
					confirmSell(negotiatedCar, price);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Method for ending the negotiation because of out of time.
	 */
	public void endTheNegotiationBecauseOfOutOfTime() {
		System.out.println("No Agreement!");
	}

}
