package gui;

import java.io.IOException;

import agents.BuyerAgent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.CarList;

/**
 * A class for setting and showing a stage NegotiationChoiceGUI.
 * This is an interface for a buyer to choose the negotiation way (manual or automated)
 */
public class NegotiationChoiceGUI extends Stage {

	private FXMLLoader loader;

	/**
	 * Constructor of the class
	 * @param agent : buyer agent
	 * @param negotiatedCar
	 */
	public NegotiationChoiceGUI(BuyerAgent agent, CarList negotiatedCar, boolean firstNegotiationThread){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("ChoiceOfNegotiationWay.fxml"));
		try {
			window = loader.load();
			// get the controller of this GUI
			NegotiationChoiceGUIController controller = loader.<NegotiationChoiceGUIController>getController();
			// set necessary informations using for the interaction between GUI and Agent
			controller.setAgent(agent);
			controller.setNegotiatedCarList(negotiatedCar);
			controller.setFirstNegotiationThread(firstNegotiationThread);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(agent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}
}
