package gui;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SellerGUI extends Stage {
	private FXMLLoader loader;

	public SellerGUI(){
		Parent window = null;
		loader = new FXMLLoader(getClass().getResource("Seller.fxml"));
		try {
			window = loader.load();
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("Seller Agent");
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show();
	}
}
