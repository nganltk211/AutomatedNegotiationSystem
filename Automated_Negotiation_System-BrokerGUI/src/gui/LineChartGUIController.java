package gui;

import javafx.beans.binding.ObjectExpression;
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
import model.NegotiationLogList;

public class LineChartGUIController {
	
	@FXML
    private LineChart<String, Number> LineChart;

    @FXML
    private CategoryAxis x;

    @FXML
    private NumberAxis y;
    
    private NegotiationLogList session;
    
    public void setNegotiation(NegotiationLogList session) {
    	this.session = session;
    	drawChart();
    }
    
    public void drawChart() {
    	
    	x.setLabel("T");
    	y.setLabel("Price");
    	
    	double max = 0.0;
    	double min = 100000;
    	for (NegotiationLog log : session) {
    		XYChart.Series<String, Number> agent = new XYChart.Series<String, Number>();
    		agent.setName(log.getAgentName());
    		boolean aboveTheLine = true;
    		if (log.getAgentName().startsWith("D")) {
    			aboveTheLine = true;
    		} else {
    			aboveTheLine = false;
    		}
    		for(LogSession x : log.getAgentLog()) {
        		Data<String, Number> n = new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer());
        		n.setNode(createDataNode(n.YValueProperty(), aboveTheLine));
        		
        		agent.getData().add(n);
        		if (x.getOffer() > max) {
        			max = x.getOffer();
        		}
        		if (x.getOffer() < min) {
        			min = x.getOffer();
        		}
        	}
    		LineChart.getData().add(agent);
    	}
    	
    	y.setAutoRanging(false);
    	y.setUpperBound(max + 250);  	
    	y.setAutoRanging(false);
    	y.setLowerBound(min - 250);	
    }
    
    private Node createDataNode(ObjectExpression<Number> value, boolean aboveTheLine) {
        Label label = new Label();
        label.textProperty().bind(value.asString("%.0f"));
        Pane pane = new Pane(label);
        pane.setShape(new Circle(5.0));
        pane.setScaleShape(false);
        if (aboveTheLine) {
        	label.translateYProperty().bind(label.heightProperty().divide(-1.5));
        } else {
        	label.translateYProperty().bind(label.heightProperty().divide(1.1));
        }      
        return pane;
    }
    
}
