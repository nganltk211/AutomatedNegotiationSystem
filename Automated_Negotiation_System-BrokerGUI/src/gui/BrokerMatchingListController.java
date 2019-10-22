package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import agents.BrokerAgentInterface;
import agents.MultiAgentManager;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.MultipleMessage;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

/**
 * A Controller class of BrokerMatchingList. The logic of GUI-Elements will be
 * defined in this class.
 */
public class BrokerMatchingListController implements Initializable {
	@FXML
	private TableView<MultipleMessage> brokerWindow;
	@FXML
	private TableColumn<MultipleMessage, Integer> car_id_coloumn;
	@FXML
	private TableColumn<MultipleMessage, String> d_Name_coloumn;
	@FXML
	private TableColumn<MultipleMessage, String> b_Name_column;
	@FXML
	private TableColumn<MultipleMessage, String> descripttion;
	@FXML
	private Label commision;
	
	private MultiAgentManager carListofBuyerInterest;
	private BrokerAgentInterface o2a; // the interface to interact with the broker agent

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setTableMenu();
	}

	/**
	 * Method to get the Agent Interface for interacting between this GUI and the broker agent
	 * @param agentCtrl
	 */
	public void setController(AgentController agentCtrl) {
		try {
			o2a = agentCtrl.getO2AInterface(BrokerAgentInterface.class);
			carListofBuyerInterest = o2a.getMultiAgentManager();
			setBuyerListData();
			commision.setText(String.valueOf(o2a.getCommision()));
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method for filling the table with data from broker agent
	 */
	private void setBuyerListData() {
		ArrayList<MultipleMessage> buyerList = carListofBuyerInterest.getNegotiationList();
		ObservableList<MultipleMessage> obList = FXCollections.observableArrayList(buyerList);

		brokerWindow.setItems(obList);
		car_id_coloumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getCar().getCarId()).asObject());
		d_Name_coloumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCar().getAgent()));
		b_Name_column.setCellValueFactory(new PropertyValueFactory<MultipleMessage, String>("buyerListString"));
		descripttion.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getCar().getMoreDetails()));
	}

	/**
	 * Method for adding menu item "Send request" to each row of the table and setting event handler on it 
	 */
	private void setTableMenu() {
		brokerWindow.setRowFactory(new Callback<TableView<MultipleMessage>, TableRow<MultipleMessage>>() {
			@Override
			public TableRow<MultipleMessage> call(TableView<MultipleMessage> tableView) {
				final TableRow<MultipleMessage> row = new TableRow<>();
				final ContextMenu contextMenu = new ContextMenu();
				final MenuItem editMenuItem = new MenuItem("Send Request");
				// event when click on this menu item
				editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						MultipleMessage message = row.getItem();
						// tells broker agent to send a list buyer to the dealer
						o2a.sendBuyerListDataToDealer(message); 
						commision.getScene().getWindow().hide(); // hide the GUI
					}
				});
				contextMenu.getItems().add(editMenuItem);
				// Set context menu on row,
				// but use a binding to make it only show for non-empty rows:
				row.contextMenuProperty()
						.bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
				return row;
			}
		});
	}

}
