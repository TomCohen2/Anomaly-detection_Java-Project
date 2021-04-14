package view;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import test.AnomalyReport;
import test.TimeSeries;
import test.ZscoreAnomalyDetector1;

public class MainWindowController implements Initializable{
	static int MagicNumber = 1;
	ZscoreAnomalyDetector1 zad;
	TimeSeries ts;
	TimeSeries ts2;
	@FXML
	NumberAxis xAxis;
	@FXML
	NumberAxis yAxis;
	@FXML
	LineChart<?, ?> trainVOT; //
	@FXML
	LineChart<?, ?> trainZScoreTx; //
	@FXML
	LineChart<?, ?> trainMean;//
	@FXML
	LineChart<?, ?> trainSTDDev;//
	@FXML
	LineChart<?, ?> testVOT;//
	@FXML
	LineChart<?, ?> testZScore;
	@FXML
	LineChart<?, ?> testMean;
	@FXML
	LineChart<?, ?> testSTDDev;
	@FXML
	LineChart<?, ?> bothVOT;
	@FXML
	LineChart<?, ?> bothZScore;
	@FXML
	LineChart<?, ?> bothMean;
	@FXML
	LineChart<?, ?> bothSTDDev;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		zad = new ZscoreAnomalyDetector1();
		ts = new TimeSeries("trainFile1.csv");
		ts2 = new TimeSeries("testFile1.csv");
		xAxis = new NumberAxis(0,ts.getNumOfRows(),1);
		yAxis = new NumberAxis(-5,5,1);
		XYChart.Series series = new XYChart.Series();
		XYChart.Series series2 = new XYChart.Series();
		XYChart.Series series3 = new XYChart.Series();
		XYChart.Series series4 = new XYChart.Series();
		XYChart.Series series5 = new XYChart.Series();
		XYChart.Series series6 = new XYChart.Series();
		XYChart.Series series7 = new XYChart.Series();
		XYChart.Series series8 = new XYChart.Series();
		XYChart.Series series9 = new XYChart.Series();
		XYChart.Series series1 = new XYChart.Series();
		XYChart.Series series12 = new XYChart.Series();
		XYChart.Series series13 = new XYChart.Series();
		XYChart.Series series14 = new XYChart.Series();
		XYChart.Series series15 = new XYChart.Series();
		XYChart.Series series16 = new XYChart.Series();
		XYChart.Series series17 = new XYChart.Series();
		XYChart.Series series18 = new XYChart.Series();
		XYChart.Series series19 = new XYChart.Series();
		
		series.setName("Values over Time [" + ts.getFeatures()[MagicNumber] + "]");
		series2.setName("Z-Scores [" + ts.getFeatures()[MagicNumber] + "]");
		series3.setName("TX [" + ts.getFeatures()[MagicNumber] + "]");
		series4.setName("Means [" + ts.getFeatures()[MagicNumber] + "]");
		series5.setName("StandardDevs [" + ts.getFeatures()[MagicNumber] + "]");
		series6.setName("Values over Time [" + ts2.getFeatures()[MagicNumber] + "]");
		series7.setName("Z-Scores [" + ts2.getFeatures()[MagicNumber] + "]");
		series8.setName("Means [" + ts2.getFeatures()[MagicNumber] + "]");
		series9.setName("StandardDevs [" + ts2.getFeatures()[MagicNumber] + "]");
		
		series13.setName("TX [" + ts.getFeatures()[MagicNumber] + "]");
		series1.setName("Train Flight Values over Time [" + ts.getFeatures()[MagicNumber] + "]");
		series12.setName("Train Flight Z-Scores [" + ts.getFeatures()[MagicNumber] + "]");
		series14.setName("Train Flight Means [" + ts.getFeatures()[MagicNumber] + "]");
		series15.setName("Train Flight StandardDevs [" + ts.getFeatures()[MagicNumber] + "]");
		series16.setName("Test Flight Values over Time [" + ts2.getFeatures()[MagicNumber] + "]");
		series17.setName("Test Flight Z-Scores [" + ts2.getFeatures()[MagicNumber] + "]");
		series18.setName("Test Flight Means [" + ts2.getFeatures()[MagicNumber] + "]");
		series19.setName("Test Flight StandardDevs [" + ts2.getFeatures()[MagicNumber] + "]");
		zad.learnNormal(ts);
		List<AnomalyReport> ZSReports = zad.detect(ts2);
		int j=0;
		for (AnomalyReport a : ZSReports) {
			System.out.println(a);
			++j;
		}
		System.out.println(j + " Anomalies were found.");
		
		for(int i=0;i<ts.getNumOfRows();i++) {
			series.getData().add(new XYChart.Data(i, ts.getData()[MagicNumber][i])); // trainVOT
			series2.getData().add(new XYChart.Data(i, zad.getzScores()[MagicNumber][i])); //trainZscores
			series3.getData().add(new XYChart.Data(i, zad.getTXtest()[MagicNumber][i])); //trainTX
			series4.getData().add(new XYChart.Data(i, zad.getMeans()[MagicNumber][i])); //trainMeans
			series5.getData().add(new XYChart.Data(i, zad.getStddev()[MagicNumber][i])); //trainSTDDev
			series6.getData().add(new XYChart.Data(i, ts2.data[MagicNumber][i])); //testVOT
			series7.getData().add(new XYChart.Data(i, zad.getTestzScores()[MagicNumber][i])); //testZScores
			series8.getData().add(new XYChart.Data(i, zad.getTestMeans()[MagicNumber][i])); //testMean
			series9.getData().add(new XYChart.Data(i, zad.getTestStddev()[MagicNumber][i])); //testSTDDev
		
			series1.getData().add(new XYChart.Data(i, ts.getData()[MagicNumber][i])); // trainVOT
			series12.getData().add(new XYChart.Data(i, zad.getzScores()[MagicNumber][i])); //trainZscores
			series13.getData().add(new XYChart.Data(i, zad.getTXtest()[MagicNumber][i])); //trainTX
			series14.getData().add(new XYChart.Data(i, zad.getMeans()[MagicNumber][i])); //trainMeans
			series15.getData().add(new XYChart.Data(i, zad.getStddev()[MagicNumber][i])); //trainSTDDev
			series16.getData().add(new XYChart.Data(i, ts2.data[MagicNumber][i])); //testVOT
			series17.getData().add(new XYChart.Data(i, zad.getTestzScores()[MagicNumber][i])); //testZScores
			series18.getData().add(new XYChart.Data(i, zad.getTestMeans()[MagicNumber][i])); //testMean
			series19.getData().add(new XYChart.Data(i, zad.getTestStddev()[MagicNumber][i])); //testSTDDev
		}
	
//		for (Object o : series.getData()) {
//			System.out.println(o);
//		}
		//System.out.println(gd.getData().size());
		trainVOT.setCreateSymbols(false);
		trainZScoreTx.setCreateSymbols(false);
		trainMean.setCreateSymbols(false);
		trainSTDDev.setCreateSymbols(false);
		testVOT.setCreateSymbols(false);
		testZScore.setCreateSymbols(false);
		testMean.setCreateSymbols(false);
		testSTDDev.setCreateSymbols(false);
		bothVOT.setCreateSymbols(false);
		bothZScore.setCreateSymbols(false);
		bothMean.setCreateSymbols(false);
		bothSTDDev.setCreateSymbols(false);
		
		trainVOT.getData().add(series);
		trainZScoreTx.getData().add(series2);
		//trainZScoreTx.getData().add(series3);
		trainMean.getData().add(series4);
		trainSTDDev.getData().add(series5);
		testVOT.getData().add(series6);
		testZScore.getData().add(series7);
		testMean.getData().add(series8);
		testSTDDev.getData().add(series9);
		bothVOT.getData().addAll(series1, series16);
		bothZScore.getData().addAll(series12, series17, series13);
		bothMean.getData().addAll(series14,series18);
		bothSTDDev.getData().addAll(series15,series19);
		//System.out.println(gd.getData().size());
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
