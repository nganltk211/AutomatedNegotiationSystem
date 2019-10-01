package gui;

import java.io.IOException;

import agents.BuyerAgent;
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
import model.FormValidation;

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
	private Label priceLable;
	@FXML
	private TextField PricetoEnter;
	@FXML
	private Label Beetalable;
	@FXML
	private TextField BeetaValue;
	@FXML
	private Label stepsLable;
	@FXML
	private TextField negotiationSteps;
	@FXML
	private Label validationLabel;
	@FXML
	private Label pricevalidationLabel;
	@FXML
	private Label maxprice_buyer;
	@FXML
	private TextField maxPriceFromBuyer;
	
	private boolean isValidate = false;

	
	private BuyerAgent bag;
	//private String opponentAgentName; // name of opponent agent
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
		stepsLable.setVisible(false);
		negotiationSteps.setVisible(false);
        priceLable.setVisible(true);
		PricetoEnter.setVisible(true);
		priceLable.setText("OfferPrice");
		BeetaValue.setVisible(false);
		Beetalable.setVisible(false);
		stepsLable.setVisible(false);
		negotiationSteps.setVisible(false);
		validationLabel.setVisible(false);
		maxPriceFromBuyer.setVisible(false);
		maxprice_buyer.setVisible(false);
    }
    
	public void radiobuttonChangeValue() {
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
				if (rb_manual.isSelected()) {
					priceLable.setVisible(true);
				    PricetoEnter.setVisible(true);
					priceLable.setText("OfferPrice");
					Beetalable.setVisible(false);
					BeetaValue.setVisible(false);
					stepsLable.setVisible(false);
					negotiationSteps.setVisible(false);
					validationLabel.setVisible(false);
					maxPriceFromBuyer.setVisible(false);
					maxprice_buyer.setVisible(false);
				} else {
				    priceLable.setVisible(true);
				    PricetoEnter.setVisible(true);
					priceLable.setText("Min Price");
					Beetalable.setVisible(true);
					BeetaValue.setVisible(true);
					stepsLable.setVisible(true);

					negotiationSteps.setVisible(true);
					validationLabel.setVisible(true);
					maxPriceFromBuyer.setVisible(true);
					maxprice_buyer.setVisible(true);
					maxPriceFromBuyer.setText(String.valueOf(bag.getReservationPrice()));
				}
			}
		});
	}
	public boolean validation()
	{
		boolean beeta = FormValidation.textFieldNotEmpty(BeetaValue, validationLabel, "Please Enter Beeta Value");
		boolean steps = FormValidation.textFieldNotEmpty(negotiationSteps, validationLabel, "Please Enter steps Value");
		boolean price = FormValidation.textFieldNotEmpty(PricetoEnter, pricevalidationLabel, "Please Enter Price");
		if(price)
		{
			isValidate = true;
		}
		
		if(Double.parseDouble(PricetoEnter.getText()) < 1000)
		{
			pricevalidationLabel.setText("Price can't be less than 1000");
			isValidate = false;
		}
		if(rb_automated.isSelected())
		{
			if(!beeta && !steps)
			{
				steps = FormValidation.textFieldNotEmpty(negotiationSteps, validationLabel, "Please Enter Beeta and steps Value");
				
				isValidate = false;
			}else {
				if(Double.parseDouble(BeetaValue.getText()) < 1)
				{
					validationLabel.setText("Format Beeta > 1");
					isValidate = false;
				}
				isValidate = true;
			}
		}
		
		return isValidate;
	}
	public void startNegotiationButtonClick(ActionEvent event) throws IOException {
				validation();
				if(isValidate)
				{
					try {
						bag.setIntialPrice(Double.parseDouble(PricetoEnter.getText()));
						
					}catch(NumberFormatException e) {
						pricevalidationLabel.setText("Please enter a number");
					}
					
					if (rb_manual.isSelected()) {		
						bag.setNegotiationManual(true);
					} else {
						bag.setNegotiationManual(false);
						try {
							bag.setBeetavalue(Double.parseDouble(BeetaValue.getText()));
							
						}catch(NumberFormatException e) {
							validationLabel.setText("Please enter a number");
						}
						try {
							bag.setMaxStep(Integer.parseInt(negotiationSteps.getText()));
							
						}catch(NumberFormatException e) {
							validationLabel.setText("Please enter a number");
						}
						
						
					}
					bag.sendBackTheChoosenCarsToTheBroker(negotiatedCar, Double.parseDouble(PricetoEnter.getText()));		
					((Node) (event.getSource())).getScene().getWindow().hide();
				}
				
	}	
	
//	public void setOpponentAgentName(String name) {
//		this.opponentAgentName = name;
//	}
	
	public void setAgent(BuyerAgent agent) {
		bag = agent;
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
