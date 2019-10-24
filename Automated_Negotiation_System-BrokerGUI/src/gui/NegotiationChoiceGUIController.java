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
import model.CarList;

/**
 * A Controller class of NegotiationChoiceGUI. The logic of GUI-Elements will be
 * defined in this class.
 */
public class NegotiationChoiceGUIController {

	@FXML
	private Button btn_start;
	@FXML
	private RadioButton rb_manual;
	@FXML
	private RadioButton rb_automated;
	@FXML
	private RadioButton rb_conan;
	@FXML
	private Label price;
	@FXML
	private Label priceLable;
	@FXML
	private TextField pricetoEnter;
	@FXML
	private Label beetalable;
	@FXML
	private TextField beetaValue;
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
	@FXML
	private Label lb_duration;
	@FXML
	private TextField nego_duration;


	private boolean isValidate = false;
	private BuyerAgent bag;
	private CarList negotiatedCarList;
	private ToggleGroup group;

	public NegotiationChoiceGUIController() {
	}

	/**
	 * Method to initialize the values for GUI-Elements.
	 */
	@FXML
	public void initialize() {
		// creates a ToogleGroup for radio button to make sure that only one radio
		// button is chosen
		group = new ToggleGroup();
		rb_manual.setToggleGroup(group);
		rb_automated.setToggleGroup(group);
		rb_conan.setToggleGroup(group);
		
		rb_manual.setSelected(true);
		radiobuttonChangeValue();
		nego_duration.setVisible(false);
		lb_duration.setVisible(false);
		stepsLable.setVisible(false);
		negotiationSteps.setVisible(false);
		priceLable.setVisible(true);
		pricetoEnter.setVisible(true);
		priceLable.setText("OfferPrice");
		beetaValue.setVisible(false);
		beetalable.setVisible(false);
		stepsLable.setVisible(false);
		negotiationSteps.setVisible(false);
		validationLabel.setVisible(false);
		maxPriceFromBuyer.setVisible(false);
		maxprice_buyer.setVisible(false);
		pricevalidationLabel.setText("");
		validationLabel.setText("");
	}

	/**
	 * sets event for the (manual/automated) radio buttons on this GUI.
	 */
	private void radiobuttonChangeValue() {
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
				if (rb_manual.isSelected()) {
					// when buyer chooses manual
					priceLable.setVisible(true);
					pricetoEnter.setVisible(true);
					priceLable.setText("OfferPrice");
					beetalable.setVisible(false);
					beetaValue.setVisible(false);
					stepsLable.setVisible(false);
					negotiationSteps.setVisible(false);
					validationLabel.setVisible(false);
					maxPriceFromBuyer.setVisible(false);
					maxprice_buyer.setVisible(false);
					nego_duration.setVisible(false);
					lb_duration.setVisible(false);
				} else if (rb_automated.isSelected()) {
					// when buyer choose time dependent tactics
					priceLable.setVisible(true);
					pricetoEnter.setVisible(true);
					priceLable.setText("Min Price");
					beetalable.setVisible(true);
					beetaValue.setVisible(true);
					stepsLable.setVisible(true);
					negotiationSteps.setVisible(true);
					validationLabel.setVisible(true);
					maxPriceFromBuyer.setVisible(true);
					maxprice_buyer.setVisible(true);
					nego_duration.setVisible(false);
					lb_duration.setVisible(false);
					maxPriceFromBuyer.setText(String.valueOf(bag.getReservationPrice()));
				} else {
					// when buyer choose concurrent negotiation
					priceLable.setVisible(true);
					pricetoEnter.setVisible(true);
					priceLable.setText("Min Price");
					beetalable.setVisible(false);
					beetaValue.setVisible(false);
					stepsLable.setVisible(false);
					negotiationSteps.setVisible(false);
					validationLabel.setVisible(false);
					maxPriceFromBuyer.setVisible(true);
					maxprice_buyer.setVisible(true);
					nego_duration.setVisible(true);
					lb_duration.setVisible(true);
					maxPriceFromBuyer.setText(String.valueOf(bag.getReservationPrice()));
				}
			}
		});
	}

	/**
	 * Method to check the validation of elements on GUI
	 * 
	 * @return true if validate
	 */
	private boolean validation() {
		boolean beeta = FormValidation.textFieldNotEmpty(beetaValue, validationLabel, "Please Enter Beeta Value");
		boolean steps = FormValidation.textFieldNotEmpty(negotiationSteps, validationLabel, "Please Enter steps Value");
		boolean price = FormValidation.textFieldNotEmpty(pricetoEnter, pricevalidationLabel, "Please Enter Price");
		if (price) {
			isValidate = true;
		}
		try {
			if (Double.parseDouble(pricetoEnter.getText()) < 1000) {
				pricevalidationLabel.setText("Price can't be less than 1000");
				isValidate = false;
			}
		} catch (NumberFormatException e) {
			pricevalidationLabel.setText("Please Enter Number Value");
		}

		if (rb_automated.isSelected()) {
			if (!beeta && !steps) {
				steps = FormValidation.textFieldNotEmpty(negotiationSteps, validationLabel,
						"Please Enter Beeta and steps Value");

				isValidate = false;
			} else {
				try {
					if (Double.parseDouble(beetaValue.getText()) < 1) {
						validationLabel.setText("Format Beeta > 1");
						isValidate = false;
					}
				} catch (NumberFormatException e) {
					validationLabel.setText("Format Beeta > 1");
				}
				isValidate = true;
			}

		}

		return isValidate;
	}

	/**
	 * Event Listener on "Start" button to start a negotiation with a dealer
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void startNegotiationButtonClick(ActionEvent event) throws IOException {
		validation();
		if (isValidate) {
			try {
				bag.setIntialPrice(Double.parseDouble(pricetoEnter.getText()));
				if (rb_manual.isSelected()) {
					bag.setNegotiationTatic(0);
					// sends a negotiation request to the broker agent
					bag.sendBackTheChoosenCarsToTheBroker(negotiatedCarList, Double.parseDouble(pricetoEnter.getText()));
					((Node) (event.getSource())).getScene().getWindow().hide();
				} else if (rb_automated.isSelected()){
					bag.setNegotiationTatic(1);
					try {
						bag.setBeetavalue(Double.parseDouble(beetaValue.getText()));
						bag.setMaxStep(Integer.parseInt(negotiationSteps.getText()));
						bag.setReservationPrice(Double.parseDouble(maxPriceFromBuyer.getText()));
						bag.setNegotiationDuration(Long.parseLong(nego_duration.getText()));
						// sends a negotiation request to the broker agent
						bag.sendBackTheChoosenCarsToTheBroker(negotiatedCarList,
								Double.parseDouble(pricetoEnter.getText()));
						((Node) (event.getSource())).getScene().getWindow().hide();
					} catch (NumberFormatException e) {
						validationLabel.setText("Please enter a number");
					}
				} else {
					bag.setNegotiationTatic(2);
					bag.setReservationPrice(Double.parseDouble(maxPriceFromBuyer.getText()));
					bag.setNegotiationDuration(Long.parseLong(nego_duration.getText()));
					bag.sendBackTheChoosenCarsToTheBroker(negotiatedCarList,
							Double.parseDouble(pricetoEnter.getText()));
					((Node) (event.getSource())).getScene().getWindow().hide();
				}
			} catch (NumberFormatException e) {
				pricevalidationLabel.setText("Please enter a number");
			}
		}

	}

	public void setAgent(BuyerAgent agent) {
		bag = agent;
	}

	public void setNegotiatedCarList(CarList carList) {
		this.negotiatedCarList = carList;
	}
}
