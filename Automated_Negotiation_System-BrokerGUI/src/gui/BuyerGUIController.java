package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import model.Car;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.ComboBox;

import javafx.scene.control.TextArea;
import model.Car;


public class BuyerGUIController implements Initializable{
	@FXML
	private ComboBox<String> body_id;
	@FXML
	private ComboBox<String> manufacture_id;
	@FXML
	private ComboBox<String> model_id;
	@FXML
	private ComboBox<String> Transmission_id;
	@FXML
	private ComboBox<String> color_id;
	@FXML
	private Text Trans;
	@FXML
	private TextArea min_id;
	@FXML
	private TextArea max_id;
	@FXML
	private Button search_id;
	
	

	
	public void ComboChanged(ActionEvent event) {
		setModelComboBox();
	}

	


	// Event Listener on Button[#search_id].onAction
	@FXML
	public void buttonSearchClick(ActionEvent event) throws IOException {
		Car[] listCars = new Car[3];
		listCars[0] = new Car(1);
		listCars[0].setManufacture("Test1");
		listCars[1] = new Car(2);
		listCars[1].setManufacture("Test2");
		listCars[2] = new Car(3);
		
		 //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CarInformation.fxml"));
       //  Parent root = (Parent) fxmlLoader.load();
         Stage stage = new ListBuyer_GUI(listCars) ;
         //stage.setScene(new Scene(root));  
         stage.show();
	//	model_id.setPromptText("A1");
		    
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		setDataComboBox();	
	}
	
	public void setDataComboBox()
	{
		
		ObservableList<String> bodyList = FXCollections.observableArrayList("SUV", "Sedan","HatchBack");
		ObservableList<String> manufactureList = FXCollections.observableArrayList("Audi","Toyota","Honda","BMW","Nissan","Holden");
		ObservableList<String> transmissionList = FXCollections.observableArrayList("AMT", "Auto","Manual");
		ObservableList<String> colorList = FXCollections.observableArrayList("Red", "Blue","White","Black","Yellow","Silver","Grey");
		
		//ModelList
		body_id.setItems(bodyList);
		manufacture_id.setItems(manufactureList);
		
		
		Transmission_id.setItems(transmissionList);
		color_id.setItems(colorList);
		
		
		manufacture_id.setPromptText("Audi");
		body_id.setPromptText("SUV");
		Transmission_id.setPromptText("AMT");
		color_id.setPromptText("Red");
		
	}
	public void setModelComboBox()
	{
		ObservableList<String> modelAudiList = FXCollections.observableArrayList("A1","A2","A3","A4","A5","A6","A7","A8");
		ObservableList<String> modelToyotaList = FXCollections.observableArrayList("Camry","Corrola","Aurian","Echo","Crown","Mark");
		ObservableList<String> modelHondaList = FXCollections.observableArrayList("Accord","Civic","Legend","Odysey","Insight");
		ObservableList<String> modelBmwList = FXCollections.observableArrayList("Seires 1","Series 2","Series 3","Series 4");
		ObservableList<String> modelNissanList = FXCollections.observableArrayList("Cima","180xs","200xs","720");
		ObservableList<String> modelHoldenList = FXCollections.observableArrayList("Appolo","Astra","Brock");
		
		ObservableList<String> modelAllList = FXCollections.observableArrayList("A1","A2","A3","A4","A5","A6","A7","A8","Camry","Corrola","Aurian","Echo","Crown","Mark"
				,"Accord","Civic","Legend","Odysey","Insight","Seires 1","Series 2","Series 3","Series 4","Cima","180xs","200xs","720","Appolo","Astra","Brock");
		
		
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
		}else if(manufacture_id.getValue() == null){
			model_id.setItems(modelAllList);
			model_id.setPromptText("Appolo");
		}
		
	}




	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
