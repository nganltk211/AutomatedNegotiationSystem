package agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Platform;
import model.Car;
import model.CarList;
import model.LogSession;
import model.NegotiationLog;
import model.ReserveOffer;

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
	private JsonIO noAgreementDB = new JsonIO("./DataBase/NoAgreementDB.txt");
	private ArrayList<LogSession> buyerLogs;
	private ArrayList<LogSession> dealerLogs;
	// variable for concurrent negotiation
	private long negotiationDuration;
	private long startTime;
	private long deadline;
	private int numberOfSeller = 0;
	private int numberOfActiveSeller = 0;
	public boolean firstNegotiationThread;
	private ArrayList<ReserveOffer> reserveOfferList; // list to store the buyer name with his lastOffer
	private long timeOneRound;
	private Map<String, PreviousOfferDetails> dealerPreviousOffers;

	public void newSellerCome() {
		numberOfSeller++;
	}

	public long getNegotiationDuration() {
		return negotiationDuration;
	}

	public void setNegotiationDuration(long negotiationDuration) {
		this.negotiationDuration = negotiationDuration*1000; // ms
	}

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

	/**
	 * Method for setting up a buyer agent
	 */
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! Buyer-agent " + getAID().getName() + " is ready.");
		firstNegotiationThread = true;
		reserveOfferList = new ArrayList<ReserveOffer>();
		dealerPreviousOffers = new HashMap<String, PreviousOfferDetails>();
		
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
		//addBehaviour(nd);
		addBehaviour(new EndTheNegotiation());
		addBehaviour(new NoAgreementFromDealer());
		addBehaviour(new NegotiationWithDealerCONANStrategy());
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
		buyerLogs = new ArrayList<LogSession>();
		dealerLogs = new ArrayList<LogSession>();
		if (manualNegotiation) {
			// Adding Buyers logs into list
			LogSession blog = new LogSession(0, beetaValue, firstOfferPrice);
			buyerLogs.add(blog);
		}
		numberOfActiveSeller++;
		numberOfSeller++;
		if (firstNegotiationThread) {
			startTime = System.currentTimeMillis();
			deadline = startTime + negotiationDuration;
			System.out.println("StartTime " + startTime + " Duration " + negotiationDuration);
			firstNegotiationThread = false;
			addWakerBehaviour();
		}
		dealerPreviousOffers.put(negotiatedCar.getAgent(), new PreviousOfferDetails(0, negotiatedCar.getMaxprice(), System.currentTimeMillis()));
		
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

	// when deadline reaches
	private void addWakerBehaviour() {
		addBehaviour(new WakerBehaviour(this, negotiationDuration) {
			protected void onWake() {
				if (reserveOfferList.size() > 0) {
					String bestSeller = reserveOfferList.get(0).getDealerName();
					double bestOffer = reserveOfferList.get(0).getOffer();
					Car negotiatedCar = reserveOfferList.get(0).getCar(); 
					int index = 0;
					for (int i = 0; i < reserveOfferList.size(); i++) {
						// find the best buyer by comparing the last offer price
						if (reserveOfferList.get(i).getOffer() < bestOffer) {
							bestOffer = reserveOfferList.get(i).getOffer();
							bestSeller = reserveOfferList.get(i).getDealerName();
							negotiatedCar = reserveOfferList.get(i).getCar();
							index = i;
						}
					}
					if (bestOffer != 0) { // when a best seller is found
						System.out.println("Dealer: Best seller: " + bestSeller + " with offer: " + bestOffer);
						// send accept message to the best seller
						acceptOffer(bestSeller, negotiatedCar, bestOffer); // need the car
						// remove the best buyer from the list
						reserveOfferList.remove(index);
					}
					// send reject message to remaining seller
					for (ReserveOffer element : reserveOfferList) {
						if (element.getOffer() != 0) {
							sendRefuseToTheDealer(element.getDealerName(), element.getCar());
						}
					}
					reserveOfferList.clear();
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
					LogSession dlog = new LogSession(dealerSteps, messObject.getBeeta(), offerPrice);
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
								LogSession blog = new LogSession(step++, beetaValue, offerPrice);
								buyerLogs.add(blog);
								acceptOffer(dealerName, messObject, offerPrice);
							} else {
								makeACounterOffer(dealerName, messObject, nextPrice, dealerTimeStep, step);
								step++;
							}
						} else {
							step = 0;
							endTheNegotiationWithoutAgreement(dealerName);
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

	private class NegotiationWithDealerCONANStrategy extends CyclicBehaviour {
		
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
					long timePointDealer = msg.getPostTimeStamp(); // only calculate through the first offer
					if (timePointDealer > 0) {
						timeOneRound = System.currentTimeMillis() - timePointDealer;
					}

					// adds to dealer log
					LogSession dlog = new LogSession(dealerSteps, messObject.getBeeta(), offerPrice);
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
						System.out.println("Buyer: Receive offer from the dealer: " + offerPrice);
						double nextOffer = 0.0;
						// calculate the next offer
						long currentTime = System.currentTimeMillis();
						double effectOfTime = Algorithms.getEffectOfTime(currentTime, startTime, negotiationDuration);
						System.out.println("Effect of time " + effectOfTime);
						
						double enviromentFactor = Algorithms.getEnvironmentFactor(numberOfActiveSeller, 1, 1, numberOfSeller);
						System.out.println("EnviromentFactor " + enviromentFactor);
						
						int oppResponseTimeScore = Algorithms.getOpponentResponseTimeASellerScore(msg.getPostTimeStamp(), dealerPreviousOffers.get(dealerName).getBuyerLastOfferAtTime(), negotiationDuration);
						System.out.println("oppResponseTime " + oppResponseTimeScore);
						
						int oppConcessionRateScore = Algorithms.getOpponentConcessionRateASellerScore(offerPrice, dealerPreviousOffers.get(dealerName).getLastOfferOfSeller() , reservationPrice, intialPrice);
						System.out.println("oppConcessionRate " + oppConcessionRateScore);
						
						double oppFactor = oppResponseTimeScore + oppConcessionRateScore;
						
						double negoSituation = Algorithms.getNegotiationSituation(numberOfSeller, oppFactor);
						System.out.println("negoSituation " + negoSituation);
						
						double selffactor = Algorithms.getSelfFactor(reserveOfferList.size(), negoSituation, effectOfTime, 1); 
						System.out.println("selffactor " + selffactor);
						
						double ws = Algorithms.getWeightForSelfFactor(effectOfTime, messObject.getMaxprice(), reservationPrice, selffactor);
						System.out.println("ws " + ws);
						double concessionRate = Algorithms.getConcessionRate(System.currentTimeMillis(), startTime, deadline, timeOneRound, dealerPreviousOffers.get(dealerName).getLastConcessionRate(), false, ws, enviromentFactor, selffactor);
						System.out.println("concessionRate " + concessionRate);
						nextOffer = Algorithms.getNextOffer(intialPrice, reservationPrice, concessionRate);
						System.out.println("nextOffer " + nextOffer);
						
						//.............
						if (compareOfferToOffersInReserveList(offerPrice) && nextOffer >= offerPrice) {
							if (System.currentTimeMillis() >= deadline - timeOneRound) { // near the deadline
								acceptOffer(dealerName, messObject, offerPrice);
							} else {
								reserveOfferList.add(new ReserveOffer(offerPrice, dealerName, messObject ));
								sendReqToReserve(dealerName);
							}
						} else {
							dealerPreviousOffers.replace(dealerName, new PreviousOfferDetails(concessionRate, offerPrice, System.currentTimeMillis()));
							makeACounterOffer(dealerName, messObject, nextOffer, dealerTimeStep, Integer.parseInt(dealerTimeStep));
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
	 * The buyer agent makes a counter-offer to the dealer agent
	 * 
	 * @param opponentAgentName
	 *            : name of the dealer agent
	 * @param negotiatedCar
	 * @param price:
	 *            offer-price
	 */
	public void makeACounterOffer(String opponentAgentName, Car negotiatedCar, double price, String dealerTimeStep,
			int buyerStep) {
		// Adding Buyers logs into list
		LogSession blog = new LogSession(buyerStep, beetaValue, price);
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

		saveLogs(opponentAgentName);
		numberOfActiveSeller --;
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
					nd.setStep(0);
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
				String dealerName = msg.getSender().getName();
				try {
					endTheNegotiationWithoutAgreement(dealerName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	 * 
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public void endTheNegotiationWithoutAgreement(String dealerName)
			throws JsonParseException, JsonMappingException, IOException {
		System.out.println("No Agreement!");
		saveNoAgreementLogs(dealerName);

		noAgreementDB.openFileReader();
		NegotiationLog session = o.readValue(noAgreementDB.readLine(), NegotiationLog.class);
		noAgreementDB.closeFileReader();

		new Thread(() -> {
			Platform.runLater(() -> {
				NoAgreementGUI guiBuyer = new NoAgreementGUI(this, session);
			});
		}).start();
	}

	private boolean compareOfferToOffersInReserveList(double offer) {
		for (ReserveOffer element : reserveOfferList) {
			if (element.getOffer() > offer) {
				return false;
			}
		}
		return true;
	}

	private void sendReqToReserve(String dealerName) {
		// send request to reserve to the dealer
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
					// adds to reserveOfferList
					boolean add = compareOfferToOffersInReserveList(offerPrice);
					if (add) {
						if (System.currentTimeMillis() >= deadline - timeOneRound) { // near the deadline
							acceptOffer(dealerName, negotiatedCar, offerPrice);
						} else {
							reserveOfferList.add(new ReserveOffer(offerPrice, dealerName, negotiatedCar ));
							sendReqToReserve(dealerName);
						}
					}

					// acceptOffer(dealerName, negotiatedCar, offerPrice);
					// buyerLogs = new ArrayList<LogSession>();
					// dealerLogs = new ArrayList<LogSession>();
				} catch (IOException e) {
					System.err.println("Problem by converting a json-format to an object");
				}
			} else {
				block();
			}
		}
	}

	/**
	 * Method to set the buyers negotiation way (manual or automated)
	 * 
	 * @param manualNegotiation
	 */
	public void setNegotiationManual(boolean manualNegotiation) {
		this.manualNegotiation = manualNegotiation;
	}

	/**
	 * Method to save the log of all negotiation session in case of no agreement
	 * reaches in a text file.
	 * 
	 * @param dealerName
	 *            : name of the dealer
	 */
	private void saveNoAgreementLogs(String dealerName) {
		NegotiationLog session = new NegotiationLog(this.getName(), dealerName, buyerLogs, dealerLogs);
		try {
			String jsonString = o.writeValueAsString(session);
			noAgreementDB.openFileWriter();
			noAgreementDB.writeLine(jsonString);
			noAgreementDB.closeFileWriter();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to save the log of all negotiation session in case of an agreement
	 * reaches in a text file.
	 * 
	 * @param dealerName
	 *            : name of the dealer
	 */
	private void saveLogs(String dealerName) {
		NegotiationLog session = new NegotiationLog(this.getName(), dealerName, buyerLogs, dealerLogs);
		try {
			String jsonString = o.writeValueAsString(session);
			negotiationDB.openFileWriter();
			negotiationDB.writeLine(jsonString);
			negotiationDB.closeFileWriter();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}
}
