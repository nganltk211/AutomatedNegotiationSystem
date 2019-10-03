package gui;

import java.io.IOException;

import agents.DealerAgent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A class for setting and showing a stage DealerGUI
 */
public class DealerGUI extends Stage{

	private FXMLLoader loader; // uses to load the fxml-file

	/**
	 * Constructor of the class.
	 */
	public DealerGUI(DealerAgent dealerAgent){
		Parent window = null;
		// loads GUI-Elements from the DealerGUI.fxml file
		loader = new FXMLLoader(getClass().getResource("DealerGUI.fxml"));
		try {
			window = loader.load();
			DealerGUIController controller = loader.<DealerGUIController>getController();
			controller.setAgent(dealerAgent);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(dealerAgent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}
}
