package agents;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import model.Car;
import model.CarList;

public class BuyerAgent extends Agent {
	/**
	 * 
	 */
	private ObjectMapper o = new ObjectMapper();
	private static final long serialVersionUID = -8414132078026686821L;
	private AID brokerAgent;
	private Car desiredCar;
	private CarList offerCarlist;
	private AID choosenDealer;

	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Buyer-agent " + getAID().getName() + " is ready.");

		Object[] args = getArguments();
		String manufacture = (String) args[0];
		String model = (String) args[1];
		String bodyType = (String) args[2];
		double maxPrice = Double.parseDouble((String) args[3]);
		desiredCar = new Car(1);
		desiredCar.setModel(model);
		desiredCar.setManufacture(manufacture);
		desiredCar.setPrice(maxPrice);
		desiredCar.setBodyType(bodyType);
		desiredCar.setAgent(this.getName());

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
					addBehaviour(new RequestInfoOfDesiredCar());
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

	// To request the broker to send a list of possible cars
	private class RequestInfoOfDesiredCar extends OneShotBehaviour {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			System.out.println("Trying to send a car to the broker");
			ACLMessage mess = new ACLMessage(ACLMessage.REQUEST);
			mess.addReceiver(brokerAgent);

			String jsonInString;
			try {
				jsonInString = o.writeValueAsString(desiredCar);
				mess.setContent(jsonInString);
				mess.setConversationId("car-trade");
				myAgent.send(mess);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	private class OfferFromBroker extends CyclicBehaviour {

		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchConversationId("car-offer"),
					MessageTemplate.MatchReplyWith("performative"));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String content = msg.getContent();
				switch (msg.getPerformative()) {
				case ACLMessage.INFORM:
					System.out.println("There are following possible offer for you:");

					try {
						offerCarlist = o.readValue(content, CarList.class);
						System.out.println(offerCarlist);
						addBehaviour(new OneShotBehaviour() {
							@Override
							public void action() {
								AMSAgentDescription[] agents = null;
								SearchConstraints c = new SearchConstraints();
								c.setMaxResults(new Long(-1));
								try {
									agents = AMSService.search(this.myAgent, new AMSAgentDescription(), c);
									for (int i = 0; i < agents.length; i++) {
										AID agentID = agents[i].getName();
										if (agentID.getName().equals(offerCarlist.get(0).getAgent())) {
											choosenDealer = agentID;
										}
									}
								} catch (FIPAException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					} catch (IOException e1) {
						// TODO Auto-generated catch block
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
}
