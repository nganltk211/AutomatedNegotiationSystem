package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.Negotiation;

public class OfferConfirmationGUIController {

    @FXML
    private Button lineChart;

    @FXML
    private Label acceptedPrice;
    
    private Negotiation session;
    
    public void setOffer(double price) {
    	acceptedPrice.setText(String.valueOf(price));
    }
    
    public void setNegotiation(Negotiation session) {
    	this.session = session;
    }
    
    public Negotiation getNegotiation() {
    	return this.session;
    }
    
    @FXML
    public void viewLineChart(ActionEvent event) {
    	//System.out.println("Test Chart ");
    	LineChartGUI gui = new LineChartGUI(this.session);
    }
}
