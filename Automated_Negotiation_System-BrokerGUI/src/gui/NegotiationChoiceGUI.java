package gui;

import java.io.IOException;

import agents.BuyerAgent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Car;

public class NegotiationChoiceGUI extends Stage {

	private FXMLLoader loader;

	public NegotiationChoiceGUI(BuyerAgent agent, Car negotiatedCar){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("ChoiceOfNegotiationWay.fxml"));
		try {
			window = loader.load();
			NegotiationChoiceGUIController controller = loader.<NegotiationChoiceGUIController>getController();
			controller.setAgent(agent);
			//controller.setOpponentAgentName(name);
			controller.setNegotiatedCar(negotiatedCar);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(agent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}
}
