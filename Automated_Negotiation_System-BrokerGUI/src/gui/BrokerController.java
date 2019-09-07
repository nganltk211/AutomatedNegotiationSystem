package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BrokerController{
	@FXML
	private Button buyer;
	@FXML
	private Button dealer;
	
	
	
	public BrokerController() {
		super();
		    
	    
	}
	
	@FXML
	public void buttonDealerClick(ActionEvent event) {
		Stage stage = new SellerGUI();
        //stage.setScene(new Scene(root));  
        stage.show();
        Stage stage2 = (Stage) dealer.getScene().getWindow();
        // do what you have to do
        stage2.close();
	}
	
	@FXML
	public void buttonBuyerClick(ActionEvent event) {
		Stage stage = new BuyerGui();
        //stage.setScene(new Scene(root));  
        stage.show();
        Stage stage2 = (Stage) buyer.getScene().getWindow();
        // do what you have to do
        stage2.close();
	}
	
}
