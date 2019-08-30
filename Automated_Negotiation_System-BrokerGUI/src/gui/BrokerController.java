package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class BrokerController {
	@FXML
	private Button buyer;
	@FXML
	private Button dealer;

	public BrokerController() {
		super();
	}
	
	@FXML
	public void buttonDealerClick(ActionEvent event) {
		System.out.println("Dealer aktiv");
	}
	
	@FXML
	public void buttonBuyerClick(ActionEvent event) {
		System.out.println("Buyer aktiv");
	}
}
