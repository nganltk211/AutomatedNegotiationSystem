package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.lang.ExceptionInInitializerError;

public class TestingBrokerCommunicationWithBuyer extends Agent{
	StackPane p= new StackPane();
	//Scene scene = new Scene(p,400,400);
	TestClass t = new TestClass();
	@Override
	protected void setup()
	{
		
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action()
			{
				ACLMessage msg = receive();
				if(msg != null)
				{
					Text text2 = new Text("This is contained message from Buyer"+ msg.getContent());
					p.getChildren().add(text2);
					
				}else {
					block();
				}
			}
		});
	}
	
	public void start(Stage window)
	{
		
		window.setTitle("Test");
		Text text = new Text("Hellow World");
		p.getChildren().add(text);
		Scene scene = new Scene(p, 400,400);
		window.setScene(scene);
		window.show();
	}
		
			
}
