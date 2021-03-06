package model;

/**
 * Class as representation for a car
 */
public class Car {

	private int carId;
	private String manufacture;
	private String model;
	private String transmission;
	private String fuelType;
	private int km;
	private String bodyType;
	private String color;
	private int warranty;
	private String agent;
	private double maxprice;
	private double minprice;
	private boolean carStatus = false; // false when the car is able to be sold
	private boolean isNegotiatable = false; // true for manual negotiation
	private int carrating;
	private String manufactureYear;
	private String picturePath;
	private String moreDetails; // only for showing data in the car details table of the seller
	private double beeta;
	private int steps;

	/**
	 * Constructor of the class
	 * @param carId : id of the car
	 */
	public Car(int carId) {
		super();
		this.carId = carId;
	}

	// empty constructor (necessary to convert a car object to json-format)
	public Car() {
	}

	public double getBeeta() {
		return beeta;
	}

	public void setBeeta(double beeta) {
		this.beeta = beeta;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
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

	public double getMaxprice() {
		return maxprice;
	}

	public boolean getisNegotiatable() {
		return isNegotiatable;
	}

	public void setNegotiatable(boolean isNegotiatable) {
		this.isNegotiatable = isNegotiatable;
	}

	public void setMaxprice(double maxprice) {
		this.maxprice = maxprice;
	}

	public double getMinprice() {
		return minprice;
	}

	public void setMinprice(double minprice) {
		this.minprice = minprice;
	}

	public boolean getcarStatus() {
		return carStatus;
	}

	public void setcarStatus(boolean carStatus) {
		this.carStatus = carStatus;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getMoreDetails() {
		moreDetails = "Transmission: " + transmission + "\nFuel type: " + fuelType + "\nBody type: " + bodyType
				+ "\nColor: " + color + "\nWarranty: " + warranty + "\nCar rating: " + carrating
				+ "\nAutomated negotiation: " + !isNegotiatable;
		return moreDetails;
	}

	@Override
	public String toString() {
		return "Car [carId=" + carId + ", manufacture=" + manufacture + ", model=" + model + ", transmission="
				+ transmission + ", fuelType=" + fuelType + ", km=" + km + ", bodyType=" + bodyType + ", color=" + color
				+ ", warranty=" + warranty + ", agent=" + agent + ", maxprice=" + maxprice + ", minprice=" + minprice
				+ ", carStatus=" + carStatus + ", carrating=" + carrating + ", manufactureYear=" + manufactureYear
				+ ", picturePath=" + picturePath + "]";
	}

}
