package agents;

import model.MultipleMessage;

/**
 * Broker Agent Interface for the interaction between BrokerMatchingList GUI and the broker agent
 */
public interface BrokerAgentInterface {

	public void sendBuyerListDataToDealer(MultipleMessage message);
	public MultiAgentManager getMultiAgentManager();
	public double getCommision();
}
