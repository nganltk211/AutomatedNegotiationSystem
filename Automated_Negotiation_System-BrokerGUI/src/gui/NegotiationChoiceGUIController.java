package gui;

import java.io.IOException;

import agents.DealerAgent;
import jade.core.Agent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import model.Car;

public class NegotiationChoiceGUIController {

	@FXML
	private Button btn_start;
	@FXML
	private RadioButton rb_manual;
	@FXML
	private RadioButton rb_automated;
	@FXML
	private Label manufacture;
	@FXML
	private Label model;
	@FXML
	private Label transmission;
	@FXML
	private Label fuelType;
	@FXML
	private Label bodyType;
	@FXML
	private Label color;
	@FXML
	private Label km;
	@FXML
	private Label price;
	@FXML
	private Label lb_offerprice;
	@FXML
	private TextField offer_price;
	
	private Agent agent;
	private String opponentAgentName; // name of opponent agent
	private Car negotiatedCar;
	private ToggleGroup group;
	public NegotiationChoiceGUIController() {
	}
	
    @FXML
    public void initialize() {
		group = new ToggleGroup();
		rb_manual.setToggleGroup(group);
		rb_automated.setToggleGroup(group);
		rb_manual.setSelected(true);
		radiobuttonChangeValue();
    }
    
	public void radiobuttonChangeValue() {
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
				if (rb_manual.isSelected()) {
					lb_offerprice.setVisible(true);
					offer_price.setVisible(true);
				} else {
					lb_offerprice.setVisible(false);
					offer_price.setVisible(false);
				}
			}
		});
	}
	
	public void startNegotiationButtonClick(ActionEvent event) throws IOException {
		
			if(agent instanceof DealerAgent) {
				DealerAgent dag = (DealerAgent) agent;
				if (rb_manual.isSelected()) {
					dag.setNegotiationChoice(0);
					dag.startNegotiation(opponentAgentName, negotiatedCar, Double.parseDouble(offer_price.getText()));
				} else {
					dag.setNegotiationChoice(1);
					dag.startNegotiation(opponentAgentName, negotiatedCar, negotiatedCar.getMaxprice());
				}		
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
		setCarDetails();
	}
	
    private void setCarDetails() {
    	manufacture.setText(negotiatedCar.getManufacture());
    	model.setText(negotiatedCar.getModel());
    	transmission.setText(negotiatedCar.getTransmission());
    	fuelType.setText(negotiatedCar.getFuelType());
    	bodyType.setText(negotiatedCar.getBodyType());
    	color.setText(negotiatedCar.getColor());
    	km.setText(String.valueOf(negotiatedCar.getKm()));
    	price.setText(String.valueOf(negotiatedCar.getMaxprice()));
    }
}
