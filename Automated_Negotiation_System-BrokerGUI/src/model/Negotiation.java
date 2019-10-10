package model;

import java.util.ArrayList;

public class Negotiation {
	
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
		return this.buyerName;
	}
	
	public String setDealerName() {
		return this.dealerName;
	}
	
	public ArrayList<Log> getBuyerlog()
	{
		return this.buyer;
	}
	
	public ArrayList<Log> getDealerlog()
	{
		return this.dealer;
	}
	
}
