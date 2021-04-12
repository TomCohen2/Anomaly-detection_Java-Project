package view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import test.TimeSeries;
import test.ZscoreAnomalyDetectorV2;
public class MainWindowController implements Initializable{
	static int MagicNumber = 17;
	ZscoreAnomalyDetectorV2 zad;
	TimeSeries ts;
	TimeSeries ts2;
	@FXML
	NumberAxis xAxis;
	@FXML
	NumberAxis yAxis;
	@FXML
	LineChart<?, ?> gd;
	@FXML
	LineChart<?, ?> gd2;
	@FXML
	LineChart<?, ?> gd3;
	@FXML
	LineChart<?, ?> gd4;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		zad = new ZscoreAnomalyDetectorV2();
		ts = new TimeSeries("reg_flight.csv");
		ts2 = new TimeSeries("anomaly_flight.csv");
		xAxis = new NumberAxis(0,ts.getNumOfRows(),1);
		yAxis = new NumberAxis(-5,5,1);
		XYChart.Series series = new XYChart.Series();
		XYChart.Series series2 = new XYChart.Series();
		XYChart.Series series3 = new XYChart.Series();
		XYChart.Series series4 = new XYChart.Series();
		XYChart.Series series5 = new XYChart.Series();
		XYChart.Series series6 = new XYChart.Series();
		series.setName("TRAIN " + ts.getFeatures()[MagicNumber]);
		series2.setName("TEST " + ts.getFeatures()[MagicNumber]);
		series3.setName("Zscore " + ts.getFeatures()[MagicNumber]);
		series4.setName("TX " + ts.getFeatures()[MagicNumber]);
		series5.setName("Means" + ts.getFeatures()[MagicNumber]);
		series6.setName("Standard Deviation" + ts.getFeatures()[MagicNumber]);
		zad.learnNormal(ts);
		for(int i=0;i<ts.getNumOfRows();i++) {
			//System.out.println(ts.data[0][i]);
			series.getData().add(new XYChart.Data(i, ts.data[MagicNumber][i]));
			series2.getData().add(new XYChart.Data(i, ts2.data[MagicNumber][i]));
			series3.getData().add(new XYChart.Data(i, zad.getzScores()[MagicNumber][i]));
			series4.getData().add(new XYChart.Data(i, zad.getTXtest()[MagicNumber][i]));
			series5.getData().add(new XYChart.Data(i, zad.getMeans()[MagicNumber][i]));
			series6.getData().add(new XYChart.Data(i, zad.getStddev()[MagicNumber][i]));
		}
		
//		for (Object o : series.getData()) {
//			System.out.println(o);
//		}
		System.out.println(gd.getData().size());
		gd.getData().add(series);
		gd.getData().add(series2);
		gd2.getData().add(series3);
		gd2.getData().add(series4);
		gd4.getData().add(series5);
		gd3.getData().add(series6);
		System.out.println(gd.getData().size());
//		for (int i=0;i<ts.getNumOfRows();i++) {
//			System.out.println(gd.getData().get(0).getData().get(i));
//		}

//		gd.setOnKeyPressed(new EventHandler<KeyEvent>() {
//
//			@Override
//			public void handle(KeyEvent arg0) {
//				if(arg0.getCode() == KeyCode.ENTER)
//					gd.reDraw();
//				
//			}
//			
//		});
		
		
	}

}
