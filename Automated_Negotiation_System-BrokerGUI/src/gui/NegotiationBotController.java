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
import javafx.scene.control.ToggleGroup;
import model.Car;

public class NegotiationBotController {

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
	private TextField encounteroffer_price;

	private Agent agent;
	private String opponentAgentName; // name of opponent agent
	private Car negotiatedCar;

	public NegotiationBotController() {
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
		encounteroffer_price.setVisible(true);
		btn_send.setVisible(true);
	}

	public void buttonSendClick(ActionEvent event) {
		if (agent instanceof DealerAgent) {
			DealerAgent dag = (DealerAgent) agent;
			dag.makeAnEncounterOffer(opponentAgentName, negotiatedCar, Double.valueOf(encounteroffer_price.getText()));
			((Node) (event.getSource())).getScene().getWindow().hide();			
		} else {
			BuyerAgent bag = (BuyerAgent) agent;
			bag.makeAnEncounterOffer(opponentAgentName, negotiatedCar, Double.valueOf(encounteroffer_price.getText()));
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