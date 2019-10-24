package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.NegotiationLog;
import model.NegotiationLogList;


public class NoAgreementGUIController {

	@FXML
    private Button viewLineChart;
	
	private NegotiationLogList session;
	
	public void setNegotiationList(NegotiationLogList session) {
    	this.session = session;
    }
	
	public NegotiationLogList getNegotiation() {
    	return this.session;
    }
	
	 @FXML
    void viewLineChart(ActionEvent event) {
		 LineChartGUI gui = new LineChartGUI(this.session);
    }
}
