package gui;

import java.io.IOException;

import jade.core.Agent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Car;

public class NegotiationBotGUI extends Stage {

	private FXMLLoader loader;

	public NegotiationBotGUI(Agent agent, String opponentAgentName, Car negotiatedCar, double offerPrice){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("NegotiationBot.fxml"));
		try {
			window = loader.load();
			NegotiationBotGUIController controller = loader.<NegotiationBotGUIController>getController();
			controller.setAgent(agent);
			controller.setOpponentAgentName(opponentAgentName);
			controller.setNegotiatedCar(negotiatedCar);
			controller.setPrice(offerPrice);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(agent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}
}
