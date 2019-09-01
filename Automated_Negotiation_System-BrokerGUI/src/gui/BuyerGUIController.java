package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.Car;

import javafx.scene.text.Text;

import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.ComboBox;

import javafx.scene.control.TextArea;

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
	
	ObservableList<String> BodyList = FXCollections.observableArrayList("SUV", "Sedan","HatchBack");
	ObservableList<String> ManufactureList = FXCollections.observableArrayList("Audi","Toyota","Honda","BMW","Nissan","Holden");
	ObservableList<String> TransmissionList = FXCollections.observableArrayList("AMT", "Auto","Manual");
	ObservableList<String> ColorList = FXCollections.observableArrayList("Red", "Blue","White","Black","Yellow","Silver","Grey");
	
	//ModelList
	ObservableList<String> ModelAudiList = FXCollections.observableArrayList("A1","A2","A3","A4","A5","A6","A7","A8");
	ObservableList<String> ModelToyotaList = FXCollections.observableArrayList("Camry","Corrola","Aurian","Echo","Crown","Mark");
	ObservableList<String> ModelHondaList = FXCollections.observableArrayList("Accord","Civic","Legend","Odysey","Insight");
	ObservableList<String> ModelBmwList = FXCollections.observableArrayList("Seires 1","Series 2","Series 3","Series 4");
	ObservableList<String> ModelNissanList = FXCollections.observableArrayList("Cima","180xs","200xs","720");
	ObservableList<String> ModelHoldenList = FXCollections.observableArrayList("Appolo","Astra","Brock");
	


	// Event Listener on Button[#search_id].onAction
	@FXML
	public void buttonSearchClick(ActionEvent event) {
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		body_id.setItems(BodyList);
		manufacture_id.setItems(ManufactureList);
		//model_id.setItems();
		
		Transmission_id.setItems(TransmissionList);
		color_id.setItems(ColorList);
		
		
		
	}
}
