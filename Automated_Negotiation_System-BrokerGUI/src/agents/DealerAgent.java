package agents;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import model.Car;

public class DealerAgent extends Agent {

	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private ObjectMapper o = new ObjectMapper();

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Dealer-agent " + getAID().getName() + " is ready.");

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
					myAgent.addBehaviour(new SendListOfCar());
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Dealer-agent " + getAID().getName() + " terminating.");
	}

	// To send a list of cars to broker
	private class SendListOfCar extends OneShotBehaviour {
		@Override
		public void action() {
			System.out.println("Trying to send a list of cars to Broker");
			ACLMessage mess = new ACLMessage(ACLMessage.INFORM);
			mess.addReceiver(brokerAgent);
			ArrayList<Car> list = new ArrayList<Car>();
			Car car1 = new Car(1);
			car1.setAgent(myAgent.getName());
			car1.setModel("A2");
			car1.setManufacture("Audi");
			car1.setPrice(4000);
			Car car2 = new Car(2);
			car2.setModel("A3");
			car2.setAgent(myAgent.getName());
			car2.setManufacture("Honda");
			car2.setPrice(2000);
			list.add(car1);
			list.add(car2);
			
			String jsonInString;
			try {
				jsonInString = o.writeValueAsString(list);
				mess.setContent(jsonInString);
				mess.setConversationId("car-trade");
				myAgent.send(mess);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
}
