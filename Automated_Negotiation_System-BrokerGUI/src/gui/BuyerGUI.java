package gui;

import java.io.IOException;
import agents.BuyerAgent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class BuyerGUI extends Stage{

	private FXMLLoader loader;

	public BuyerGUI(BuyerAgent buyerAgent){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("BuyerGUI.fxml"));
		//String css = BuyerGui.class.getResource("GUI.css").toExternalForm();
		try {
			window = loader.load();
			BuyerGUIController controller = loader.<BuyerGUIController>getController();
			controller.setAgent(buyerAgent);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle(buyerAgent.getName());
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}

	
}
