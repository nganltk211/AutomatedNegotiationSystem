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

/**
 * A Controller class of BuyerGUI. The logic of GUI-Elements will be defined in
 * this class.
 */
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

	// empty constructor
	public BuyerGUIController() {
	}

	/**
	 * Calls method to set the data for Comboboxes on GUI
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setDataComboBox();
		priceValidationLabel.setText("");
	}

	/**
	 * Event Listener on Button[#search_id].onAction
	 * The buyer agent sends a request to the broker agent to get a list of suitable cars
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void buttonSearchClick(ActionEvent event) throws IOException {
		boolean price = FormValidation.textFieldNotEmpty(max_id, priceValidationLabel, "Please Enter Max Price");

		// numbers Validation
		if (price) {
			try {
				if (Double.parseDouble(max_id.getText()) < 1000) {
					priceValidationLabel.setText("Please Enter Above 1000");
				} else {
					Car searchCar = new Car(0);
					searchCar.setManufacture(manufacture_id.getValue());
					searchCar.setModel(model_id.getValue());
					searchCar.setTransmission(transmission_id.getValue());
					searchCar.setBodyType(body_id.getValue());
					searchCar.setColor(color_id.getValue());
					searchCar.setFuelType(fueltype_id.getValue());
					searchCar.setMaxprice(Double.parseDouble(max_id.getText()));
					buyerAgent.setReservationPrice(Double.parseDouble(max_id.getText()));
					// a request to the broker to get a list of suitable cars
					buyerAgent.requestInfoOfDesiredCar(searchCar);
				}
			} catch (NumberFormatException e) {
				priceValidationLabel.setText("Please Enter Number Value");
			}
		}
	}

	/**
	 * Event Listener on the combox manufacture.
	 * Changing the value of manufacture combobox will update the values of model combobox
	 * @param event
	 */
	public void ComboChanged(ActionEvent event) {
		setModelComboBox();
	}

	/**
	 * Sets data for Comboboxes on GUI.
	 */
	private void setDataComboBox() {
		// defines lists storing data for comboboxes
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

		// sets data
		body_id.setItems(bodyList);
		manufacture_id.setItems(manufactureList);
		transmission_id.setItems(transmissionList);
		fueltype_id.setItems(fuelList);
		color_id.setItems(colorList);
		model_id.setItems(modelAllList);

		// sets prompt text
		model_id.setPromptText("No Select");
		manufacture_id.setPromptText("No Select");
		body_id.setPromptText("No Select");
		transmission_id.setPromptText("No Select");
		color_id.setPromptText("No Select");
		fueltype_id.setPromptText("No Select");
		model_id.setPromptText("No Select");
	}

	/**
	 * Sets data for car model combobox on GUI
	 */
	private void setModelComboBox() {
		// defines lists storing data of different models
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

		// sets data for the model combobox depending on selected value of manufacture combobox
		if (manufacture_id.getValue() == "Audi") {
			model_id.setItems(modelAudiList);
		} else if (manufacture_id.getValue() == "Toyota") {
			model_id.setItems(modelToyotaList);
		} else if (manufacture_id.getValue() == "Honda") {
			model_id.setItems(modelHondaList);
		} else if (manufacture_id.getValue() == "BMW") {
			model_id.setItems(modelBmwList);
		} else if (manufacture_id.getValue() == "Holden") {
			model_id.setItems(modelHoldenList);
		} else if (manufacture_id.getValue() == "Nissan") {
			model_id.setItems(modelNissanList);
		}
	}

	/**
	 * set-Method for "buyerAgent" attribute.
	 * @param bag : buyer agent
	 */
	public void setAgent(BuyerAgent bag) {
		buyerAgent = bag;
	}
}
