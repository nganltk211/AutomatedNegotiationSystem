package gui;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Car;


	public class Main extends Application{

		public static void main(String[] args) throws InterruptedException {				
			launch(args);
		}

		@Override
		public void start(Stage primaryStage) throws Exception {
		   /* Car[] listCars = new Car[3];
			listCars[0] = new Car(1);
			listCars[0].setManufacture("Test1");
			listCars[1] = new Car(2);
			listCars[1].setManufacture("Test2");
			listCars[2] = new Car(3);
			primaryStage = new ListBuyer_GUI(listCars);*/
			primaryStage = new BuyerGui();
		}
	}
