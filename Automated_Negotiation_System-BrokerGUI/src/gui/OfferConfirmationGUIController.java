package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.NegotiationLog;

/**
 * A Controller class of OfferConfirmationGUI. The logic of GUI-Elements will be defined in
 * this class.
 */
public class OfferConfirmationGUIController {

    @FXML
    private Button lineChart;
    @FXML
    private Label acceptedPrice;   
    @FXML
    private Label lb_buyername;
    @FXML
    private Label lb_dealername;
    
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
    
    public void setBuyerName(String buyerName) {
    	lb_buyername.setText(buyerName);
    }
    
    public void setDealerName(String dealerName) {
    	lb_dealername.setText(dealerName);
    }
    
    @FXML
    public void viewLineChart(ActionEvent event) {
    	LineChartGUI gui = new LineChartGUI(this.session);
    }
}
