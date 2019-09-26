package agents;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gui.DealerGUI;
import gui.NegotiationBotGUI;
import gui.NegotiationChoiceGUI;
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
import model.Negotiation;

public class DealerAgent extends Agent {

	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private ObjectMapper o = new ObjectMapper();
	private int negotiationOption = 0; // 0 for manual negotiation and 1 for automated negotiation
	private double intialPrice = 15200; // max price
	private double reservationPrice = 13000; // min price
	private int maxStep = 30;
	
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Dealer-agent " + getAID().getName() + " is ready.");

		// starts the GUI
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
					DFAgentDescription[] result = DFService.search(myAgent, template);
					brokerAgent = result[0].getName();
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
		addBehaviour(new StartTheNegotiationWithBuyer());
		addBehaviour(new NegotiationWithBuyer());
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
	 * The dealer agent sent a list of cars to the broker
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
				// to set information that these cars are offered from this dealer agent
				for (Car c : carList) {
					c.setAgent(myAgent.getName());
				}
				String jsonInString;
				try {
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

	private class StartTheNegotiationWithBuyer extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-trade-broker-seller"),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println("Dealer: Receive an offer from the broker");
				String content = msg.getContent();
				String buyer = msg.getReplyWith();
				try {
					Car choosenCars = o.readValue(content, Car.class);
					System.out.println(choosenCars + "\n");
					System.out.println("Dealer: Trying to create a negotiation with the buyer: " + buyer);
					// starts the Negotiation GUI, where the dealer can choose,
					// whether he want to negotiate manually or automated
					new Thread(() -> {
						Platform.runLater(() -> {
							//for (Car c : choosenCars) {
								NegotiationChoiceGUI gui = new NegotiationChoiceGUI(myAgent, buyer, choosenCars);
							//}
						});
					}).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class NegotiationBehaviourWithBuyerFirstOffer extends OneShotBehaviour {
		private String buyerAgentName;
		private Car negotiatedCar;
		private double offerPrice;

		public NegotiationBehaviourWithBuyerFirstOffer(String opponentAgentName, Car car, double firstOfferPrice) {
			buyerAgentName = opponentAgentName;
			negotiatedCar = car;
			this.offerPrice = firstOfferPrice;
		}

		@Override
		public void action() {
			ACLMessage mess = new ACLMessage(ACLMessage.PROPOSE);
			mess.addReceiver(AgentSupport.findAgentWithName(myAgent, buyerAgentName));
			if (negotiationOption == 1) {
				//offerPrice = Algorithms.offer(intialPrice, reservationPrice, 1, maxStep, 1.1);
				offerPrice = intialPrice;
			}
			System.out.println(myAgent.getName() + ": First offer to the buyer: " + offerPrice);
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
		}
	}

	/**
	 * This behavior is for the negotiation with the buyer.
	 * In case of manual negotiation, a GUI will be shown with the offer price from the buyer and options for buyer to accept or decline the offer 
	 * In case of automated negotiation .....
	 */
	private class NegotiationWithBuyer extends CyclicBehaviour {
		private int step = 1;
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-negotiation"),
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				if (negotiationOption == 0) {
					// for manual negotiation: receive offer from the buyer and start the GUI for the dealer to make a counter-offer
					String content = msg.getContent();
					try {
						Car negotiatedCar = o.readValue(content, Car.class);
						String buyerName = msg.getSender().getName();
						double offerPrice = Double.parseDouble(msg.getReplyWith());
						System.out.println("Dealer: Receive offer from the buyer: " + offerPrice);
						new Thread(() -> {
							Platform.runLater(() -> {
								NegotiationBotGUI bot = new NegotiationBotGUI(myAgent, buyerName, negotiatedCar,
										offerPrice);
							});
						}).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// for automated Negotiation: receive offer from the buyer and decide to accept or make a counter-offer
					if (step <= maxStep) {
						String content = msg.getContent();
						try {
							Car messObject = o.readValue(content, Car.class);
							String buyerName = msg.getSender().getName();
							double offerPrice = Double.parseDouble(msg.getReplyWith());
							System.out.println("Dealer: Receive offer from the buyer: " + offerPrice);
							double nextPrice = Algorithms.offer(intialPrice, reservationPrice, step, maxStep, 0.9);
							if (nextPrice <= offerPrice) {
								acceptOffer(buyerName, messObject, offerPrice);
								step = 1;
							} else {
								makeACounterOffer(buyerName, messObject, nextPrice);
								step++;
							}
							System.out.println(step);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						endTheNegotiationBecauseOfOutOfTime();
						step = 1;
					}
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
					System.out.println("End of the negotiation : ");
					System.out.println("Sold car: " + negotiatedCar);
					System.out.println("Sold price: " + offerPrice);
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
				System.out.println(myAgent.getName() + ": Counter offer to the buyer: " + price);
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
	 * This method will be called, when the dealer accepts the offer from the buyer
	 * 
	 * @param opponentAgentName
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
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void endTheNegotiationBecauseOfOutOfTime() {
		System.out.println("No Agreement!");
	}

	public void startNegotiation(String opponentAgentName, Car car, double firstOfferPrice) {
		addBehaviour(new NegotiationBehaviourWithBuyerFirstOffer(opponentAgentName, car, firstOfferPrice));
	}

	public void setNegotiationChoice(int option) {
		negotiationOption = option;
	}

}