package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;

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
	public void OnButtonDone(ActionEvent event) throws IOException {
		
		
	}
	public void OnButtonCancel(ActionEvent event) throws IOException {
		
		
	}
	public void onAddButtonClick(ActionEvent event) throws IOException {
		
		setMoreList();
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		setDataComboBox();	
	}
	
	public void onManufactiureChanged(ActionEvent event) {
		setModelComboBox();
	}
	public void setMoreList()
	{
		SellerController[] cont = new SellerController[1];
		cont[0] = new SellerController();
		System.out.println("Hello");
		Stage stage = new SellerGUI();
		stage.show();
		
	}
	private void setDataComboBox() {
		// TODO Auto-generated method stub
		ObservableList<String> bodyList = FXCollections.observableArrayList("SUV", "Sedan","HatchBack");
		ObservableList<String> manufactureList = FXCollections.observableArrayList("Audi","Toyota","Honda","BMW","Nissan","Holden");
		ObservableList<String> transmissionList = FXCollections.observableArrayList("AMT", "Auto","Manual");
		ObservableList<String> colorList = FXCollections.observableArrayList("Red", "Blue","White","Black","Yellow","Silver","Grey");
		ObservableList<String> fuelList = FXCollections.observableArrayList("Deisel", "Gas","Petrol");
		ObservableList<String> warrantyList = FXCollections.observableArrayList("1 year", "2 year","3 year","4 year","5 year");
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
			model_id.setPromptText("A1");
		}else if(manufacture_id.getValue() == "Toyota")
		{
			model_id.setItems(modelToyotaList);
			model_id.setPromptText("Camry");
		}else if(manufacture_id.getValue() == "Honda")
		{
			model_id.setItems(modelHondaList);
			model_id.setPromptText("Accord");
		}else if(manufacture_id.getValue() == "BMW")
		{
			model_id.setItems(modelBmwList);
			model_id.setPromptText("Series 1");
		}else if(manufacture_id.getValue() == "Holden")
		{
			model_id.setItems(modelHoldenList);
		}else if(manufacture_id.getValue() == "Nissan")
		{
			model_id.setItems(modelNissanList);
			model_id.setPromptText("Cima");
		}
		
	}
			
	

}
