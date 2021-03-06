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
		ScrollPane sp = new ScrollPane(); // a scroll pane for showing list of cars
		VBox root = new VBox();
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		GridPane carBlock = null;
		for (int i = 0; i < offerCarlist.size(); i++) {
			// loads GUI-Elements for one car from the CarInformation.fxml file
			loader = new FXMLLoader(getClass().getResource("CarInformation.fxml"));
			try {
				carBlock = loader.load();
				carController[i] = loader.getController();
		        // Set data in the controller
				carController[i].setCar(offerCarlist.get(i));			
				carController[i].setBuyerAgent(buyerAgent);
				carBlock.setStyle("-fx-background-color: #ffffff");
				root.getChildren().add(carBlock); // adds carBlock to the scroll pane
				blockHeight = carBlock.getPrefHeight();
			} catch (IOException e) {
				System.err.println("Error by loading fxml-File");
			}	
		}	
		
		this.setTitle("List of possible cars");
		sp.setContent(root);
        sp.setPannable(true); 
        Scene scene;
        // sets the size of the window depending on the size of car list
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
