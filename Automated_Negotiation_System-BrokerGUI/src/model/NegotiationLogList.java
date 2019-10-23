package model;

import java.util.ArrayList;

public class NegotiationLogList extends ArrayList<NegotiationLog>{
	
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
