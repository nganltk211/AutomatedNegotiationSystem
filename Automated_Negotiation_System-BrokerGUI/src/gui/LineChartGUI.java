package gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.NegotiationLog;

/**
 * A class for setting and showing a stage LineChartGUI
 */
public class LineChartGUI extends Stage{
	
	private FXMLLoader loader; // uses to load the fxml-file
	
	public LineChartGUI(NegotiationLog session) {
		
		Parent window = null;
		// loads GUI-Elements from the BrokerGUI.fxml file
		loader = new FXMLLoader(getClass().getResource("LineChartGUI.fxml"));
		
		try {
			window = loader.load();
			LineChartGUIController controller = loader.<LineChartGUIController>getController();
			controller.setNegotiation(session);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("Broker Line chart");
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show(); //shows the window
	}
}
