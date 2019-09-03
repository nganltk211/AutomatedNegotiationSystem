package model;

public class Car {

	private int carId;
	public String manufacture = "";
	private String model = "";
	private String transmission = "";
	private String fuelType = "";
	private int km;
	private double price;
	private String bodyType = "";
	private String color = "";
	private int warranty;
	private String agent = "";

	public Car(int carId) {
		super();
		this.carId = carId;
	}

	public Car() {
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

	@Override
	public String toString() {
		return "Car [carId=" + carId + ", manufacture=" + manufacture + ", model=" + model + ", transmission="
				+ transmission + ", fuelType=" + fuelType + ", km=" + km + ", price=" + price + ", bodyType=" + bodyType
				+ ", color=" + color + ", warranty=" + warranty + ", agent=" + agent + "]";
	}
	
}
