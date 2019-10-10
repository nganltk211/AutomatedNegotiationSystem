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
    private LineChart<?, ?> LineChart;

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
    	
    	XYChart.Series buyer = new XYChart.Series();
    	buyer.setName(session.getBuyerName());
    	
    	XYChart.Series dealer = new XYChart.Series();
    	dealer.setName(session.getDealerName());
    	
    	for(Log x : session.getBuyer()) {
    		buyer.getData().add(new XYChart.Data(x.getStep(), x.getOffer()));
    	}
    	
    	for(Log x : session.getDealer()) {
    		dealer.getData().add(new XYChart.Data(x.getStep(), x.getOffer()));
    	}
    			
    	/*XYChart.Series series = new XYChart.Series();
    	
    	series.setName("Dealer");
    	
    	series.getData().add(new XYChart.Data("1", 23));
    	series.getData().add(new XYChart.Data("2", 33));
    	series.getData().add(new XYChart.Data("3", 33));
    	series.getData().add(new XYChart.Data("4", 53));
    	series.getData().add(new XYChart.Data("5", 63));*/
    	
    	LineChart.getData().addAll(buyer, dealer);
    }
}
