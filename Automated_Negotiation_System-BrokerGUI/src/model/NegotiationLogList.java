package model;

import java.util.ArrayList;

/**
 * Class as representation for a list of negotiation logs
 */
public class NegotiationLogList extends ArrayList<NegotiationLog>{
	
	/**
	 * Method to add the information about negotiation step and offer to an agent's log
	 * @param agentName : name of the agent
	 * @param step
	 * @param offer
	 */
	public void addToLog(String agentName, int step, double offer) {
		for (NegotiationLog log : this) {
			if (log.getAgentName().equals(agentName)) {
				log.addOffer(step, offer);
			}
		}
	}
	
	public String toString() {
		String s = "";
		for (NegotiationLog log : this) {
			s += log.getAgentName() + " ";
			for (LogSession session : log.getAgentLog()) {
				s += "(" + session.getStep() + " " + session.getOffer() + ");";
			}
			s += "\n";
		}
		return s;
		
	}
}
