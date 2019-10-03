package model;

public class Negotiation {
	
	private int carId;
	private String buyer;
	private int steps;
	

	public Negotiation(int carId, String buyer, int steps) {
		this.carId = carId;
		this.buyer = buyer;
		this.steps = steps;
	}

	public int getCarId() {
		return this.carId;
	}
	
	public String getBuyer() {
		return this.buyer;
	}
	
	public int getSteps() {
		return this.steps;
	}
	
	public void setSteps(int steps) {
		this.steps = steps;
	}
}
