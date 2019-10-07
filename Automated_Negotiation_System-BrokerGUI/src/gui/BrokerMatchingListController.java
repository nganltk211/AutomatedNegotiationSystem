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
import model.Car;
import model.MultipleMessage;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;



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
	private Label commision;
	@FXML
	private TableColumn<MultipleMessage, String> descripttion;
	private AgentController agentCtrl;
	private MultiAgentManager carListofBuyerInterest;
	private BrokerAgentInterface o2a;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		setTableMenu();
	}

	public void setController(AgentController agentCtrl2) {
		// TODO Auto-generated method stub
		this.agentCtrl = agentCtrl2;
		
		try {
			o2a = agentCtrl.getO2AInterface(BrokerAgentInterface.class);
			//o2a.sendBuyerListDataToDealer();
			carListofBuyerInterest = o2a.getMultiAgentManager();
			setBuyerListData();
			commision.setText(String.valueOf(o2a.getCommision()));
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setBuyerListData()
	{
		ArrayList<MultipleMessage> buyerList  = carListofBuyerInterest.getNegotiationList();
		ObservableList<MultipleMessage> obList = FXCollections.observableArrayList(buyerList);
		
		brokerWindow.setItems(obList);
		car_id_coloumn.setCellValueFactory(cellData->new SimpleIntegerProperty(cellData.getValue().getCar().getCarId()).asObject());
		d_Name_coloumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getCar().getAgent()));
		b_Name_column.setCellValueFactory(new PropertyValueFactory<MultipleMessage, String>("buyerListString"));
		descripttion.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getCar().getMoreDetails()));
		
	}
	
	public void setTableMenu() {
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
						o2a.sendBuyerListDataToDealer(message);
						commision.getScene().getWindow().hide();
						
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
