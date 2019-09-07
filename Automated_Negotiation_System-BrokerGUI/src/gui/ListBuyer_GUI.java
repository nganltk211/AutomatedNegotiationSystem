package gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Car;
import model.CarList;

public class ListBuyer_GUI extends Stage {

	private FXMLLoader loader;

	public ListBuyer_GUI(CarList offerCarlist){
		ScrollPane sp = new ScrollPane();
		VBox root = new VBox();
		root.setSpacing(10);
		root.setPadding(new Insets(10));
		GridPane window = null;
		for (Car car : offerCarlist) {
			loader = new FXMLLoader(getClass().getResource("CarInformation.fxml"));
			try {
				window = loader.load();
		        CarInfoController controller = loader.getController();
		        // Set data in the controller
		        controller.setCar(car);
				window.setStyle("-fx-background-color: #ffffff");
				root.getChildren().add(window);
			} catch (IOException e) {
				System.err.println("Error by loading fxml-File");
			}	
		}		
		this.setTitle("List of possible cars");
		sp.setContent(root);
        sp.setPannable(true); 
		Scene scene = new Scene(sp,625,800);
		this.setScene(scene);
		this.setResizable(false);
		this.show();
	}
}
