package test;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.XYChart;

public interface TimeSeriesAnomalyDetector {
	void learnNormal(TimeSeries ts);
	List<AnomalyReport> detect(TimeSeries ts);
	GraphStruct display(String colName);
	
	
	class GraphStruct{
		XYChart.Series<Number,Number> points;
		XYChart.Series<Number,Number> feature1Points;
		XYChart.Series<Number,Number> feature2Points;
		Line l;
		Circle c;
		String str;
		float minVal;
		float maxVal;
		float minYVal;
		float maxYVal;
		float[] zScores;
		
		
		
		public XYChart.Series<Number, Number> getFeature1Points() {
			return feature1Points;
		}

		public void setFeature1Points(XYChart.Series<Number, Number> feature1Points) {
			this.feature1Points = feature1Points;
		}
		public void setFeature2Points(XYChart.Series<Number, Number> feature2Points) {
			this.feature2Points = feature2Points;
		}

		public XYChart.Series<Number, Number> getFeature2Points() {
			return feature2Points;
		}


		public GraphStruct() {
			//linear reg
			this.points = new XYChart.Series<>();
			this.feature1Points = new XYChart.Series<>();
			this.feature2Points = new XYChart.Series<>();
			l = null;
			minVal = Float.MIN_VALUE;
			maxVal = Float.MAX_VALUE;
			minYVal = Float.MIN_VALUE;
			maxYVal = Float.MAX_VALUE;
			//hybrid
			c = null;			
			zScores=null;
			str = null;
		}

		public GraphStruct(XYChart.Series<Number,Number> points,XYChart.Series<Number,Number> feature1Points,XYChart.Series<Number,Number> feature2Points, Line l, Circle c, String str, float min, float max, float minY, float maxY, float[] zScores) {
			super();
			this.points.getData().addAll(points.getData());
			this.feature1Points.getData().addAll(feature1Points.getData());
			this.feature2Points.getData().addAll(feature2Points.getData());
			this.l = l;
			this.c = c;
			this.str=str;
			this.minVal=min;
			this.maxVal=max;
			this.minYVal=minY;
			this.maxYVal=maxY;
			this.zScores = new float[zScores.length];
			setzScores(zScores);
			
		}
		
		public float[] getzScores() {
			return zScores;
		}

		public void setzScores(float[] zScores) {
			this.zScores = new float[zScores.length];
			for(int i=0;i<zScores.length;i++)
				this.zScores[i] = zScores[i];
		}

		public float getMinYVal() {
			return minYVal;
		}

		public void setMinYVal(float minYVal) {
			this.minYVal = minYVal;
		}

		public float getMaxYVal() {
			return maxYVal;
		}

		public void setMaxYVal(float maxYVal) {
			this.maxYVal = maxYVal;
		}

		public float getMinVal() {
			return minVal;
		}

		public void setMinVal(float minVal) {
			this.minVal = minVal;
		}

		public float getMaxVal() {
			return maxVal;
		}

		public void setMaxVal(float maxVal) {
			this.maxVal = maxVal;
		}

		public String getStr() {
			return str;
		}

		public void setStr(String str) {
			this.str = str;
		}

		public XYChart.Series<Number,Number> getPoints() {
			return points;
		}
		public void setPoints(XYChart.Series<Number,Number> points) {
			this.points.getData().addAll(points.getData());
		}
		public Line getL() {
			return l;
		}
		public void setL(Line l) {
			this.l = l;
		}
		public Circle getC() {
			return c;
		}
		public void setC(Circle c) {
			this.c = c;
		}
		
		
	}
}
