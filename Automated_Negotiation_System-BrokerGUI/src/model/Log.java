package model;

public class Log {
	private int step;
	private double beeta;
	private double offer;
	
	public Log(int step, double beeta, double offer) {
		this.step = step;
		this.beeta = beeta;
		this.offer = offer;
	}
	
	public Log() {
		
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
