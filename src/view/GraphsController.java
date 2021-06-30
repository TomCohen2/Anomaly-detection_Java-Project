package view;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;

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
	@FXML
	CheckBox Simulator;
	
	XYChart.Series<Number, Number> goodPoints = new XYChart.Series<Number,Number>();
	XYChart.Series<Number, Number> badPoints = new XYChart.Series<Number,Number>();
	XYChart.Series<Number, Number> line = new XYChart.Series<Number,Number>();
	XYChart.Series<Number, Number> Feature1 = new XYChart.Series<Number,Number>();
	XYChart.Series<Number, Number> Feature2 = new XYChart.Series<Number,Number>();
	XYChart.Series welzlCircle = new XYChart.Series<>();
	XYChart.Series goodWelzl = new XYChart.Series<>();
	XYChart.Series badWelzl = new XYChart.Series<>();
	
	Runnable paintCircle;
	
	public void zScoreView() {
		LineChart.setId("Z");
		if(!bubbleChart.getData().isEmpty())
			bubbleChart.getData().removeAll(welzlCircle, goodWelzl, badWelzl);
		if(!LineChart.getData().isEmpty()) {
			LineChart.getData().removeAll(line,goodPoints,badPoints);
		}
		LineChart.getData().addAll(line,goodPoints,badPoints);
	}
	
	public void welzlView() {
		bubbleChart.setId("W");
		Feature1LC.setId("LR");
		Feature2LC.setId("LR");
		if(!LineChart.getData().isEmpty()) {
			LineChart.getData().removeAll(line,goodPoints,badPoints);
		}
		if(!bubbleChart.getData().isEmpty())
			bubbleChart.getData().removeAll(welzlCircle,goodWelzl,badWelzl);
		if(!Feature1LC.getData().isEmpty())
			Feature1LC.getData().removeAll(Feature1);
		if(!Feature2LC.getData().isEmpty())
			Feature2LC.getData().removeAll(Feature2);
		bubbleChart.getData().addAll(welzlCircle,goodWelzl,badWelzl);
		LineChart.getData().addAll(line,goodPoints,badPoints);
		Feature1LC.getData().addAll(Feature1);
		Feature2LC.getData().addAll(Feature2);
	}
	
	public void linearView() {
		LineChart.setId("LR");
		Feature1LC.setId("LR");
		Feature2LC.setId("LR");
		if(!LineChart.getData().isEmpty())
			LineChart.getData().removeAll(line,goodPoints,badPoints);
		if(!Feature1LC.getData().isEmpty())
			Feature1LC.getData().removeAll(Feature1);
		if(!Feature2LC.getData().isEmpty())
			Feature2LC.getData().removeAll(Feature2);
		if(!bubbleChart.getData().isEmpty())
			bubbleChart.getData().removeAll(welzlCircle, goodWelzl, badWelzl);
		LineChart.getData().addAll(line,goodPoints,badPoints);
		Feature1LC.getData().addAll(Feature1);
		Feature2LC.getData().addAll(Feature2);
		LineChart.setCreateSymbols(true);
//		Feature1LC.setCreateSymbols(false);
//		graphs.controller.Feature2LC.setCreateSymbols(false);
	}
	
	void paint() {
		if(paintCircle!=null)
			paintCircle.run();
	}
	
	public void add2LineChart(Series<Number, Number> series) {
		if(series!=null)
			LineChart.getData().add(series);
	}
	
	public void clearSeries() {
		goodPoints.getData().clear();
		badPoints.getData().clear();
		line.getData().clear();
	}
	
	public void removeFromLineChart() {
		if (LineChart.getData() != null)
			LineChart.getData().clear();
		if (Feature1LC.getData() != null)
			Feature1LC.getData().clear();
		if (Feature2LC.getData() != null)
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
	
	void bindFlightGearBoolean(BooleanProperty FG) {
		FG.bindBidirectional(Simulator.selectedProperty());
	}
	
	void bindGoodPoints(XYChart.Series<Number, Number> data) {
		goodPoints.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindBadPoints(XYChart.Series<Number, Number> data) {
		badPoints.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindLine(XYChart.Series<Number, Number> data) {
		line.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindWelzlCircle(XYChart.Series data) {
		welzlCircle.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindBadWelzl(XYChart.Series data) {
		badWelzl.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindGoodWelzl(XYChart.Series data) {
		goodWelzl.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindFeature1(XYChart.Series<Number, Number> data) {
		Feature1.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindFeature2(XYChart.Series<Number, Number> data) {
		Feature2.dataProperty().bindBidirectional(data.dataProperty());
	}
	
	void bindLineChartVis(BooleanProperty bol) {
		LineChart.visibleProperty().bind(bol);
	}
	
	void bindBubbleChartVis(BooleanProperty bol) {
		bubbleChart.visibleProperty().bind(bol);
	}
	void bindFeature1LCVis(BooleanProperty bol) {
		Feature1LC.visibleProperty().bind(bol);
	}
	void bindFeature2LCVis(BooleanProperty bol) {
		Feature2LC.visibleProperty().bind(bol);
	}

	
}
