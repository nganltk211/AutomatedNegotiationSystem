package gui;

import java.io.IOException;

import agents.DealerAgent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DealerGUI extends Stage{

	private FXMLLoader loader;

	public DealerGUI(DealerAgent dealerAgent){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("Seller.fxml"));
		try {
			window = loader.load();
			SellerController controller = loader.<SellerController>getController();
			controller.setAgent(dealerAgent);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("Dealer Agent");
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}
}
