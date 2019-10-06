package model;

import java.util.Hashtable;
import java.util.Map;

public class MultipleMessage {

	private Car car;
	private Map<String, Double> buyerList;
	
	public MultipleMessage() {
		buyerList = new Hashtable<>();
	}
	
	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public Map<String, Double> getBuyerList() {
		return buyerList;
	}

	public void setBuyerList(Map<String, Double> buyerList) {
		this.buyerList = buyerList;
	}
	
	public void addToBuyerList(String buyer, double offer) {
		buyerList.put(buyer, offer);
	}
}
