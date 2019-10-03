package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;

/**
 * Class for static-methods used to support the agent classes. 
 */
public class AgentSupport {
	
	/**
	 * This method is used to look for AID of one agent through its name.
	 * @param hostAgent : start finding from this agent
	 * @param buyerName : name of the agent, whose AID needs to be return
	 * @return AID of the agent
	 */
	public static AID findAgentWithName(Agent hostAgent, String buyerName) {
		AMSAgentDescription[] agents = null;
		SearchConstraints c = new SearchConstraints();
		c.setMaxResults(new Long(-1));
		try {
			agents = AMSService.search(hostAgent, new AMSAgentDescription(), c);
			for (int i = 0; i < agents.length; i++) {
				AID agentID = agents[i].getName();
				if (agentID.getName().equals(buyerName)) {
					return agentID;
				}
			}
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
