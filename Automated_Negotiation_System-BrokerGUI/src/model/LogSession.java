package model;

import java.io.Serializable;

/**
 *  Class as representation for a negotiation session.
 */
public class LogSession implements Serializable{

	private static final long serialVersionUID = 460127305922452230L;
	private int step;
	private double offer;
	
	public LogSession(int step, double offer) {
		this.step = step;
		this.offer = offer;
	}
	
	public LogSession() {	
	}
	
	public void setStep(int step) {
		this.step = step;
	}

	public void setOffer(double offer) {
		this.offer = offer;
	}

	public int getStep() {
		return this.step;
	}
	
	public double getOffer() {
		return this.offer;
	}

}
