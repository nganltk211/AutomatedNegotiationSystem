package model;

public class BrookerStorage {
	
	private Car negoCar;
	private String buyer;
	
	

	public BrookerStorage(Car negoCar, String buyer) {
		this.negoCar = negoCar;
		this.buyer = buyer;
		
	}

	public Car getCar() {
		return this.negoCar;
	}
	
	public String getBuyer() {
		return this.buyer;
	}
	

}
