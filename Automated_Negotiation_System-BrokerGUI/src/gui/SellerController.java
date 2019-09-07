package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import agents.DealerAgent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Car;
import model.CarList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SellerController implements Initializable {
	
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
	private ComboBox<String> warrantyid;
	@FXML
	private ComboBox<String> rating_id;
	@FXML
	private TextField km_id;
	@FXML
	private TextField priceMaxId;
	@FXML
	private Button done_id;
	@FXML
	private Button cancel_id;
	@FXML
	private TextField priceMin;
	@FXML
	private TextField changeOfYearId;
	
	@FXML
	private TableColumn<Car, Integer> nr_column;
	@FXML
	private TableColumn<Car, String> manu_column;
	@FXML
	private TableColumn<Car, String> model_column;
	@FXML
	private TableColumn<Car, Double> maxprice_column;
	@FXML
	private TableColumn<Car, Double> minprice_column;
	@FXML
	private TableColumn<Car, String> details_column;
	@FXML
	private TableView<Car> tableViewID;
	
	private CarList listOfCars;
	private int carCounter;
	private DealerAgent dealerAgent;
	
	public SellerController() {
		listOfCars = new CarList();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {	
		setDataComboBox();	
		
	}
	
	public void onManufactureChanged(ActionEvent event) {
		setModelComboBox();
	}
	
	private void setDataComboBox() {
		ObservableList<String> bodyList = FXCollections.observableArrayList("SUV", "Sedan","HatchBack");
		ObservableList<String> manufactureList = FXCollections.observableArrayList("Audi","Toyota","Honda","BMW","Nissan","Holden");
		ObservableList<String> transmissionList = FXCollections.observableArrayList("AMT", "Auto","Manual");
		ObservableList<String> colorList = FXCollections.observableArrayList("Red", "Blue","White","Black","Yellow","Silver","Grey");
		ObservableList<String> fuelList = FXCollections.observableArrayList("Diesel", "Gas","Petrol");
		ObservableList<String> warrantyList = FXCollections.observableArrayList("1", "2","3","4","5");
		ObservableList<String> modelAllList = FXCollections.observableArrayList("A1","A2","A3","A4","A5","A6","A7","A8","Camry","Corrola","Aurian","Echo","Crown","Mark"
				,"Accord","Civic","Legend","Odysey","Insight","Seires 1","Series 2","Series 3","Series 4","Cima","180xs","200xs","720","Appolo","Astra","Brock");
		
		//ObservableList<String> ratingList = FXCollections.observableArrayList("Red", "Blue","White","Black","Yellow","Silver","Grey");
		manufacture_id.setItems(manufactureList);
		body_id.setItems(bodyList);
		transmission_id.setItems(transmissionList);
		colorid.setItems(colorList);
		fuel_id.setItems(fuelList);
		warrantyid.setItems(warrantyList);
		model_id.setItems(modelAllList);
				
		manufacture_id.setPromptText("No Select");
		body_id.setPromptText("No Select");
		transmission_id.setPromptText("No Select");
		colorid.setPromptText("No Select");
		fuel_id.setPromptText("No Select");
		warrantyid.setPromptText("No Select");
		model_id.setPromptText("No Select");
	}
	
	public void setModelComboBox()
	{
		ObservableList<String> modelAudiList = FXCollections.observableArrayList("A1","A2","A3","A4","A5","A6","A7","A8");
		ObservableList<String> modelToyotaList = FXCollections.observableArrayList("Camry","Corrola","Aurian","Echo","Crown","Mark");
		ObservableList<String> modelHondaList = FXCollections.observableArrayList("Accord","Civic","Legend","Odysey","Insight");
		ObservableList<String> modelBmwList = FXCollections.observableArrayList("Seires 1","Series 2","Series 3","Series 4");
		ObservableList<String> modelNissanList = FXCollections.observableArrayList("Cima","180xs","200xs","720");
		ObservableList<String> modelHoldenList = FXCollections.observableArrayList("Appolo","Astra","Brock");
	
		if(manufacture_id.getValue() == "Audi")
		{
			model_id.setItems(modelAudiList);
			model_id.setValue("A1");
		}else if(manufacture_id.getValue() == "Toyota")
		{
			model_id.setItems(modelToyotaList);
			model_id.setValue("Camry");
		}else if(manufacture_id.getValue() == "Honda")
		{
			model_id.setItems(modelHondaList);
			model_id.setValue("Accord");
		}else if(manufacture_id.getValue() == "BMW")
		{
			model_id.setItems(modelBmwList);
			model_id.setValue("Series 1");
		}else if(manufacture_id.getValue() == "Holden")
		{
			model_id.setItems(modelHoldenList);
		}else if(manufacture_id.getValue() == "Nissan")
		{
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
			newCar.setWarranty(Integer.parseInt(warrantyid.getValue()));
		}	
		newCar.setMaxPrice(Double.parseDouble(priceMaxId.getText()));
		newCar.setMinPrice(Double.parseDouble(priceMin.getText()));	
		listOfCars.add(newCar);
		ObservableList<Car> obList = FXCollections.observableArrayList(listOfCars);
		tableViewID.setItems(obList);
		updateTable();
	}
	
	public void OnButtonResetClick(ActionEvent event) throws IOException {
			
	}
	
	public void onButtonSendClick(ActionEvent event) throws IOException {	
		dealerAgent.sendListOfCarToBroker(listOfCars);
		((Node)(event.getSource())).getScene().getWindow().hide(); 
	}
	
	private void updateTable() {
		nr_column.setCellValueFactory(new PropertyValueFactory<Car, Integer>("carId"));
		manu_column.setCellValueFactory(new PropertyValueFactory<Car, String>("manufacture"));
		model_column.setCellValueFactory(new PropertyValueFactory<Car, String>("model"));
		maxprice_column.setCellValueFactory(new PropertyValueFactory<Car, Double>("maxPrice"));
		minprice_column.setCellValueFactory(new PropertyValueFactory<Car, Double>("minPrice"));
		details_column.setCellValueFactory(new PropertyValueFactory<Car, String>("moreDetails"));
	}
	
	public void setAgent(DealerAgent ag) {
		dealerAgent = ag;
	}
	
}
