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

	public NegotiationBotGUI(Agent hostAgent, Agent opponentAgent, Car negotiatedCar){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("NegotiationBot.fxml"));
		try {
			window = loader.load();
			NegotiationBotController controller = loader.<NegotiationBotController>getController();
//			controller.setAgent(agent);
//			controller.setOpponentAgentName(buyerName);
//			controller.setNegotiatedCar(negotiatedCar);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(hostAgent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}
}
