package gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Car;

public class CarInfoController {
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
	private CheckBox checkbox;
	@FXML
	private ImageView imageView;
	
	private Car car;
	
	public CarInfoController() {
		super();
	}
	
	public void setCar(Car car) {
		this.car = car;
		File file = new File("./image/test.jpg");
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
		manufacture.setText(car.getManufacture());
		model.setText(car.getModel());
		transmission.setText(car.getTransmission());
		fuelType.setText(car.getFuelType());
		km.setText(String.valueOf(car.getKm()));
		bodytype.setText(car.getBodyType());
		color.setText(car.getColor());
		warranty.setText(String.valueOf(car.getWarranty()));
		price.setText(String.valueOf(car.getPrice()));
	}
	
	public Car getCar() {
		return car;
	}
	
	public boolean getValueChoosenCB() {
		return checkbox.isSelected();
	}
}
