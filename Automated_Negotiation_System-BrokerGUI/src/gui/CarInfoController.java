package gui;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	private Button btn_negotiate;
	@FXML
	private ImageView imageView;
	private Car car;
	
	public CarInfoController() {
		super();
	}
	
	@FXML
	public void negotiate(ActionEvent event) {
		System.out.println(car.getManufacture());
	}
	
	public void setCar(Car car) {
		this.car = car;
		File file = new File("./image/test.jpg");
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
		//manufacture.setText(car.getManufacture());
		//model.setText(car.getModel());
	}
}
