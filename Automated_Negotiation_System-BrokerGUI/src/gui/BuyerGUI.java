package gui;

import java.io.IOException;
import agents.BuyerAgent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * A class for setting and showing a stage BuyerGUI
 */
public class BuyerGUI extends Stage{

	private FXMLLoader loader; // uses to load the fxml-file

	/**
	 * Constructor of the class
	 * @param buyerAgent 
	 */
	public BuyerGUI(BuyerAgent buyerAgent){
		Parent window = null;
		// loads GUI-Elements from the BuyerGUI.fxml file
		loader = new FXMLLoader(getClass().getResource("BuyerGUI.fxml"));
		try {
			window = loader.load();
			// get the controller of this GUI
			BuyerGUIController controller = loader.<BuyerGUIController>getController(); 
			// sets the buyerAgent to the controller (for the interaction between GUI and Agent)
			controller.setAgent(buyerAgent); 
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(buyerAgent.getName());
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();		 
		this.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - 600);
		this.setY(primaryScreenBounds.getMinY());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show(); // shows the window
	}

	
}
