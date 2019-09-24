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
	private CarList choosenOffers;
	
	public CarListToBuyerGUI(CarList offerCarlist, Agent myAgent){
		choosenOffers = new CarList();
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
				window.setStyle("-fx-background-color: #ffffff");
				root.getChildren().add(window);
			} catch (IOException e) {
				System.err.println("Error by loading fxml-File");
			}	
		}	
		CheckBox negotiationChoice = new CheckBox("Manual");
		Button sendbtn = new Button("Send");
		setActionForSendButton(sendbtn,negotiationChoice);
		sendbtn.setPrefWidth(100);
		this.setTitle("List of possible cars");
		sp.setContent(root);
        sp.setPannable(true); 
              
        HBox hBox = new HBox();
        hBox.setSpacing(100);
        hBox.getChildren().addAll(negotiationChoice,sendbtn);
        
        VBox vBox = new VBox();
		vBox.setSpacing(10);
		vBox.getChildren().addAll(sp, hBox);
		vBox.setPadding(new Insets(5));
		Scene scene = new Scene(vBox,710,850);
		this.setScene(scene);
		this.setResizable(false);
		this.show();
	}
	
	private void setActionForSendButton(Button btn, CheckBox negotiationChoice) {
		btn.setOnAction((ActionEvent me) -> {
			for(CarInfoGUIController controller : carController) {
				if (controller.getValueChoosenCB()) {
					choosenOffers.add(controller.getCar());
				}
			}
			if (choosenOffers.size()>0) {
				buyerAgent.sendBackTheChoosenCarsToTheBroker(choosenOffers);
				buyerAgent.setNegotiationManual(negotiationChoice.isSelected());
				this.close();
			}	
		});
		
	}
}