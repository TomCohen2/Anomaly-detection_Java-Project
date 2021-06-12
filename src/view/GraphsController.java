package view;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;

public class GraphsController {
	@FXML
	LineChart<Number, Number> LineChart;
	@FXML
	LineChart<Number, Number> Feature1LC;
	@FXML
	LineChart<Number, Number> Feature2LC;
	@FXML
	NumberAxis xAxis;
	@FXML
	NumberAxis yAxis;
	
	@SuppressWarnings("unchecked")
	public void add2LineChart(@SuppressWarnings("rawtypes") Series series) {
		if(series!=null)
			LineChart.getData().add(series);
	}
	

	public void removeFromLineChart() {
		LineChart.getData().clear();
		Feature1LC.getData().clear();
		Feature2LC.getData().clear();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void add2Feature1(Series series) {
		Feature1LC.getData().add(series);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void add2Feature2(Series series) {
		Feature2LC.getData().add(series);
	}
}
