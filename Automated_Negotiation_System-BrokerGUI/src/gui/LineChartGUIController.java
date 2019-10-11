package gui;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import model.Log;
import model.Negotiation;

public class LineChartGUIController {
	
	@FXML
    private LineChart<String, Number> LineChart;

    @FXML
    private CategoryAxis x;

    @FXML
    private NumberAxis y;
    
    private Negotiation session;
    
    public void setNegotiation(Negotiation session) {
    	this.session = session;
    	chart();
    }
    
    public void chart() {
    	
    	x.setLabel("T");
    	y.setLabel("Price");
    	
    	XYChart.Series<String, Number> buyer = new XYChart.Series<String, Number>();
    	buyer.setName(session.getBuyerName());
    	
    	XYChart.Series<String, Number> dealer = new XYChart.Series<String, Number>();
    	dealer.setName(session.getDealerName());
    	
    	for(Log x : session.getBuyer()) {
    		buyer.getData().add(new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer()));
    	}
    	
    	for(Log x : session.getDealer()) {
    		dealer.getData().add(new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer()));
    	}
    	
    	y.setAutoRanging(false);
    	y.setUpperBound(session.getDealer().get(0).getOffer() + 100);  	
    	y.setAutoRanging(false);
    	y.setLowerBound(session.getBuyer().get(0).getOffer() - 100);
    	
    	LineChart.getData().addAll(buyer, dealer);
    }
}
