package model;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class as representation for message used in multiple negotiation
 */
public class MultipleMessage {

	private Car car;
	private Map<String, Double> buyerList; // a map containing buyer name with his last offer	
	private String buyerListString; // for showing name of the buyer interested in a same car 
	
	public MultipleMessage() {		
	}
	
	public MultipleMessage(Car car) {
		buyerList = new Hashtable<>();
		this.car = car;
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
	
	public String getBuyerListString() {
		String newList = ""; 
		for(Entry<String, Double> element : buyerList.entrySet())
		{
			newList += element.getKey() + " " + element.getValue() + "\n";
		}
		return newList;		
	}
}
