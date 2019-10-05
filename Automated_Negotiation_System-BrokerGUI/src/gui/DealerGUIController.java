package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;

import javafx.scene.control.Label;

import agents.DealerAgent;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.Car;
import model.CarList;
import model.FormValidation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;

/**
 * A Controller class of DealerGUI. The logic of GUI-Elements will be defined in
 * this class.
 */
public class DealerGUIController implements Initializable {

	@FXML
	private ComboBox<String> manufacture_id;
	@FXML
	private ComboBox<String> model_id;
	@FXML
	private ComboBox<String> transmission_id;
	@FXML
	private ComboBox<String> colorid;
	@FXML
	private ComboBox<String> fuel_id;
	@FXML
	private ComboBox<String> body_id;
	@FXML
	private ComboBox<Integer> warrantyid;
	@FXML
	private ComboBox<Integer> rating_id;
	@FXML
	private TextField km_id;
	@FXML
	private TextField priceMin;
	@FXML
	private TextField priceMax;
	@FXML
	private TextField manu_year_id;
	@FXML
	private TextField picture_path;
	@FXML
	private TextField beetaValue;
	@FXML
	private TextField stepsValue;
	@FXML
	private Label beetaLabel;
	@FXML
	private Label stepsLabel;
	@FXML
	private Label manufactureValidationLabel;
	@FXML
	private Label modelValidationLabel;
	@FXML
	private Label transmissionValidationLabel;
	@FXML
	private Label bodyValidationLabel;
	@FXML
	private Label priceValidationLabel;
	@FXML
	private Label yearValidationLabel;
	@FXML
	private Label beetaValidationLabel;
	@FXML
	private Label stepsValidationLabel;
	@FXML
	private Button done_id;
	@FXML
	private Button cancel_id;
	@FXML
	private Button picture_btn;
	@FXML
	private RadioButton autoNego;
	@FXML
	private RadioButton manualNego;

	@FXML
	private TableColumn<Car, Integer> nr_column;
	@FXML
	private TableColumn<Car, String> manu_column;
	@FXML
	private TableColumn<Car, String> model_column;
	@FXML
	private TableColumn<Car, Double> minprice_column;
	@FXML
	private TableColumn<Car, Double> maxprice_column;
	@FXML
	private TableColumn<Car, String> details_column;
	@FXML
	private TableColumn<Car, String> manu_year_column;
	@FXML
	private TableView<Car> tableViewID;
	@FXML
	private ImageView helpMenu;
	private CarList listOfCars;
	private int carCounter;
	private DealerAgent dealerAgent;
	private ToggleGroup group;

	private boolean isvalidate = false;

	public DealerGUIController() {
		listOfCars = new CarList();
		carCounter = 0;
	}

	/**
	 * Method to initialize the values for GUI-Elements.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// creates a ToogleGroup for radio button to make sure that only one radio button is chosen
		group = new ToggleGroup();
		manualNego.setToggleGroup(group);
		autoNego.setToggleGroup(group);
		manualNego.setSelected(true);
		
		beetaValue.setVisible(false);
		stepsValue.setVisible(false);
		stepsLabel.setVisible(false);
		beetaLabel.setVisible(false);
		helpMenu.setVisible(false);
		beetaValidationLabel.setVisible(false);
		stepsValidationLabel.setVisible(false);

		manufactureValidationLabel.setText("");
		modelValidationLabel.setText("");
		bodyValidationLabel.setText("");
		transmissionValidationLabel.setText("");
		yearValidationLabel.setText("");
		priceValidationLabel.setText("");
		beetaValidationLabel.setText("");
		stepsValidationLabel.setText("");
		
		radioButtonValueChanged();
		setDataComboBox();
		setTableMenu();	
	}

	/**
	 * sets event for the (manual/automated) radio buttons on this GUI.
	 */
	public void radioButtonValueChanged() {
		// when the radio button in the toggle group is clicked
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
				if (autoNego.isSelected()) {
					// if automated negotiation is chosen
					beetaValue.setVisible(true);
					stepsValue.setVisible(true);
					stepsLabel.setVisible(true);
					beetaLabel.setVisible(true);
					helpMenu.setVisible(true);
					beetaValidationLabel.setVisible(true);
					stepsValidationLabel.setVisible(true);
				} else {
					// if manual negotiation is chosen
					beetaValue.setVisible(false);
					stepsValue.setVisible(false);
					stepsLabel.setVisible(false);
					beetaLabel.setVisible(false);
					helpMenu.setVisible(false);
					beetaValidationLabel.setVisible(false);
					stepsValidationLabel.setVisible(false);
				}
			}
		});
	}

	/**
	 * Sets the value for GUI-Elements with the information from Object car
	 * @param car: the car showed on the interface
	 */
	private void setValueForTheCarInfoGUI(Car car) {
		manufacture_id.setValue(car.getManufacture());
		model_id.setValue(car.getModel());
		transmission_id.setValue(car.getTransmission());
		colorid.setValue(car.getColor());
		fuel_id.setValue(car.getFuelType());
		body_id.setValue(car.getBodyType());
		rating_id.setValue(car.getCarrating());
		manu_year_id.setText(car.getManufactureYear());
		warrantyid.setValue(car.getWarranty());
		priceMin.setText(String.valueOf(car.getMinprice()));
		km_id.setText(String.valueOf(car.getKm()));
		picture_path.setText(car.getPicturePath());
		if (car.getisNegotiatable()) {
			manualNego.setSelected(true);
		} else {
			autoNego.setSelected(true);
			beetaValue.setText(String.valueOf(car.getBeeta()));
			stepsValue.setText(String.valueOf(car.getSteps()));
		}
	}

	/**
	 * sets a menu item "Edit" to table rows
	 */
	private void setTableMenu() {
		tableViewID.setRowFactory(new Callback<TableView<Car>, TableRow<Car>>() {
			@Override
			public TableRow<Car> call(TableView<Car> tableView) {
				final TableRow<Car> row = new TableRow<>();
				final ContextMenu contextMenu = new ContextMenu();
				final MenuItem editMenuItem = new MenuItem("Edit");
				// event when click on this menu item
				editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						tableViewID.getItems().remove(row.getItem()); // removes the row from the table
						setValueForTheCarInfoGUI(row.getItem()); // shows info of the deleted row 
						listOfCars.remove(row.getItem()); // removes the car from the list
					}
				});
				contextMenu.getItems().add(editMenuItem);
				// Set context menu on row, 
				// but use a binding to make it only show for non-empty rows:
				row.contextMenuProperty()
						.bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
				return row;
			}
		});
	}

	/**
	 * Event handler for beetaHelp option (to show the meaning of beeta to users)
	 * @param event
	 */
	public void openBeetaHelp(ActionEvent event) {

	}

	/**
	 * Event Listener on the combox manufacture.
	 * Changing the value of manufacture combobox will update the values of model combobox
	 * @param event
	 */
	public void onManufactureChanged(ActionEvent event) {
		setModelComboBox();
	}

	/**
	 * Sets data for Comboboxes on GUI
	 */
	private void setDataComboBox() {
		ObservableList<String> bodyList = FXCollections.observableArrayList("SUV", "Sedan", "HatchBack");
		ObservableList<String> manufactureList = FXCollections.observableArrayList("Audi", "Toyota", "Honda", "BMW",
				"Nissan", "Holden");
		ObservableList<String> transmissionList = FXCollections.observableArrayList("AMT", "Auto", "Manual");
		ObservableList<String> colorList = FXCollections.observableArrayList("Red", "Blue", "White", "Black", "Yellow",
				"Silver", "Grey");
		ObservableList<String> fuelList = FXCollections.observableArrayList("Diesel", "Gas", "Petrol");
		ObservableList<Integer> warrantyList = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5);
		ObservableList<String> modelAllList = FXCollections.observableArrayList("A1", "A2", "A3", "A4", "A5", "A6",
				"A7", "A8", "Camry", "Corrola", "Aurian", "Echo", "Crown", "Mark", "Accord", "Civic", "Legend",
				"Odysey", "Insight", "Seires 1", "Series 2", "Series 3", "Series 4", "Cima", "180xs", "200xs", "720",
				"Appolo", "Astra", "Brock");
		ObservableList<Integer> ratingList = FXCollections.observableArrayList(1, 2, 3, 4, 5);
		
		manufacture_id.setItems(manufactureList);
		body_id.setItems(bodyList);
		transmission_id.setItems(transmissionList);
		colorid.setItems(colorList);
		fuel_id.setItems(fuelList);
		warrantyid.setItems(warrantyList);
		model_id.setItems(modelAllList);
		rating_id.setItems(ratingList);
		picture_path.setText(null);
		setPromptText();
	}

	/**
	 * sets prompt text for comboboxes on GUI
	 */
	private void setPromptText() {
		manufacture_id.setPromptText("No Select");
		body_id.setPromptText("No Select");
		transmission_id.setPromptText("No Select");
		colorid.setPromptText("No Select");
		fuel_id.setPromptText("No Select");
		warrantyid.setPromptText("0");
		model_id.setPromptText("No Select");
		rating_id.setPromptText("0");
	}

	/**
	 * Sets data for car model combobox on GUI
	 */
	private void setModelComboBox() {
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
			model_id.setValue("A1");
		} else if (manufacture_id.getValue() == "Toyota") {
			model_id.setItems(modelToyotaList);
			model_id.setValue("Camry");
		} else if (manufacture_id.getValue() == "Honda") {
			model_id.setItems(modelHondaList);
			model_id.setValue("Accord");
		} else if (manufacture_id.getValue() == "BMW") {
			model_id.setItems(modelBmwList);
			model_id.setValue("Series 1");
		} else if (manufacture_id.getValue() == "Holden") {
			model_id.setItems(modelHoldenList);
		} else if (manufacture_id.getValue() == "Nissan") {
			model_id.setItems(modelNissanList);
			model_id.setValue("Cima");
		}
	}

	/**
	 * Method to check the validation of elements on GUI
	 * @return true if validate
	 */
	private boolean validation() {
		boolean manufacture = FormValidation.comboBoxNotSelected(manufacture_id, manufactureValidationLabel,
				"Please select manufacture type");
		boolean model = FormValidation.comboBoxNotSelected(model_id, modelValidationLabel, "Please select model type");
		boolean transmission = FormValidation.comboBoxNotSelected(transmission_id, transmissionValidationLabel,
				"Please select transmission type");
		boolean body = FormValidation.comboBoxNotSelected(body_id, bodyValidationLabel, "Please select body type");
		boolean year = FormValidation.textFieldNotEmpty(manu_year_id, yearValidationLabel,
				"Please enter manufacture year");
		boolean minPrice = FormValidation.textFieldNotEmpty(priceMin, priceValidationLabel,
				"Please enter minimum price");
		boolean maxPrice = FormValidation.textFieldNotEmpty(priceMax, priceValidationLabel,
				"Please enter maximum price");
		boolean beeta = FormValidation.textFieldNotEmpty(beetaValue, beetaValidationLabel, "Please enter Beeta Value ");
		boolean steps = FormValidation.textFieldNotEmpty(stepsValue, stepsValidationLabel, "Please enter steps Value ");

		if (!minPrice && !maxPrice) {
			FormValidation.textFieldNotEmpty(priceMax, priceValidationLabel, "Please enter minimum and maximum price");
		}
		if (autoNego.isSelected()) {
			if (!beeta && !steps) {
				isvalidate = false;
			}
		}
		if (manufacture && model && transmission && body && year && minPrice && maxPrice) {

			isvalidate = true;
		} else {
			isvalidate = false;
		}

		return isvalidate;
	}

	/**
	 * Event Listener on "Add" button to add a new car to the dealer car list
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void onButtonAddClick(ActionEvent event) throws IOException {
		validation();
		// adds a new car to the list
		if (isvalidate) {
			carCounter++;
			Car newCar = new Car(carCounter);
			newCar.setManufacture(manufacture_id.getValue());
			newCar.setModel(model_id.getValue());
			newCar.setTransmission(transmission_id.getValue());
			newCar.setBodyType(body_id.getValue());
			newCar.setColor(colorid.getValue());
			newCar.setFuelType(fuel_id.getValue());
			newCar.setKm(Integer.parseInt(km_id.getText()));
			if (warrantyid.getValue() != null) {
				newCar.setWarranty(warrantyid.getValue());
			}
			newCar.setManufactureYear(manu_year_id.getText());
			if (rating_id.getValue() != null) {
				newCar.setCarrating(rating_id.getValue());
			}
			if (manualNego.isSelected()) {
				// This is Manual Stuff
				newCar.setNegotiatable(true);
			} else {
				// This is Automated Stuff
				newCar.setNegotiatable(false);
				newCar.setBeeta(Double.parseDouble(beetaValue.getText()));
				newCar.setSteps(Integer.parseInt(stepsValue.getText()));
			}
			newCar.setMinprice(Double.parseDouble(priceMin.getText()));
			newCar.setMaxprice(Double.parseDouble(priceMax.getText()));

			setCarPicturePath(newCar);

			listOfCars.add(newCar);
			updateTable();
		}
	}

	/**
	 * Event Listener on "Reset" button.
	 * Reset values of GUI-elements relating to show car info to default value
	 *  
	 * @param event
	 * @throws IOException
	 */
	public void onButtonResetClick(ActionEvent event) throws IOException {
		setValueForTheCarInfoGUI(new Car(0));
		setPromptText();
	}

	/**
	 * sets selected picture path to the car and copies this image from local PC to image folder of the application
	 * @param newCar
	 */
	private void setCarPicturePath(Car newCar) {
		if (picture_path.getText() != null) {
			// copy the selected picture to the folder image
			File sourceFile = new File(picture_path.getText());
			File targetFile = new File("./image/");
			try {
				FileUtils.copyFileToDirectory(sourceFile, targetFile, true);
			} catch (IOException e) {
				System.out.println("Problem with copying one picture to the folder image");
			}
			newCar.setPicturePath("./image/" + sourceFile.getName());
		}
	}

	/**
	 * Event Listener on "Select" button.
	 * Opens a File Chooser dialog to select an image from local folder
	 * @param event
	 * @throws IOException
	 */
	public void selectPictureButton(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Please choose a car picture");
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null)
			// shows picture path on the picture_path text field
			picture_path.setText(selectedFile.getAbsolutePath());
	}

	/**
	 * Event Listener on "Send" button to send a list of cars to the broker agent.
	 * @param event
	 * @throws IOException
	 */
	public void onButtonSendClick(ActionEvent event) throws IOException {
		if (listOfCars.size() > 0) {
			// sends a list of cars to the broker agent 
			dealerAgent.sendListOfCarToBroker(listOfCars);
			((Node) (event.getSource())).getScene().getWindow().hide(); // hide the GUI
		}
	}

	/**
	 * Event Listener on "Load Sample Date" button to load a prepared list of cars.
	 * This is for the testing purpose
	 * @param event
	 */
	public void onButtonLoadSampleDataClick(ActionEvent event) {
		Car car1 = new Car(++carCounter);
		car1.setAgent(dealerAgent.getName());
		car1.setManufacture("Audi");
		car1.setModel("A2");
		car1.setMaxprice(20000);
		car1.setMinprice(18000);
		car1.setColor("Red");
		car1.setFuelType("Diesel");
		car1.setBodyType("SUV");
		car1.setCarrating(5);
		car1.setKm(5000);
		car1.setManufactureYear("2018");
		car1.setPicturePath("./image/auto1.jpg");
		car1.setTransmission("AMT");
		car1.setWarranty(0);
		car1.setNegotiatable(false);
		car1.setBeeta(0.8);
		car1.setSteps(20);
		
		Car car2 = new Car(++carCounter);
		car2.setAgent(dealerAgent.getName());
		car2.setManufacture("Toyota");
		car2.setModel("Camry");
		car2.setMaxprice(19100);
		car2.setMinprice(16900);
		car2.setColor("Blue");
		car2.setFuelType("Gas");
		car2.setBodyType("Sedan");
		car2.setCarrating(4);
		car2.setKm(7000);
		car2.setManufactureYear("2017");
		car2.setPicturePath("./image/auto2.jpg");
		car2.setTransmission("Auto");
		car2.setWarranty(0);
		car2.setNegotiatable(true);
		
		listOfCars.add(car1);
		listOfCars.add(car2);
		updateTable();
	}
	
	/**
	 * Updates the table data
	 */
	private void updateTable() {
		ObservableList<Car> obList = FXCollections.observableArrayList(listOfCars);
		tableViewID.setItems(obList);
		nr_column.setCellValueFactory(new PropertyValueFactory<Car, Integer>("carId"));
		manu_column.setCellValueFactory(new PropertyValueFactory<Car, String>("manufacture"));
		model_column.setCellValueFactory(new PropertyValueFactory<Car, String>("model"));
		minprice_column.setCellValueFactory(new PropertyValueFactory<Car, Double>("minprice"));
		maxprice_column.setCellValueFactory(new PropertyValueFactory<Car, Double>("maxprice"));
		details_column.setCellValueFactory(new PropertyValueFactory<Car, String>("moreDetails"));
		manu_year_column.setCellValueFactory(new PropertyValueFactory<Car, String>("manufactureYear"));
	}
	
	/**
	 * set-Method for "dealerAgent" attribute.
	 * @param dag
	 */
	public void setAgent(DealerAgent dag) {
		dealerAgent = dag;
	}

}
