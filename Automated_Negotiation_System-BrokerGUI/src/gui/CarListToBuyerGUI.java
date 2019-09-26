package gui;

import java.io.IOException;

import agents.BuyerAgent;
import jade.core.Agent;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Car;
import model.CarList;

public class CarListToBuyerGUI extends Stage {

	private FXMLLoader loader;
	private CarInfoGUIController[] carController;
	private BuyerAgent buyerAgent;
	//private CarList choosenOffers;
	
	public CarListToBuyerGUI(CarList offerCarlist, Agent myAgent){
		//choosenOffers = new CarList();
		carController = new CarInfoGUIController[offerCarlist.size()];
		buyerAgent = (BuyerAgent) myAgent;
		ScrollPane sp = new ScrollPane();
		VBox root = new VBox();
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		GridPane window = null;
		for (int i = 0; i < offerCarlist.size(); i++) {
			loader = new FXMLLoader(getClass().getResource("CarInformation.fxml"));
			try {
				window = loader.load();
				carController[i] = loader.getController();
		        // Set data in the controller
				carController[i].setCar(offerCarlist.get(i));
				
				carController[i].setBuyerAgent(buyerAgent);
				window.setStyle("-fx-background-color: #ffffff");
				root.getChildren().add(window);
			} catch (IOException e) {
				System.err.println("Error by loading fxml-File");
			}	
		}	
		this.setTitle("List of possible cars");
		sp.setContent(root);
        sp.setPannable(true); 
		Scene scene = new Scene(sp,710,850);
		this.setScene(scene);
		this.setResizable(false);
		this.show();
	}
}
