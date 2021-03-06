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

/**
 * A Controller class of LineChartGUI. The logic of GUI-Elements will be defined
 * in this class.
 */
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

	/**
	 * Method for drawing the line chart.
	 */
	public void drawChart() {
		x.setLabel("T");
		y.setLabel("Price");

		XYChart.Series<String, Number> buyer = new XYChart.Series<String, Number>();
		buyer.setName(session.getBuyerName());

		XYChart.Series<String, Number> dealer = new XYChart.Series<String, Number>();
		dealer.setName(session.getDealerName());

		for (LogSession x : session.getBuyerLog()) {
			Data<String, Number> n = new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer());
			n.setNode(createDataNode(n.YValueProperty(), false));
			buyer.getData().add(n);
		}

		for (LogSession x : session.getDealerLog()) {
			Data<String, Number> n = new XYChart.Data<String, Number>(String.valueOf(x.getStep()), x.getOffer());
			n.setNode(createDataNode(n.YValueProperty(), true));
			dealer.getData().add(n);
		}

		if (session.getDealerLog().size() > 0 && session.getBuyerLog().size() > 0) {
			y.setAutoRanging(false);
			y.setUpperBound(session.getDealerLog().get(0).getOffer() + 250);
			y.setAutoRanging(false);
			y.setLowerBound(session.getBuyerLog().get(0).getOffer() - 250);
		}
		LineChart.getData().addAll(buyer, dealer);
	}

	/**
	 * Method to create labels for a node of the line chart.
	 * 
	 * @param value: value of the node
	 * @param aboveTheLine: true if the label should be above the line
	 * @return
	 */
	private Node createDataNode(ObjectExpression<Number> value, boolean aboveTheLine) {
		Label label = new Label();
		label.textProperty().bind(value.asString("%.0f"));

		Pane pane = new Pane(label);
		pane.setShape(new Circle(6.0));
		pane.setScaleShape(false);
		if (aboveTheLine) {
			label.translateYProperty().bind(label.heightProperty().divide(-1.5));

		} else {
			label.translateYProperty().bind(label.heightProperty().divide(1.1));
		}
		return pane;
	}

}
