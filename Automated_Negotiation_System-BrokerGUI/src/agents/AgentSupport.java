package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;

public class AgentSupport {
	
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
