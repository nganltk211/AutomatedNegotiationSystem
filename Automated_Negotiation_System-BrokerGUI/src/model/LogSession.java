package model;

import java.io.Serializable;

public class LogSession implements Serializable{

	private static final long serialVersionUID = 460127305922452230L;
	private int step;
	private double beeta;
	private double offer;
	
	public LogSession(int step, double beeta, double offer) {
		this.step = step;
		this.beeta = beeta;
		this.offer = offer;
	}
	
	public LogSession() {
		
	}
	
	public void setStep(int step) {
		this.step = step;
	}

	public void setBeeta(double beeta) {
		this.beeta = beeta;
	}

	public void setOffer(double offer) {
		this.offer = offer;
	}

	public int getStep() {
		return this.step;
	}
	
	public double getBeeta() {
		return this.beeta;
	}
	
	public double getOffer() {
		return this.offer;
	}

}
