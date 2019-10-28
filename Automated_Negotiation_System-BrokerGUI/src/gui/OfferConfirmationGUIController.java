package gui;


import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.NegotiationLog;
import model.NegotiationLogList;

public class OfferConfirmationGUIController {

    @FXML
    private Button lineChart;
    @FXML
    private Label acceptedPrice;
    @FXML
    private Label lb_dealerName;
    @FXML
    private Label lb_buyerName;
    
    private NegotiationLogList session;
    
    public void setOffer(double price) {
    	acceptedPrice.setText(String.valueOf(price));
    }
    
    public void setNegotiation(NegotiationLogList session) {
    	this.session = session;
    }
    
    public NegotiationLogList getNegotiation() {
    	return this.session;
    }
    
    public void setBuyerName(String buyerName) {
    	lb_buyerName.setText(buyerName);
    }
    
   public void setDealerName(String dealerName) {
	   lb_dealerName.setText(dealerName);
   }
    
    @FXML
    public void viewLineChart(ActionEvent event) {
    	LineChartGUI gui = new LineChartGUI(this.session);
    }
    
}
