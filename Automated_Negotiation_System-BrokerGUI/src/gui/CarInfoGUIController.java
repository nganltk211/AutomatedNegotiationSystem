package gui;

import java.io.File;

import agents.BuyerAgent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Car;

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
	
	private Car car;
	private BuyerAgent buyerAgent;
	
	public CarInfoGUIController() {
		super();
	}
	
	public void setCar(Car car) {
		this.car = car;
		if (car.getPicturePath() != null) {
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
		//price.setText(String.valueOf(car.getMaxprice()));
		price.setText(String.valueOf(car.getMinprice()));
		agent_name.setText(car.getAgent());
	}
	
	public Car getCar() {
		return car;
	}
	
	public void setBuyerAgent(BuyerAgent buyerAgent) {
		this.buyerAgent = buyerAgent;
	}
	
	@FXML
	public void buttonNegotiateClick(ActionEvent action) {
		NegotiationChoiceGUI negotiationChoice = new NegotiationChoiceGUI(buyerAgent, "", car);
	}
}
