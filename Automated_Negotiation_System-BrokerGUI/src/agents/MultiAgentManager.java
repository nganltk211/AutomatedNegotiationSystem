package agents;

import java.util.ArrayList;
import java.util.Iterator;

import model.Car;
import model.MultipleMessage;

/*
 MultiAgent negotiation manager
 */

public class MultiAgentManager {
	
	ArrayList<MultipleMessage> negotiationList = null;
	
	public MultiAgentManager() {
		negotiationList = new ArrayList<MultipleMessage>();
	}
	
	//Add Negotiation to the list 
	public void addBuyer(Car car, String buyer, double firstOffer)
	{
		boolean isCarinList = false;
		//MultipleMessage buyerList = new MultipleMessage();
		for(MultipleMessage m : negotiationList)
		{
			if(car.getCarId() == m.getCar().getCarId())
			{
				//add only buyer to multiple message list
				m.addToBuyerList(buyer, firstOffer);
				//make boolean to true
				isCarinList = true;
			}
		}
		if(!isCarinList)
		{
			// add whole list to buyer if there is no same car in the list
			MultipleMessage buyerList = new MultipleMessage(car);
			buyerList.addToBuyerList(buyer, firstOffer);
			negotiationList.add(buyerList);
		}
	
	}

	@Override
	public String toString() {
		return "MultiAgentManager [negotiationList=" + negotiationList + "]";
	}

	public ArrayList<MultipleMessage> getNegotiationList() {
		return negotiationList;
	}
	public void removeCarFromList(Car car)
	{
		for (Iterator<MultipleMessage> iterator = negotiationList.iterator(); iterator.hasNext();) {
			MultipleMessage message = iterator.next();
		    if(!message.getBuyerList().isEmpty()) {
		    	if(car.getCarId() == message.getCar().getCarId())
				{
		    		iterator.remove();
				}
     
		    }
		}
		
	}
	

	
}
	
	//This function increment buyers steps
	/*public void incrementSteps(String buyer, int steps) {
		
		for(Negotiation i : negotiationList) {
			
			if(i.getBuyer().equals(buyer)) {
				i.setSteps(steps);
				break;
			}
		}
	}
	
	//Remove buyer by passing buyer name
	public void terminateSession(String buyer) {
		
		int length = negotiationList.size();
		
		for(int i = 0; i < length; i++) {
			
			String x = negotiationList.get(i).getBuyer();
			
			if(buyer.equals(x)) {
				negotiationList.remove(i);
				break;
			}
		}
		
	}
	
	//Detect MultiAgent negotiation 
	public boolean isMultiAgent()
	{
		boolean condition = false;
		for(BrookerStorage i : negotiationList) {
			
			for(BrookerStorage j : negotiationList) {
				if(i == j) {
					continue;
				}
				if(i.getCarId() == j.getCarId()) {
					condition = true;
				}
			}
		}
		return condition;
	}
	
	//Get all MultiAgents in negotiation
	public ArrayList<ArrayList<String>> getMultiAgents() {
		
		ArrayList<ArrayList<String> > aidList = new ArrayList<ArrayList<String> >();
		ArrayList<Negotiation> tempList = new ArrayList<Negotiation>(negotiationList);
		
		for(Negotiation i : negotiationList) {
			ArrayList<String> temp = new ArrayList<String>();
			
			int length = tempList.size();
			
			for(int j = 0; j < length; j++) {
				
				if(i.getCarId() == tempList.get(j).getCarId()) {
						temp.add(tempList.get(j).getBuyer());
						tempList.remove(j);
				}
			}
			aidList.add(temp);
		}
		
		return aidList;
	}*/
	

