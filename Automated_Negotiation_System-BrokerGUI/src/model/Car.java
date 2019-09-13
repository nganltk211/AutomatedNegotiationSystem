package model;

public class Car {

	private int carId;
	private String manufacture = "";
	private String model = "";
	private String transmission = "";
	private String fuelType = "";
	private int km;
	private String bodyType = "";
	private String color = "";
	private int warranty;
	private String agent = "";
	private double price;
	private int carrating;
	private String manufactureYear;
	private String picturePath;
	private String moreDetails; //only for showing data in the car details table of the seller 

	public Car(int carId) {
		super();
		this.carId = carId;
	}

	public Car() {
	}

	public int getCarrating() {
		return carrating;
	}

	public void setCarrating(int carrating) {
		this.carrating = carrating;
	}

	public String getManufactureYear() {
		return manufactureYear;
	}

	public void setManufactureYear(String manufactureYear) {
		this.manufactureYear = manufactureYear;
	}
	
	public int getCarId() {
		return carId;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public String getManufacture() {
		return manufacture;
	}

	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTransmission() {
		return transmission;
	}

	public void setTransmission(String transmission) {
		this.transmission = transmission;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public int getKm() {
		return km;
	}

	public void setKm(int km) {
		this.km = km;
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getWarranty() {
		return warranty;
	}

	public void setWarranty(int warranty) {
		this.warranty = warranty;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getMoreDetails() {
		moreDetails = "Transmission: " + transmission + "\nFuel type: " + fuelType + "\nBody type: " + bodyType 
					+ "\nColor: " + color + "\nWarranty: " + warranty + "\nCar rating: " + carrating;
		return moreDetails;
	}

	@Override
	public String toString() {
		return "Car [carId=" + carId + ", manufacture=" + manufacture + ", model=" + model + ", transmission="
				+ transmission + ", fuelType=" + fuelType + ", km=" + km + ", bodyType=" + bodyType + ", color=" + color
				+ ", warranty=" + warranty + ", agent=" + agent + ", price=" + price + ", carrating=" + carrating
				+ ", manufactureYear=" + manufactureYear + "]";
	}
	
}
