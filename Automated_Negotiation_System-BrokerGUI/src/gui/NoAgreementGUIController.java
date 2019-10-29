package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import model.NegotiationLog;
import model.NegotiationLogList;

/**
 * A Controller class of NoAgreementGUI. The logic of GUI-Elements will be
 * defined in this class.
 */
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

	/**
	 * This method will be executed when the user click the button "View line chart"
	 * 
	 * @param event
	 */
	@FXML
	void viewLineChart(ActionEvent event) {
		// shows the line chart
		LineChartGUI gui = new LineChartGUI(this.session);
	}
}
