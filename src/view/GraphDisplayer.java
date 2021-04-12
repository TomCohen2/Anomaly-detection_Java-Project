package view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
import test.TimeSeries;
import test.ZscoreAnomalyDetectorV2;
import javafx.beans.property.StringProperty;

public class GraphDisplayer extends LineChart{

	float[] vals;
	private StringProperty featureName;
	float maxValue;
	

	public GraphDisplayer(Axis arg0, Axis arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stu	
	}


	public float[] getVals() {
		return vals;
	}


	public void setData(float[] data) {
		this.vals = data;
	}
	
		
	}

