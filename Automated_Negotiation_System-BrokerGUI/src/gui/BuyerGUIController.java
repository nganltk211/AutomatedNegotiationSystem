package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.Car;
import model.FormValidation;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import agents.BuyerAgent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BuyerGUIController implements Initializable {
	@FXML
	private ComboBox<String> body_id;
	@FXML
	private ComboBox<String> manufacture_id;
	@FXML
	private ComboBox<String> model_id;
	@FXML
	private ComboBox<String> transmission_id;
	@FXML
	private ComboBox<String> color_id;
	@FXML
	private ComboBox<String> fueltype_id;
	@FXML
	private Label priceValidationLabel;
	@FXML
	private Text Trans;
	@FXML
	private TextField max_id;
	@FXML
	private Button search_id;

	private BuyerAgent buyerAgent;

	public BuyerGUIController() {
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setDataComboBox();
		priceValidationLabel.setText("");
	}

	// Event Listener on Button[#search_id].onAction
	@FXML
	public void buttonSearchClick(ActionEvent event) throws IOException {
		boolean price = FormValidation.textFieldNotEmpty(max_id, priceValidationLabel, "Please Enter Max Price");
		
		
		//numbers Validation

       
		if(price)
		{
			try {	
			if(Double.parseDouble(max_id.getText()) < 1000)
			{
				priceValidationLabel.setText("Please Enter Ablove 1000");
			}
			else {
				Car searchCar = new Car(0);
				searchCar.setManufacture(manufacture_id.getValue());
				searchCar.setModel(model_id.getValue());
				searchCar.setTransmission(transmission_id.getValue());
				searchCar.setBodyType(body_id.getValue());
				searchCar.setColor(color_id.getValue());
				searchCar.setFuelType(fueltype_id.getValue());
				searchCar.setMaxprice(Double.parseDouble(max_id.getText()));
				buyerAgent.setReservationPrice(Double.parseDouble(max_id.getText()));
				buyerAgent.requestInfoOfDesiredCar(searchCar);
				//((Node) (event.getSource())).getScene().getWindow().hide();
			}}catch(NumberFormatException e)
			{
				priceValidationLabel.setText("Please Enter Number Value");
			}
			
		}
		
		
	}

	public void ComboChanged(ActionEvent event) {
		setModelComboBox();
	}

	public void setDataComboBox() {
		ObservableList<String> bodyList = FXCollections.observableArrayList("SUV", "Sedan", "HatchBack");
		ObservableList<String> manufactureList = FXCollections.observableArrayList("Audi", "Toyota", "Honda", "BMW",
				"Nissan", "Holden");
		ObservableList<String> transmissionList = FXCollections.observableArrayList("AMT", "Auto", "Manual");
		ObservableList<String> colorList = FXCollections.observableArrayList("Red", "Blue", "White", "Black", "Yellow",
				"Silver", "Grey");
		ObservableList<String> fuelList = FXCollections.observableArrayList("Diesel", "Gas", "Petrol");
		ObservableList<String> modelAllList = FXCollections.observableArrayList("A1", "A2", "A3", "A4", "A5", "A6",
				"A7", "A8", "Camry", "Corrola", "Aurian", "Echo", "Crown", "Mark", "Accord", "Civic", "Legend",
				"Odysey", "Insight", "Seires 1", "Series 2", "Series 3", "Series 4", "Cima", "180xs", "200xs", "720",
				"Appolo", "Astra", "Brock");

		// ModelList
		body_id.setItems(bodyList);
		manufacture_id.setItems(manufactureList);
		transmission_id.setItems(transmissionList);
		fueltype_id.setItems(fuelList);
		color_id.setItems(colorList);
		model_id.setItems(modelAllList);

		model_id.setPromptText("No Select");
		manufacture_id.setPromptText("No Select");
		body_id.setPromptText("No Select");
		transmission_id.setPromptText("No Select");
		color_id.setPromptText("No Select");
		fueltype_id.setPromptText("No Select");
		model_id.setPromptText("No Select");
	}

	public void setModelComboBox() {
		ObservableList<String> modelAudiList = FXCollections.observableArrayList("A1", "A2", "A3", "A4", "A5", "A6",
				"A7", "A8");
		ObservableList<String> modelToyotaList = FXCollections.observableArrayList("Camry", "Corrola", "Aurian", "Echo",
				"Crown", "Mark");
		ObservableList<String> modelHondaList = FXCollections.observableArrayList("Accord", "Civic", "Legend", "Odysey",
				"Insight");
		ObservableList<String> modelBmwList = FXCollections.observableArrayList("Seires 1", "Series 2", "Series 3",
				"Series 4");
		ObservableList<String> modelNissanList = FXCollections.observableArrayList("Cima", "180xs", "200xs", "720");
		ObservableList<String> modelHoldenList = FXCollections.observableArrayList("Appolo", "Astra", "Brock");

		if (manufacture_id.getValue() == "Audi") {
			model_id.setItems(modelAudiList);
			//model_id.setPromptText("A1");
		} else if (manufacture_id.getValue() == "Toyota") {
			model_id.setItems(modelToyotaList);
			//model_id.setPromptText("Camry");
		} else if (manufacture_id.getValue() == "Honda") {
			model_id.setItems(modelHondaList);
			//model_id.setPromptText("Accord");
		} else if (manufacture_id.getValue() == "BMW") {
			model_id.setItems(modelBmwList);
			//model_id.setPromptText("Series 1");
		} else if (manufacture_id.getValue() == "Holden") {
			model_id.setItems(modelHoldenList);
		} else if (manufacture_id.getValue() == "Nissan") {
			model_id.setItems(modelNissanList);
			//model_id.setPromptText("Cima");
		}
	}

	public void setAgent(BuyerAgent ag) {
		buyerAgent = ag;
	}
}
