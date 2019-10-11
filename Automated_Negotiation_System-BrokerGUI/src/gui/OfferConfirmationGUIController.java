package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.NegotiationLog;

public class OfferConfirmationGUIController {

    @FXML
    private Button lineChart;

    @FXML
    private Label acceptedPrice;
    
    private NegotiationLog session;
    
    public void setOffer(double price) {
    	acceptedPrice.setText(String.valueOf(price));
    }
    
    public void setNegotiation(NegotiationLog session) {
    	this.session = session;
    }
    
    public NegotiationLog getNegotiation() {
    	return this.session;
    }
    
    @FXML
    public void viewLineChart(ActionEvent event) {
    	//System.out.println("Test Chart ");
    	LineChartGUI gui = new LineChartGUI(this.session);
    }
}
