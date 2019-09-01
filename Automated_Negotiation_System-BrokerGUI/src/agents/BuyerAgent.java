package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

public class BuyerAgent extends Agent{
	
	private String _carTitle;
	private AID[] brokerAgent;
	protected void setup()
	{
		System.out.println("Hellow!, Buyer-agent"+ getAID().getName()+"is ready.");
		Object[] args = getArguments();
		if(args != null && args.length > 0)
		{
			_carTitle = (String) args[0];
			System.out.println("Car to Buy is"+ _carTitle);
		}
		addBehaviour(new OneShotBehaviour()
		{
			@Override
			public void action()
			{
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
			    message.addReceiver(new AID("TestingBrokerCommunicationWithBuyer", AID.ISLOCALNAME));
			    message.setContent("Car to Buy is"+ _carTitle);
			    send(message);	
			}
		});
	}
}


