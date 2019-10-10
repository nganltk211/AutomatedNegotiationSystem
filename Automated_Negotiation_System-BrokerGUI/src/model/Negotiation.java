package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Negotiation implements Serializable{
	
	private static final long serialVersionUID = -3896235106950619702L;
	private String buyerName, dealerName;
	private ArrayList<Log> buyer = null, dealer = null;
	
	public Negotiation(String buyerName, String dealerName, ArrayList<Log> buyer, ArrayList<Log> dealer) {
		this.buyerName = buyerName;
		this.dealerName = dealerName;
		this.buyer = buyer;
		this.dealer = dealer;
	}

	public Negotiation() {
		
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

	public ArrayList<Log> getBuyer() {
		return buyer;
	}

	public void setBuyer(ArrayList<Log> buyer) {
		this.buyer = buyer;
	}

	public ArrayList<Log> getDealer() {
		return dealer;
	}

	public void setDealer(ArrayList<Log> dealer) {
		this.dealer = dealer;
	}
	
}
