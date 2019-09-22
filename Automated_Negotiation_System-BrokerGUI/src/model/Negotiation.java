package model;

public class Negotiation {
	
	private boolean  sessionStatus;
	private int carID;
	private boolean offerStatus;
	private double offer;
	
	public Negotiation(boolean sessionStatus, int carID, boolean offerStatus, double offer) {
		this.sessionStatus = sessionStatus;
		this.carID = carID;
		this.offerStatus = offerStatus;
		this.offer = offer;
	}
	
	public boolean getsessionStatus() {
		return sessionStatus;
	}
	
	public int getcarID() {
		return carID;
	}
	
	public boolean getofferStatus() {
		return offerStatus;
	}
	
	public double getoffer() {
		return offer;
	}

}
