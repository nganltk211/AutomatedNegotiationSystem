package model;

import java.util.ArrayList;


public class MultiAgentManager {
	
	ArrayList<Negotiation> negotiationList = null;
	
	public MultiAgentManager() {
		negotiationList = new ArrayList<Negotiation>();
	}
	
	public int addSession(int carID, String buyer, int steps)
	{
		Negotiation negotiation = new Negotiation(carID, buyer, steps);
		negotiationList.add(negotiation);
		return sessionID;
	}
	
	public void incrimentSteps(String buyer) {
		
		for(Negotiation i : negotiationList) {
			
			if(i.getBuyer().equals(buyer)) {
				int s = i.getSteps();
				i.setSteps(s++);
				break;
			}
		}
	}
	
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
	
	public boolean isMultiAgent()
	{
		boolean condition = false;
		for(Negotiation i : negotiationList) {
			
			for(Negotiation j : negotiationList) {
				if(i.getCarId() == j.getCarId()) {
					condition = true;
				}
			}
		}
		return condition;
	}
	
	public ArrayList<ArrayList<String>> getMultiAgents() {
		
		ArrayList<ArrayList<String> > aidList = new ArrayList<ArrayList<String> >();
		
		for(Negotiation i : negotiationList) {
			ArrayList<String> temp = new ArrayList<String>();
			
			for(Negotiation j : negotiationList) {
				if(i.getCarId() == j.getCarId()) {
						temp.add(j.getBuyer());
				}
			}
			aidList.add(temp);
		}
		
		return aidList;
	}
	
}
