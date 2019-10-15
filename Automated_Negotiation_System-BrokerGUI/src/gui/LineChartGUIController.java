package gui;

import javafx.beans.binding.ObjectExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import model.LogSession;
import model.NegotiationLog;

public class LineChartGUIController {
	
	@FXML
    private LineChart<String, Number> LineChart;

    @FXML
    private CategoryAxis x;

    @FXML
    private NumberAxis y;
    
    private NegotiationLog session;
    
    public void setNegotiation(NegotiationLog session) {
    	this.session = session;
    	drawChart();
    }
    
    public void drawChart() {
    	
    	x.setLabel("T");
    	y.setLabel("Price");
    	
    	XYChart.Series<String, Number> buyer = new XYChart.Series<String, Number>();
    	buyer.setName(session.getBuyerName());
    	
    	XYChart.Series<String, Number> dealer = new XYChart.Series<String, Number>();
    	dealer.setName(session.getDealerName());
    	
    	for(LogSession x : session.getBuyerLog()) {
    		Data<String, Number> n = new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer());
    		n.setNode(createDataNode(n.YValueProperty()));
    		buyer.getData().add(n);
    	}
    	
    	int counter = 0;
    	for(LogSession x : session.getDealerLog()) {
    		counter++;
    		Data<String, Number> n = new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer());
    		
    		if (counter < session.getDealerLog().size()) {
    			n.setNode(createDataNode(n.YValueProperty()));
    			dealer.getData().add(n);
    		} else {
    			dealer.getData().add(n);
    		}
    	}
    	
    	y.setAutoRanging(false);
    	y.setUpperBound(session.getDealerLog().get(0).getOffer() + 100);  	
    	y.setAutoRanging(false);
    	y.setLowerBound(session.getBuyerLog().get(0).getOffer() - 100);
    	
    	LineChart.getData().addAll(buyer, dealer);
    }
    
    private Node createDataNode(ObjectExpression<Number> value) {
        Label label = new Label();
        label.textProperty().bind(value.asString("%.0f"));

        Pane pane = new Pane(label);
        pane.setShape(new Circle(6.0));
        pane.setScaleShape(false);

        label.translateYProperty().bind(label.heightProperty().divide(-1.5));

        return pane;
    }
    
}
