package view;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.BubbleChart;
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
	@FXML
	Canvas welzlCanvas;
	@FXML
	BubbleChart<Number,Number> bubbleChart;
	
	Runnable paintCircle;
	
	public void zScoreView() {
		Feature1LC.visibleProperty().set(false);
		Feature2LC.visibleProperty().set(false);
		bubbleChart.visibleProperty().set(false);
		LineChart.visibleProperty().set(true);
	}
	
	public void welzlView() {
		LineChart.visibleProperty().set(false);
		Feature1LC.visibleProperty().set(true);
		Feature2LC.visibleProperty().set(true);
		bubbleChart.visibleProperty().set(true);
	}
	
	public void linearView() {
		bubbleChart.visibleProperty().set(false);
		LineChart.visibleProperty().set(true);
		Feature1LC.visibleProperty().set(true);
		Feature2LC.visibleProperty().set(true);
	}
	
	void paint() {
		if(paintCircle!=null)
			paintCircle.run();
	}
	
	public void add2LineChart(Series<Number, Number> series) {
		if(series!=null)
			LineChart.getData().add(series);
	}
	

	public void removeFromLineChart() {
		LineChart.getData().clear();
		Feature1LC.getData().clear();
		Feature2LC.getData().clear();
	}

	public void add2Feature1(Series<Number, Number> series) {
		Feature1LC.getData().add(series);
	}
	
	public void add2Feature2(Series<Number, Number> series) {
		Feature2LC.getData().add(series);
	}
	
	public void add2Welzl(Series<Number, Number> series) {
		bubbleChart.getData().add(series);
	}

	@SuppressWarnings("unchecked")
	public void add2LineChart(Series<Number, Number> series, Series<Number, Number> series2, Series<Number, Number> series22) {
		LineChart.getData().addAll(series, series2,series22);
		
	}
	
	public void setLineChartID(String id) {
		LineChart.setId(id);
	}
	
	public void setBubbleChartID(String id) {
		bubbleChart.setId(id);
	}

	public void removeFromBubbleChart() {
		bubbleChart.getData().clear();
	}

	
}
