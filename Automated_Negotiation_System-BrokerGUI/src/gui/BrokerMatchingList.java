package gui;

import java.io.IOException;

import jade.wrapper.AgentController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A class for setting and showing a stage BrokerMatchingList
 */
public class BrokerMatchingList extends Stage {

	private FXMLLoader loader; // uses to load the fxml-file

	/**
	 * Constructor of the class.
	 */
	public BrokerMatchingList(AgentController agentCtrl) {
		Parent window = null;
		// loads GUI-Elements from the BrokerGUI.fxml file
		loader = new FXMLLoader(getClass().getResource("BrokerMatchingList.fxml"));
		try {
			window = loader.load();
			BrokerMatchingListController controller = loader.<BrokerMatchingListController>getController();
			controller.setController(agentCtrl);
		} catch (IOException e) {
			System.err.println("Error by loading fxml-File");
		}
		this.setTitle("Broker Window");
		Scene scene = new Scene(window);
		this.setScene(scene);
		this.show(); // shows the window
	}
}
