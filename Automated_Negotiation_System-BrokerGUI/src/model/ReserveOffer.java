package model;

/**
 * Class to store the information about a buyer's reserved offer
 */
public class ReserveOffer {
	
	private double offer;
	private String dealerName;
	private Car car;
	public ReserveOffer(double offer, String dealerName, Car car) {
		super();
		this.offer = offer;
		this.dealerName = dealerName;
		this.car = car;
	}
	public double getOffer() {
		return offer;
	}
	public void setOffer(double offer) {
		this.offer = offer;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	
	
}
