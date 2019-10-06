package gui;

import java.io.IOException;

import jade.core.Agent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A class for setting and showing a stage NoOffersGUI.
 * This interface showed to buyer when there are no suitable cars in broker's catalog  
 */
public class NoOffersGUI extends Stage {

	private FXMLLoader loader; // uses to load the fxml-file

	/**
	 * Constructor of the class
	 * @param agent : buyer agent
	 */
	public NoOffersGUI(Agent agent){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("NoOffersGUI.fxml"));
		try {
			window = loader.load();
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("To : " + agent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.setResizable(false);
		this.show(); // shows the window
	}
}
