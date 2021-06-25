package GraphTest;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Circle;
import test.Point;
import test.StatLib;
import test.TimeSeries;

public class GraphController {
	test.Circle circle;
    float minX;
    float maxX;
    float minY;
    float maxY;
    Point[] p;
	@FXML
	BubbleChart<Float, Float> bubbleChart;
	int i=0;
	@FXML
	NumberAxis yAxis;
	@FXML
	NumberAxis xAxis;
	XYChart.Series<Float,Float> series = new XYChart.Series<>();
	XYChart.Series<Float,Float> series2 = new XYChart.Series<>();
	public GraphController(){
		yAxis = new NumberAxis();
		xAxis = new NumberAxis();
		TimeSeries regTS = new TimeSeries("reg_flight.csv");
		TimeSeries anomalyTS = new TimeSeries("anomaly_flight.csv");
		List<Point> points = new ArrayList<>();
		float[][] data = regTS.getData();
        p = StatLib.arrToPoints(data[24], data[25]);
        for (Point a : p)
            points.add(a);
        circle = StatLib.makeCircle(points);
        minX = Float.MAX_VALUE;
        maxX = Float.MIN_VALUE;
        minY = Float.MAX_VALUE;
        maxY = Float.MIN_VALUE;
        data = anomalyTS.getData();
        p = StatLib.arrToPoints(data[24], data[25]);
	}
	
	public void init() {
		yAxis.setUpperBound(2000);
		yAxis.setLowerBound(-2000);
		xAxis.setUpperBound(2000);
		xAxis.setLowerBound(-2000);
		series.getData().add(new XYChart.Data<>(circle.center.x,circle.center.y,circle.radius));
		bubbleChart.getData().add(series);
	}
	
	public void run(){
			addPoint(p[i].x, p[i].y);
			i++;
	}
	
	public void addPoint(float x,float y) {
		bubbleChart.getData().clear();
		series2.getData().add(new XYChart.Data<>(x,y,1));
		bubbleChart.getData().add(series);
		bubbleChart.getData().add(series2);
	}
}
