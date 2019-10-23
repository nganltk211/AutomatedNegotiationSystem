package gui;

import java.io.File;

import agents.BuyerAgent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Car;

/**
 * A Controller class of CarInfoGUIController. The logic of GUI-Elements will be defined in
 * this class.
 */
public class CarInfoGUIController {
	@FXML
	private Label manufacture;
	@FXML
	private Label model;
	@FXML
	private Label transmission;
	@FXML
	private Label fuelType;
	@FXML
	private Label km;
	@FXML
	private Label bodytype;
	@FXML
	private Label color;
	@FXML
	private Label price;
	@FXML
	private Label warranty;
	@FXML
	private Label agent_name;
	@FXML
	private Button btn_negotiate;
	@FXML
	private ImageView imageView;
	@FXML
	private CheckBox checkbox;
	
	private Car car;
	private BuyerAgent buyerAgent;
	
	public CarInfoGUIController() {
		super();
	}
	
	/**
	 * Sets the value for GUI-Elements with the information from Object car
	 * @param car: the car showed on the interface
	 */
	public void setCar(Car car) {
		this.car = car;
		if (car.getPicturePath() != null) {
			// path to car's picture
			File file = new File(car.getPicturePath());
	        Image image = new Image(file.toURI().toString());
	        imageView.setImage(image);
		}
		manufacture.setText(car.getManufacture());
		model.setText(car.getModel());
		transmission.setText(car.getTransmission());
		fuelType.setText(car.getFuelType());
		km.setText(String.valueOf(car.getKm()));
		bodytype.setText(car.getBodyType());
		color.setText(car.getColor());
		warranty.setText(String.valueOf(car.getWarranty()));
		price.setText(String.valueOf(car.getMaxprice()));
		agent_name.setText(car.getAgent());
	}
	
	/**
	 * get-Method for the car showed on this GUI-block
	 * @return an Car-object 
	 */
	public Car getCar() {
		return car;
	}
	
	/**
	 * set-Method for the buyer agent, who is looking for cars
	 * @return an Car-object 
	 */
	public void setBuyerAgent(BuyerAgent buyerAgent) {
		this.buyerAgent = buyerAgent;
	}
	
	public boolean getValueChoosenCB() {
		return checkbox.isSelected();
	}
}
