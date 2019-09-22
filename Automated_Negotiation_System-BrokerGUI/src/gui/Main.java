package gui;

import javafx.application.Application;
import javafx.stage.Stage;

	public class Main extends Application{

		public static void main(String[] args) throws InterruptedException {				
			launch(args);
		}

		@Override
		public void start(Stage primaryStage) throws Exception {
			primaryStage = new BrokerGUI();
		}
	}
