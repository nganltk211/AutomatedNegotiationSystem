package agents;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gui.BuyerGui;
import gui.DealerGUI;
import gui.NegotiationChoiceGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.application.Application;
import javafx.application.Platform;
import model.Car;
import model.CarList;

public class DealerAgent extends Agent {

	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private ObjectMapper o = new ObjectMapper();
	private int negotiationOption = 0; // 0 for manual negotiation and 1 for automated negotiation
	
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Dealer-agent " + getAID().getName() + " is ready.");

		//registerO2AInterface(DealerAgentInterface.class, this);
		
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
	}

	/**
	 * Method for the Agent clean-up
	 */
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Dealer-agent " + getAID().getName() + " terminating.");
	}
	
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
					CarList choosenCars = o.readValue(content, CarList.class);
					System.out.println(choosenCars + "\n");
					System.out.println("Dealer: Trying to create a negotiation with the buyer: " + buyer);
					// starts the Negotiation GUI 
					new Thread(() -> {
						Platform.runLater(() -> {
							for (Car c : choosenCars) {
								NegotiationChoiceGUI gui = new NegotiationChoiceGUI(myAgent,buyer, c);
							}					
						}); 
					}).start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
		
	public void sendListOfCarToBroker(CarList carList) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				System.out.println("Dealer : Trying to send a list of cars to Broker\n");
				ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
				mess.addReceiver(brokerAgent);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
				
			}			
		});
	}	
	
	private class ManualNegotiationBehaviourWithBuyer extends OneShotBehaviour {
		private String buyerAgentName;
		private Car negotiatedCar;
		private double offerPrice;
		
		public ManualNegotiationBehaviourWithBuyer(String opponentAgentName, Car car, double firstOfferPrice) {
			buyerAgentName = opponentAgentName;
			negotiatedCar = car;
			this.offerPrice = firstOfferPrice;
		}
		
		@Override
		public void action() {
			ACLMessage mess = new ACLMessage(ACLMessage.PROPOSE);
			System.out.println(myAgent.getName() + ": First offer to the buyer: " + offerPrice);
			mess.addReceiver(AgentSupport.findAgentWithName(myAgent, buyerAgentName));
			mess.setContent(String.valueOf(offerPrice));
			mess.setConversationId("car-negotiation");
			myAgent.send(mess);
		}
	}
	
	public void startNegotiation(String opponentAgentName, Car car, double firstOfferPrice) {
		if (negotiationOption == 0) {
			addBehaviour(new ManualNegotiationBehaviourWithBuyer(opponentAgentName,car,firstOfferPrice));
		}
	}

	public void setNegotiationChoice(int option) {
		negotiationOption = option;
	}

}