package gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class BuyerGui extends Stage{

	private FXMLLoader loader;

	public BuyerGui(){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("BuyerGUI.fxml"));
		try {
			window = loader.load();
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("Buyer Agent");
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}

	
}
