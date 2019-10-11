package agents;

import java.io.IOException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gui.BuyerGUI;
import gui.CarListToBuyerGUI;
import gui.NegotiationBotGUI;
import gui.NoAgreementGUI;
import gui.NoOffersGUI;
import io.JsonIO;
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
import model.Log;
import model.Negotiation;

/**
 * Class as representation of an buyer agent
 */
public class BuyerAgent extends Agent {

	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private boolean manualNegotiation; // true if the negotiation is manual
	private double intialPrice; // min price
	private double reservationPrice; // max price
	private int maxStep; // deadline : max time step
	private double beetaValue; // beetaValue using time dependent tactics
	private NegotiationWithDealer nd;

	private ObjectMapper o = new ObjectMapper();
	private JsonIO negotiationDB = new JsonIO("./DataBase/NegotiatioDB.txt");
	private ArrayList<Log> buyerLogs = new ArrayList<Log>();
	private ArrayList<Log> dealerLogs = new ArrayList<Log>();

	public double getIntialPrice() {
		return intialPrice;
	}

	public void setIntialPrice(double intialPrice) {
		this.intialPrice = intialPrice;
	}

	public double getReservationPrice() {
		return reservationPrice;
	}

	public void setReservationPrice(double reservationPrice) {
		this.reservationPrice = reservationPrice;
	}

	public int getMaxStep() {
		return maxStep;
	}

	public void setMaxStep(int maxStep) {
		this.maxStep = maxStep;
	}

	public double getBeetavalue() {
		return beetaValue;
	}

	public void setBeetavalue(double beetavalue) {
		this.beetaValue = beetavalue;
	}

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Buyer-agent " + getAID().getName() + " is ready.");

		// starts the BuyerGUI
		new Thread(() -> {
			Platform.runLater(() -> {
				BuyerGUI guiBuyer = new BuyerGUI(this);
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
					System.err.println("Problem while searching an agent by its public service ");
				}
			}
		});

		nd = new NegotiationWithDealer();
		// Adding behaviors to the buyer agent
		addBehaviour(new OfferFromBroker());
		addBehaviour(nd);
		addBehaviour(new EndTheNegotiation());
		addBehaviour(new NoAgreementFromDealer());
	}

	/**
	 * Method for the Agent clean-up
	 */
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
	}

	/**
	 * The buyer agent is able to receive the list of cars, which match to his
	 * demand from the broker.
	 */
	private class OfferFromBroker extends CyclicBehaviour {

		@Override
		public void action() {
			// Define template of the received message, which need to be matched to the sent
			// message from the broker
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-offer"),
					MessageTemplate.MatchReplyWith("performative"));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String content = msg.getContent();
				// Using performative of the received message to specify the sent message from
				// the broker
				switch (msg.getPerformative()) {
				case ACLMessage.INFORM:
					// When the broker can find cars, which match to the demand of the buyer
					System.out.println("Buyer: There are following possible offers for you:");
					try {
						CarList offerCarlist = o.readValue(content, CarList.class);
						System.out.println(offerCarlist);
						// Start the GUI to show the list of possible cars to the buyer
						new Thread(() -> {
							Platform.runLater(() -> {
								CarListToBuyerGUI listCarGUI = new CarListToBuyerGUI(offerCarlist, myAgent);
							});
						}).start();
					} catch (IOException e1) {
						System.err.println("Problem by converting a json-format to an object");
					}
					break;
				case ACLMessage.REFUSE:
					// When the broker find no matching car
					System.out.println(content);
					new Thread(() -> {
						Platform.runLater(() -> {
							NoOffersGUI noOffer = new NoOffersGUI(myAgent);
						});
					}).start();
				}
			} else {
				block();
			}
		}
	}

	/**
	 * This method will be called after the buyer select the cars he want to
	 * negotiate. A list of chosen cars will be sent to the broker
	 * 
	 * @param listOfChosenCars
	 */
	public void sendBackTheChoosenCarsToTheBroker(Car negotiatedCar, double firstOfferPrice) {
		if (manualNegotiation) {
			// Adding Buyers logs into list
			Log blog = new Log(0, beetaValue, firstOfferPrice);
			buyerLogs.add(blog);
		}
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println("Trying to send a choosen car to the broker\n");
				ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
				mess.addReceiver(brokerAgent);
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setConversationId("car-trade-broker-buyer");
					mess.setReplyWith(String.valueOf(firstOfferPrice));
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	/**
	 * To request the broker to send a list of possible cars
	 * 
	 * @param desiredCar
	 */
	public void requestInfoOfDesiredCar(Car desiredCar) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println("Buyer: Trying to send a car to the broker:\n");
				ACLMessage mess = new ACLMessage(ACLMessage.REQUEST);
				mess.addReceiver(brokerAgent);

				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(desiredCar);
					mess.setContent(jsonInString);
					mess.setConversationId("car-trade-broker-buyer");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	// -----------------------------------------------------------------------------------------------------------------------------------------
	// Negotiation part

	/**
	 * This behavior is for the negotiation with dealer. In case of manual
	 * negotiation, a GUI will be shown with the offer price from the dealer and
	 * options for buyer to accept or decline the offer. In case of automated
	 * negotiation, the buyer AI will decide to accept the offer or make a
	 * counter-offer
	 */
	private class NegotiationWithDealer extends CyclicBehaviour {
		private int step = 0;
		// Session log

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
				try {
					Car messObject = o.readValue(content, Car.class);
					String dealerName = msg.getSender().getName();
					double offerPrice = Double.parseDouble(msg.getReplyWith());
					String dealerTimeStep = msg.getInReplyTo();
					int dealerSteps = Integer.parseInt(dealerTimeStep);
					// adds to dealer log
					Log dlog = new Log(dealerSteps, messObject.getBeeta(), offerPrice);
					dealerLogs.add(dlog);
					if (manualNegotiation) {
						// for manual negotiation
						System.out.println("Buyer: Receive offer from the dealer: " + offerPrice);
						// start the NegotiationBotGUI
						new Thread(() -> {
							Platform.runLater(() -> {
								NegotiationBotGUI bot = new NegotiationBotGUI(myAgent, dealerName, messObject,
										offerPrice, dealerSteps + 1); // for manual, the step is used for both agents
							});
						}).start();
					} else {
						// for automated Negotiation: AI part
						if (step <= maxStep) {
							System.out.println("Buyer: Receive offer from the dealer: " + offerPrice);
							// calculate the next offer
							int nextPrice = Algorithms.offer(intialPrice, reservationPrice, step, maxStep, beetaValue);
							if (nextPrice >= offerPrice) {
								step = 0;
								acceptOffer(dealerName, messObject, offerPrice);
							} else {
								makeACounterOffer(dealerName, messObject, nextPrice, dealerTimeStep, step);
								step++;
							}
						} else {
							step = 0;
							endTheNegotiationWithoutAgreement();
							sendRefuseToTheDealer(dealerName, messObject);
						}
					}
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
	 * means that the dealer accept the offer from the buyer.
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
					String dealerName = msg.getSender().getName();
					acceptOffer(dealerName, negotiatedCar, offerPrice);
//					saveLogs(dealerName);// Send logs to Dealer
//					System.out.println("\nEnd of the negotiation : ");
//					System.out.println("Bought car: " + negotiatedCar);
//					System.out.println("Bought price: " + offerPrice);
					nd.setStep(0);
				} catch (IOException e) {
					System.err.println("Problem by converting a json-format to an object");
				}
			} else {
				block();
			}
		}
	}

	/**
	 * The buyer agent makes a counter-offer to the dealer agent
	 * 
	 * @param opponentAgentName
	 *            : name of the dealer agent
	 * @param negotiatedCar
	 * @param price:
	 *            offer-price
	 */
	public void makeACounterOffer(String opponentAgentName, Car negotiatedCar, double price, String dealerTimeStep, int buyerStep) {
		// Adding Buyers logs into list
		Log blog = new Log(buyerStep, beetaValue, price);
		buyerLogs.add(blog);
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.PROPOSE);
				System.err.println(myAgent.getName() + ": Counter offer to the dealer: " + price + "\n");
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, opponentAgentName));

				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setReplyWith(String.valueOf(price));
					mess.setConversationId("car-negotiation");
					mess.setInReplyTo(dealerTimeStep);
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	/**
	 * This method will be called, when the buyer accepts the offer from the dealer
	 * 
	 * @param opponentAgentName
	 *            : dealer agent name
	 * @param negotiatedCar
	 * @param price
	 */
	public void acceptOffer(String opponentAgentName, Car negotiatedCar, double price) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				System.out.println(myAgent.getName() + ": Accept offer from the dealer: " + price);
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, opponentAgentName));
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setReplyWith(String.valueOf(price));
					mess.setConversationId("car-negotiation");
					myAgent.send(mess);
					saveLogs(opponentAgentName);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	/**
	 * This behavior is for receiving refuse message from the dealer in case of no
	 * agreement
	 */
	private class NoAgreementFromDealer extends CyclicBehaviour {

		@Override
		public void action() {
			// Define template of the received message, which need to be matched to the sent
			// message from the dealer
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-negotiation-refuse"),
					MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				endTheNegotiationWithoutAgreement();
			} else {
				block();
			}
		}
	}

	/**
	 * Method for sending a refuse message to the dealer when no agreement reaches
	 * (because of out of time)
	 * 
	 * @param dealerName
	 * @param negotiatedCar
	 * @param price
	 */
	public void sendRefuseToTheDealer(String dealerName, Car negotiatedCar) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				ACLMessage mess = new ACLMessage(ACLMessage.REFUSE);
				System.err.println(myAgent.getName() + ": Refuse Agreement \n");
				mess.addReceiver(AgentSupport.findAgentWithName(myAgent, dealerName));
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(negotiatedCar);
					mess.setContent(jsonInString);
					mess.setConversationId("car-negotiation-refuse");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					System.err.println("Problem by converting an object o json-format");
				}
			}
		});
	}

	/**
	 * Method for ending the negotiation because of out of time.
	 */
	public void endTheNegotiationWithoutAgreement() {
		System.out.println("No Agreement!");
		new Thread(() -> {
			Platform.runLater(() -> {
				NoAgreementGUI guiBuyer = new NoAgreementGUI(this);
			});
		}).start();
	}

	/**
	 * Method to set the buyers negotiation way (manual or automated)
	 * 
	 * @param manualNegotiation
	 */
	public void setNegotiationManual(boolean manualNegotiation) {
		this.manualNegotiation = manualNegotiation;
	}

	private void saveLogs(String dealerName) {
		Negotiation session = new Negotiation(this.getName(), dealerName, buyerLogs, dealerLogs);
		try {
			String jsonString = o.writeValueAsString(session);
			negotiationDB.openFileWriter();
			negotiationDB.writeLine(jsonString);
			negotiationDB.closeFileWriter();
			buyerLogs = new ArrayList<Log>();
			dealerLogs = new ArrayList<Log>();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}
}
