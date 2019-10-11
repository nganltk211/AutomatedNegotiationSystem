package gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.NegotiationLog;

public class OfferConfirmationGUI extends Stage {

	private FXMLLoader loader; // uses to load the fxml-file

	/**
	 * Constructor of the class.
	 */
	public OfferConfirmationGUI(double price, NegotiationLog session){
		Parent window = null;
		// loads GUI-Elements from the BrokerGUI.fxml file
		loader = new FXMLLoader(getClass().getResource("OfferConfirmationGUI.fxml"));
		try {
			window = loader.load();
			OfferConfirmationGUIController controller = loader.<OfferConfirmationGUIController>getController();
			controller.setOffer(price);
			controller.setNegotiation(session);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("Broker Agent Confirmation");
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show(); //shows the window
	}
}