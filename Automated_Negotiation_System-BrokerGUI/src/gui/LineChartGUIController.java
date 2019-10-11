package gui;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    	chart();
    }
    
    public void chart() {
    	
    	x.setLabel("T");
    	y.setLabel("Price");
    	
    	XYChart.Series<String, Number> buyer = new XYChart.Series<String, Number>();
    	buyer.setName(session.getBuyerName());
    	
    	XYChart.Series<String, Number> dealer = new XYChart.Series<String, Number>();
    	dealer.setName(session.getDealerName());
    	
    	for(LogSession x : session.getBuyerLog()) {
    		buyer.getData().add(new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer()));
    	}
    	
    	for(LogSession x : session.getDealerLog()) {
    		dealer.getData().add(new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer()));
    	}
    	
    	y.setAutoRanging(false);
    	y.setUpperBound(session.getDealerLog().get(0).getOffer() + 100);  	
    	y.setAutoRanging(false);
    	y.setLowerBound(session.getBuyerLog().get(0).getOffer() - 100);
    	
    	LineChart.getData().addAll(buyer, dealer);
    }
}
