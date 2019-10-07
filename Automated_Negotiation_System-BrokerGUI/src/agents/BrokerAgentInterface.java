package agents;

import model.MultipleMessage;

public interface BrokerAgentInterface {

	public void sendBuyerListDataToDealer(MultipleMessage message);
	public MultiAgentManager getMultiAgentManager();
	public double getCommision();
}
