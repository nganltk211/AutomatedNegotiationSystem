package gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.NegotiationLog;

/**
 * A Controller class of NoAgreementGUIController. The logic of GUI-Elements will be defined in
 * this class.
 */
public class NoAgreementGUIController {

	@FXML
    private Button viewLineChart;
	
	private NegotiationLog session;
	
	public void setNegotiation(NegotiationLog session) {
    	this.session = session;
    }
	
	public NegotiationLog getNegotiation() {
    	return this.session;
    }
	
	 @FXML
    void viewLineChart(ActionEvent event) {
		 LineChartGUI gui = new LineChartGUI(this.session);
    }
}
