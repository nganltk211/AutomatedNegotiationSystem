package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class as representation for a log of a negotiation, which contains all
 * negotiation sessions of buyer and dealer.
 */
public class NegotiationLog implements Serializable {

	private static final long serialVersionUID = -3896235106950619702L;
	private String agentName;
	private ArrayList<LogSession> agentLog;

	public NegotiationLog(String agentName, ArrayList<LogSession> agentLog) {
		this.agentName = agentName;
		this.agentLog = new ArrayList<LogSession>(agentLog);
	}

	public NegotiationLog() {
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public ArrayList<LogSession> getAgentLog() {
		return agentLog;
	}

	public void setAgentLog(ArrayList<LogSession> agentLog) {
		this.agentLog = agentLog;
	}
	
	public void addOffer(int step, double offer) {
		agentLog.add(new LogSession(step,offer));
	}
}
