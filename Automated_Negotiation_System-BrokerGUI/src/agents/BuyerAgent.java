package agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gui.BuyerGui;
import gui.ListBuyer_GUI;
import jade.core.AID;
import jade.core.Agent;
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
import javafx.application.Platform;
import model.Car;
import model.CarList;

public class BuyerAgent extends Agent {

	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private Car desiredCar;
	private CarList offerCarlist;
	private ObjectMapper o = new ObjectMapper();

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Buyer-agent " + getAID().getName() + " is ready.");

		// starts the GUI
		new Thread(() -> {
			Platform.runLater(() -> {
				BuyerGui guiBuyer = new BuyerGui(this);
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
		addBehaviour(new OfferFromBroker());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent " + getAID().getName() + " terminating.");
	}

	private class OfferFromBroker extends CyclicBehaviour {

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-offer"),
					MessageTemplate.MatchReplyWith("performative"));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String content = msg.getContent();
				switch (msg.getPerformative()) {
				case ACLMessage.INFORM:
					System.out.println("Buyer: There are following possible offers for you:");
					try {
						offerCarlist = o.readValue(content, CarList.class);
						System.out.println(offerCarlist);
						new Thread(() -> {
							Platform.runLater(() -> {
								ListBuyer_GUI listCarGUI = new ListBuyer_GUI(offerCarlist,myAgent);
							});
						}).start();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				case ACLMessage.REFUSE:
					System.out.println(content);
				}
			} else {
				block();
			}
		}
	}

	public void sendBackTheChoosenCarsToTheBroker(CarList listOfChoosenCars) {
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
//				CarList listOfChoosenCars = new CarList();
//				int choosenCar;
//				do {
//					System.out.println("Please choose one offer!");
//					choosenCar = readInt();
//				} while (choosenCar > list.size() || choosenCar < 1);
//
//				listOfChoosenCars.add(offerCarlist.get(choosenCar - 1));
				System.out.println("Trying to send a choosen car to the broker\n");
				ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
				mess.addReceiver(brokerAgent);
				String jsonInString;
				try {
					jsonInString = o.writeValueAsString(listOfChoosenCars);
					mess.setContent(jsonInString);
					mess.setConversationId("car-trade-broker-buyer");
					myAgent.send(mess);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	private int readInt() {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			String eingabe = "";
			Integer string_to_int;
			eingabe = input.readLine();
			string_to_int = new Integer(eingabe);
			return string_to_int.intValue();
		} catch (Exception e) {
			System.err.println("Please give the right choice of car!");
			return -2;
		}
	}
	
	public void requestInfoOfDesiredCar(Car desiredCar) {
		// To request the broker to send a list of possible cars
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
					e.printStackTrace();
				}		
			}
			
		});
	}
}
