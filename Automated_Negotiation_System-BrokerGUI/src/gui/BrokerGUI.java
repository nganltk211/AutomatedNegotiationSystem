package gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A class for setting and showing a stage BrokerGUI
 */
public class BrokerGUI extends Stage {

	private FXMLLoader loader; // uses to load the fxml-file

	/**
	 * Constructor of the class.
	 */
	public BrokerGUI(){
		Parent window = null;
		// loads GUI-Elements from the BrokerGUI.fxml file
		loader = new FXMLLoader(getClass().getResource("BrokerGUI.fxml"));
		try {
			window = loader.load();
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("Broker Agent");
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show(); //shows the window
	}
}
