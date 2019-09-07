package gui;


import agents.BrokerAgent;
import agents.BuyerAgent;
import agents.DealerAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BrokerController{
	@FXML
	private Button buyer;
	@FXML
	private Button dealer;
	
	private int counterBuyer;
	private int counterDealer;
	private static ContainerController mainCtrl;

	public BrokerController() {
		super();
		counterBuyer = 1;
		counterDealer = 1;
		try {
			startBrokerAgent();
		} catch (InterruptedException e) {
			System.err.println("Problem by starting a BrokerAgent");
		}
	}

	public static void startBrokerAgent() throws InterruptedException {
		// Get a hold to the JADE runtime
		Runtime rt = Runtime.instance();
		// Launch the Main Container (with the administration GUI on top) listening on
		// port 8888
		System.out.println("Launching the platform Main Container...");
		Profile pMain = new ProfileImpl(null, 8888, null);
		pMain.setParameter(Profile.GUI, "true");
		mainCtrl = rt.createMainContainer(pMain);

		// Create and start an agent of class BrokerAgent
		System.out.println("Starting up a BrokerAgent...");
		AgentController agentCtrl;
		try {
			agentCtrl = mainCtrl.createNewAgent("BrokerAgent", BrokerAgent.class.getName(), new Object[0]);
			agentCtrl.start();
		} catch (StaleProxyException e) {
			System.err.println("Problem by creating a BrokerAgent");
		}

	}

	@FXML
	public void buttonDealerClick(ActionEvent event) {

		// Create and start an agent of class DealerAgent
		System.out.println("Starting up a DealerAgent...");
		AgentController agentCtrl;
		try {
			agentCtrl = mainCtrl.createNewAgent("DealerAgent" + counterDealer, DealerAgent.class.getName(),
					new Object[0]);
			agentCtrl.start();
			counterDealer++;
		} catch (StaleProxyException e) {
			System.err.println("Problem by creating a DealerAgent");
		}

	}

	@FXML
	public void buttonBuyerClick(ActionEvent event) {
		// Create and start an agent of class BuyerAgent
		System.out.println("Starting up a BuyerAgent...");
		AgentController agentCtrl;
		try {
			agentCtrl = mainCtrl.createNewAgent("BuyerAgent" + counterBuyer, BuyerAgent.class.getName(), new Object[0]);
			agentCtrl.start();
			counterBuyer++;
		} catch (StaleProxyException e) {
			System.err.println("Problem by creating a BuyerAgent");
		}

	}
	
}
