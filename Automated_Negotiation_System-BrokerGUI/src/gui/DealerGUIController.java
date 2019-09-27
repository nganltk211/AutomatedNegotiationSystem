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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.Car;
import model.CarList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;

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
	private TableColumn<Car, Double> price_column;
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

	public DealerGUIController() {
		listOfCars = new CarList();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		group = new ToggleGroup();
		manualNego.setToggleGroup(group);
		autoNego.setToggleGroup(group);
		manualNego.setSelected(true);
		beetaValue.setVisible(false);
		stepsValue.setVisible(false);
		stepsLabel.setVisible(false);
		beetaLabel.setVisible(false);
		helpMenu.setVisible(false);
		radioButtonValueChanged();
		setDataComboBox();
		setTableMenu();
	}
	public void radioButtonValueChanged() {
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
				if(autoNego.isSelected())
				{
					beetaValue.setVisible(true);
					stepsValue.setVisible(true);
					stepsLabel.setVisible(true);
					beetaLabel.setVisible(true);
					helpMenu.setVisible(true);
				}else {
					beetaValue.setVisible(false);
					stepsValue.setVisible(false);
					stepsLabel.setVisible(false);
					beetaLabel.setVisible(false);
					helpMenu.setVisible(false);
				}
			}
		});
		
	}

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
	}
	
	private void setTableMenu() {
		tableViewID.setRowFactory(new Callback<TableView<Car>, TableRow<Car>>() {  
            @Override  
            public TableRow<Car> call(TableView<Car> tableView) {  
                final TableRow<Car> row = new TableRow<>();  
                final ContextMenu contextMenu = new ContextMenu();  
                final MenuItem removeMenuItem = new MenuItem("Edit");  
                removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {  
                    @Override  
                    public void handle(ActionEvent event) {  
                    	tableViewID.getItems().remove(row.getItem());  
                    	setValueForTheCarInfoGUI(row.getItem());
                    	listOfCars.remove(row.getItem());
                    }  
                });  
                contextMenu.getItems().add(removeMenuItem);  
               // Set context menu on row, but use a binding to make it only show for non-empty rows:  
                row.contextMenuProperty().bind(  
                        Bindings.when(row.emptyProperty())  
                        .then((ContextMenu)null)  
                        .otherwise(contextMenu)  
                );  
                return row ;  
            }
        });  
	}
	public void openBeetaHelp(ActionEvent event)
	{
		
	}
	public void onManufactureChanged(ActionEvent event) {
		setModelComboBox();
	}

	private void setDataComboBox() {
		ObservableList<String> bodyList = FXCollections.observableArrayList("SUV", "Sedan", "HatchBack");
		ObservableList<String> manufactureList = FXCollections.observableArrayList("Audi", "Toyota", "Honda", "BMW",
				"Nissan", "Holden");
		ObservableList<String> transmissionList = FXCollections.observableArrayList("AMT", "Auto", "Manual");
		ObservableList<String> colorList = FXCollections.observableArrayList("Red", "Blue", "White", "Black", "Yellow",
				"Silver", "Grey");
		ObservableList<String> fuelList = FXCollections.observableArrayList("Diesel", "Gas", "Petrol");
		ObservableList<Integer> warrantyList = FXCollections.observableArrayList(1, 2, 3, 4, 5);
		ObservableList<String> modelAllList = FXCollections.observableArrayList("A1", "A2", "A3", "A4", "A5", "A6",
				"A7", "A8", "Camry", "Corrola", "Aurian", "Echo", "Crown", "Mark", "Accord", "Civic", "Legend",
				"Odysey", "Insight", "Seires 1", "Series 2", "Series 3", "Series 4", "Cima", "180xs", "200xs", "720",
				"Appolo", "Astra", "Brock");

		ObservableList<Integer> ratingList = FXCollections.observableArrayList(1,2,3,4,5);
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

	@FXML
	public void OnButtonAddClick(ActionEvent event) throws IOException {
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
		if(manualNego.isSelected())
		{
			//This is Manual Stuff
			newCar.setNegotiatable(true);
		}else {
			//This Is Automated Stuff
			newCar.setNegotiatable(false);
			newCar.setBeeta(Double.parseDouble(beetaValue.getText()));
			newCar.setSteps(Integer.parseInt(stepsValue.getText()));
		}
		newCar.setMinprice(Double.parseDouble(priceMin.getText()));
		newCar.setMaxprice(Double.parseDouble(priceMax.getText()));
	
		setCarPicturePath(newCar);
		
		listOfCars.add(newCar);
		ObservableList<Car> obList = FXCollections.observableArrayList(listOfCars);
		tableViewID.setItems(obList);
		updateTable();
		
	}

	public void OnButtonResetClick(ActionEvent event) throws IOException {
		setValueForTheCarInfoGUI(new Car(0));
		setPromptText();
	}
	
	private void setCarPicturePath(Car newCar) {
		if (picture_path.getText() != null) {
			//copy the selected picture to the folder image
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
	
	public void selectPictureButton(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Please choose one picture");
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) picture_path.setText(selectedFile.getAbsolutePath());
	}

	public void onButtonSendClick(ActionEvent event) throws IOException {
		dealerAgent.sendListOfCarToBroker(listOfCars);
		//dealerAgent.setBeeta(Double.parseDouble(beetaValue.getText()));
	    //dealerAgent.setMaxStep(Integer.parseInt(stepsValue.getText()));
		((Node) (event.getSource())).getScene().getWindow().hide();
	}

	private void updateTable() {
		nr_column.setCellValueFactory(new PropertyValueFactory<Car, Integer>("carId"));
		manu_column.setCellValueFactory(new PropertyValueFactory<Car, String>("manufacture"));
		model_column.setCellValueFactory(new PropertyValueFactory<Car, String>("model"));
		price_column.setCellValueFactory(new PropertyValueFactory<Car, Double>("minprice"));
		details_column.setCellValueFactory(new PropertyValueFactory<Car, String>("moreDetails"));
		manu_year_column.setCellValueFactory(new PropertyValueFactory<Car, String>("manufactureYear"));
	}

	public void setAgent(DealerAgent ag) {
		dealerAgent = ag;
	}

}
