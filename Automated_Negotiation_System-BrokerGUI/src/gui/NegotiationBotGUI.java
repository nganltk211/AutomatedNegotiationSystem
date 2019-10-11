package gui;

import java.io.IOException;

import jade.core.Agent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Car;

/**
 * A class for setting and showing a stage NegotiationBotGUI.
 * This is an interface for a buyer/dealer to accept or reject offers (make a counter-offer) from other agents 
 */
public class NegotiationBotGUI extends Stage {

	private FXMLLoader loader; // uses to load the fxml-file

	/**
	 * Constructor of the class
	 * @param agent : the GUI is started from this agent 
	 * @param opponentAgentName : name of another agent in the negotiation conversation
	 * @param negotiatedCar
	 * @param offerPrice
	 */
	public NegotiationBotGUI(Agent agent, String opponentAgentName, Car negotiatedCar, double offerPrice, int step){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("NegotiationBot.fxml"));
		try {
			window = loader.load();
			// get the controller of this GUI
			NegotiationBotGUIController controller = loader.<NegotiationBotGUIController>getController();
			// set necessary informations using for the interaction between GUI and Agent
			controller.setAgent(agent);
			controller.setOpponentAgentName(opponentAgentName);
			controller.setNegotiatedCar(negotiatedCar);
			controller.setPrice(offerPrice);
			controller.setStep(step);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(agent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show(); // shows the window
	}
}
