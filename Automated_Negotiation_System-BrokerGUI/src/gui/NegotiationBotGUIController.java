package gui;

import agents.BuyerAgent;
import agents.DealerAgent;
import jade.core.Agent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Car;

public class NegotiationBotGUIController {

	@FXML
	private Label offer_price;
	@FXML
	private Label lb_newoffer;
	@FXML
	private Label lb_price;

	@FXML
	private Button btn_accept;
	@FXML
	private Button btn_reject;
	@FXML
	private Button btn_send;
	@FXML
	private TextField counteroffer_price;

	private Agent agent;
	private String opponentAgentName; // name of opponent agent
	private Car negotiatedCar;

	public NegotiationBotGUIController() {
	}

	@FXML
	public void initialize() {

	}

	public void buttonAcceptClick(ActionEvent event) {
		if (agent instanceof DealerAgent) {
			DealerAgent dag = (DealerAgent) agent;
			dag.acceptOffer(opponentAgentName, negotiatedCar, Double.valueOf(offer_price.getText()));
			((Node) (event.getSource())).getScene().getWindow().hide();			
		} else {
			BuyerAgent bag = (BuyerAgent) agent;
			bag.acceptOffer(opponentAgentName, negotiatedCar, Double.valueOf(offer_price.getText()));
			((Node) (event.getSource())).getScene().getWindow().hide();
		}
	}

	public void buttonRejectClick(ActionEvent event) {
		lb_price.setVisible(true);
		lb_newoffer.setVisible(true);
		counteroffer_price.setVisible(true);
		btn_send.setVisible(true);
	}

	public void buttonSendClick(ActionEvent event) {
		if (agent instanceof DealerAgent) {
			DealerAgent dag = (DealerAgent) agent;
			dag.makeACounterOffer(opponentAgentName, negotiatedCar, Double.valueOf(counteroffer_price.getText()));
			((Node) (event.getSource())).getScene().getWindow().hide();			
		} else {
			BuyerAgent bag = (BuyerAgent) agent;
			bag.makeACounterOffer(opponentAgentName, negotiatedCar, Double.valueOf(counteroffer_price.getText()));
			((Node) (event.getSource())).getScene().getWindow().hide();
		}
	}

	public void setOpponentAgentName(String name) {
		this.opponentAgentName = name;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public void setNegotiatedCar(Car car) {
		this.negotiatedCar = car;
	}

	public void setPrice(double price) {
		offer_price.setText(String.valueOf(price));
	}
}