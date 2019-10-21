package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class as representation for a log of a negotiation, which contains all
 * negotiation sessions of buyer and dealer.
 */
public class NegotiationLog implements Serializable {

	private static final long serialVersionUID = -3896235106950619702L;
	private String buyerName, dealerName;
	private ArrayList<LogSession> buyerLog = null, dealerLog = null;

	public NegotiationLog(String buyerName, String dealerName, ArrayList<LogSession> buyer,
			ArrayList<LogSession> dealer) {
		this.buyerName = buyerName;
		this.dealerName = dealerName;
		this.buyerLog = buyer;
		this.dealerLog = dealer;
	}

	public NegotiationLog() {
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public ArrayList<LogSession> getBuyerLog() {
		return buyerLog;
	}

	public void setBuyerLog(ArrayList<LogSession> buyerLog) {
		this.buyerLog = buyerLog;
	}

	public ArrayList<LogSession> getDealerLog() {
		return dealerLog;
	}

	public void setDealerLog(ArrayList<LogSession> dealerLog) {
		this.dealerLog = dealerLog;
	}
}
