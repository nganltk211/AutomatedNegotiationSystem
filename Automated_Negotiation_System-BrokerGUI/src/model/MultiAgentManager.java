package model;

import java.util.ArrayList;

/*
 MultiAgent negotiation manager
 */

public class MultiAgentManager {
	
	ArrayList<Negotiation> negotiationList = null;
	
	public MultiAgentManager() {
		negotiationList = new ArrayList<Negotiation>();
	}
	
	//Add Negotiation to the list 
	public void addSession(int carID, String buyer, int steps)
	{
		Negotiation negotiation = new Negotiation(carID, buyer, steps);
		negotiationList.add(negotiation);
	}
	
	//This function increment buyers steps
	public void incrementSteps(String buyer) {
		
		for(Negotiation i : negotiationList) {
			
			if(i.getBuyer().equals(buyer)) {
				int s = i.getSteps();
				i.setSteps(s++);
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
		for(Negotiation i : negotiationList) {
			
			for(Negotiation j : negotiationList) {
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
	}
	
}
