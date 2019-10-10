package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Negotiation;

/**
 * Main class to start the application
 */
public class Main extends Application {

	public static void main(String[] args) {
		launch(args); // launches the GUI
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage = new BrokerGUI();
		//Negotiation n = new Negotiation();
		//primaryStage = new OfferConfirmationGUI(1000, n);
	}
}
