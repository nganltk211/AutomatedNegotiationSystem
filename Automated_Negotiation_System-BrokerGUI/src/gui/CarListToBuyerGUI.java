package gui;

import java.io.IOException;

import agents.BuyerAgent;
import jade.core.Agent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.CarList;

/**
 * A class for setting and showing a stage CarListToBuyerGUI
 */
public class CarListToBuyerGUI extends Stage {

	private FXMLLoader loader;
	private CarInfoGUIController[] carController;
	private BuyerAgent buyerAgent;
	
	/**
	 * Constructor of the class.
	 * @param offerCarlist : list of possible cars to show the buyer
	 * @param myAgent : a buyer agent
	 */
	public CarListToBuyerGUI(CarList offerCarlist, Agent myAgent){
		double blockHeight = 0;
		carController = new CarInfoGUIController[offerCarlist.size()];
		buyerAgent = (BuyerAgent) myAgent;
		ScrollPane sp = new ScrollPane();
		VBox root = new VBox();
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		GridPane window = null;
		for (int i = 0; i < offerCarlist.size(); i++) {
			// // loads GUI-Elements for a car block info from the CarInformation.fxml file
			loader = new FXMLLoader(getClass().getResource("CarInformation.fxml"));
			try {
				window = loader.load();
				carController[i] = loader.getController();
		        // Set data in the controller
				carController[i].setCar(offerCarlist.get(i));
				
				carController[i].setBuyerAgent(buyerAgent);
				window.setStyle("-fx-background-color: #ffffff");
				root.getChildren().add(window);
				blockHeight = window.getPrefHeight();
			} catch (IOException e) {
				System.err.println("Error by loading fxml-File");
			}	
		}	
		this.setTitle("List of possible cars");
		sp.setContent(root);
        sp.setPannable(true); 
        Scene scene;
        if (offerCarlist.size()<=2) {
        	scene = new Scene(sp,700,blockHeight*offerCarlist.size()+30);
        } else {
        	scene = new Scene(sp,710,blockHeight*2+30);
        }	
		this.setScene(scene);
		this.setResizable(false);
		this.show();
	}
}
