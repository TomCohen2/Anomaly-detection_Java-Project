package view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import ViewModel.ViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import test.Line;
import test.Point;
import test.TimeSeriesAnomalyDetector;

public class MainWindowController {
	@FXML TabPane TabPanel;

	//Simulation tab
	@FXML Settings settings;
	@FXML Joystick joystick;
	@FXML Dashboard dashboard;
	
	//Anomaly Detection tab
	@FXML Algorithms algorithms;
	@FXML FeatureList featureList;
	@FXML Graphs graphs;
	
	//Both tabs
	@FXML Player player;
	
	AtomicBoolean featureSelected;
	ViewModel viewModel;
	ExecutorService es = Executors.newFixedThreadPool(10);
	TimeSeriesAnomalyDetector.GraphStruct test;
	
	public void setViewModel(ViewModel viewModel){
		this.viewModel = viewModel;
		featureSelected = new AtomicBoolean(false);
		//binding tabs for some system logic
		viewModel.getIProperty("Tabs").bind(TabPanel.getSelectionModel().selectedIndexProperty());
						//Settings
		//binding
		settings.controller.bindLoadedSettings(viewModel.getSProperty("LastSettings"));
		settings.controller.bindFlightGearBoolean(viewModel.getBProperty("FlightGear"));;
		
		
		//binding Joystick
		joystick.controller.bindRudderSlider(viewModel.getDProperty("Rudder"));
		joystick.controller.bindThrottleSlider(viewModel.getDProperty("Throttle"));
		
		//binding Dashboard
		dashboard.controller.bindAileronVal(viewModel.getDProperty("Aileron"));
		dashboard.controller.bindElevatorVal(viewModel.getDProperty("Elevator"));
		dashboard.controller.bindFlightHeightVal(viewModel.getDProperty("FlightHeight"));
		dashboard.controller.bindFlightSpeedVal(viewModel.getDProperty("FlightSpeed"));
		dashboard.controller.bindPitchVal(viewModel.getDProperty("Pitch"));
		dashboard.controller.bindRollVal(viewModel.getDProperty("Roll"));
		dashboard.controller.bindRudderVal(viewModel.getDProperty("Rudder"));
		dashboard.controller.bindThrottleVal(viewModel.getDProperty("Throttle"));
		dashboard.controller.bindYawVal(viewModel.getDProperty("Yaw"));
		
		//binding Algorithms panel
		algorithms.controller.bindAlgorithms(viewModel.getSProperty("AlgoLabel"));
		algorithms.controller.bindTrainFileName(viewModel.getSProperty("TrainFileName"));
		algorithms.controller.bindTestFileName(viewModel.getSProperty("TestFileName"));
		
		//binding FeatureList
		
		//binding Player panel
		player.controller.bindCurTimeStep(viewModel.getSProperty("CurTimeStep"));
		player.controller.bindMaxTimestep(viewModel.getSProperty("MaxTimeStep"));
		player.controller.bindPlaySpeed(viewModel.getSProperty("PlaySpeed"));
		player.controller.bindTimeStepSlider(viewModel.getIProperty("TimeStep"));
		player.controller.bindTimeSliderMax(viewModel.getIProperty("MaxTime"));
		player.controller.bindFlightSelected(viewModel.getSProperty("SelectedFlightToDisplay"));
	}
	
	public void printGraphs() {
		test = viewModel.getGraphStruct();
		graphs.controller.removeFromLineChart();
		float min,max;
		//min = viewModel.getMinValue(FeaturesList.getSelectionModel().getSelectedItem());
		//max = viewModel.getMaxValue(FeaturesList.getSelectionModel().getSelectedItem());
		min = test.getMinVal();
		max = test.getMaxVal();
			
		String[]args = test.getStr().split(",");
		XYChart.Series<Number,Number> series = new XYChart.Series<>();
		XYChart.Series<Number,Number> series2 = new XYChart.Series<>();//for the line and circle
		XYChart.Series<Number,Number> series3 = new XYChart.Series<>();
		switch(args[0]) {
		case "LR":
			graphs.controller.linearView();
			graphs.controller.removeFromLineChart();
			graphs.controller.setLineChartID("LR");
			//xAxis = new NumberAxis(0,50,1);
			//yAxis = new NumberAxis(-25,25,1);
			//xAxis.setAutoRanging(true);
			//yAxis.setAutoRanging(true);
			XYChart.Series<Number,Number> feature1Series = new XYChart.Series<>();
			XYChart.Series<Number,Number> feature2Series = new XYChart.Series<>();
			//System.out.println(args[1] + " " + args[2]);
			feature1Series.setName(args[1]);
			feature2Series.setName(args[2]);
			series.setName("Points");
			test.getPoints().getData().forEach((a)->{
				Node node = a.getNode();
				//a.getNode().setScaleX(0.1);
				//a.getNode().setScaleY(0.1);
				series.getData().add(a);
			});
			test.getFeature1Points().getData().forEach(a->{
				Node node = a.getNode();
				feature1Series.getData().add(a);
			});
			test.getFeature2Points().getData().forEach(a->{
				Node node = a.getNode();
				feature2Series.getData().add(a);
			});
			Line l = test.getL();
			series2.setName("Correlation line");
			series2.getData().add(new XYChart.Data<Number, Number>(min,l.f(min)));
			series2.getData().add(new XYChart.Data<Number, Number>(max,l.f(max)));
			graphs.controller.LineChart.setCreateSymbols(true);
			graphs.controller.Feature1LC.setCreateSymbols(false);
			graphs.controller.Feature2LC.setCreateSymbols(false);
			//graphs.controller.LineChart.setLegendVisible(false);
			//graphs.controller.LineChart.setAnimated(false);
			//graphs.controller.LineChart.setVerticalZeroLineVisible(false);
			
			//LineChart.getXAxis().setLayoutX(100);
			//LineChart.getYAxis().setLayoutY(100);
			graphs.controller.add2LineChart(series);
			graphs.controller.add2LineChart(series2);
			graphs.controller.add2Feature1(feature1Series);
			graphs.controller.add2Feature2(feature2Series);
			break;
		case "Z":
			graphs.controller.zScoreView();
			graphs.controller.removeFromLineChart();
			graphs.controller.setLineChartID("Z");
			l = test.getL();
			series.getData().clear();
			max = test.getMaxVal();
			test.getPoints().getData().forEach((a)->{
				//a.getNode().setScaleX(0.1);
				//a.getNode().setScaleY(0.1);
			//	System.out.println("woop woop "+a);
				series.getData().add(a);
			});
			
			series2.getData().clear();
			series2.getData().add(new XYChart.Data<Number, Number>(0,l.f(0)));
			series2.getData().add(new XYChart.Data<Number, Number>(max,l.f(0)));
			series3.getData().add(new XYChart.Data<Number,Number>(0,0));
			series2.setName("TX");
			series.setName("Z-Scores feature " + args[1]);
			series3.setName("(0,0)");
			graphs.controller.add2LineChart(series,series3,series2);
			//graphs.controller.add2LineChart(series3);
			//graphs.controller.add2LineChart(series2);
			break;
		case "HYB":
			graphs.controller.welzlView();
			graphs.controller.removeFromBubbleChart();
			graphs.controller.removeFromLineChart();
			graphs.controller.setBubbleChartID("welzl");;
			Point center = test.getC().getCenter();
			float radius = test.getC().getRadius();
			Series<Number, Number> welzl = new Series<>();
			welzl.getData().add(new XYChart.Data<>(center.x,center.y,radius));
			welzl.setName("Min Enclosing Circle");
			graphs.controller.add2Welzl(welzl);
			graphs.controller.add2Welzl(test.getPoints());
			break;					
		}
	}
	
	public void init() {
		settings.controller.addToSettingsList(viewModel.getSavedSettingFileNames());
		settings.controller.onDelete = ()->viewModel.deleteSettingsFile(settings.controller.SettingsFilesListView.getSelectionModel().getSelectedItem());
		settings.controller.onLoadSettings = ()-> viewModel.selectSettings(settings.controller.SettingsFilesListView.getSelectionModel().getSelectedItem());
		settings.controller.onNewSettings = ()->viewModel.newSettingsFile();
		settings.controller.onCSVFile = ()->{
			viewModel.openCSVFile();
			featureList.controller.addAll(viewModel.getFeatureList());
		};
		
		algorithms.controller.onDelete = ()->viewModel.deleteAlgo(algorithms.controller.getSelectedAlgo());
		algorithms.controller.onNewAlgo = ()->viewModel.newAlgoFile();
		algorithms.controller.onSelectAlgo = ()->viewModel.algoSelected(algorithms.controller.getSelectedAlgo());
		algorithms.controller.onUpTrainCSV = settings.controller.onCSVFile;
		algorithms.controller.onUpTestCSV = ()->viewModel.uploadTestFile();
		algorithms.controller.addToAlgoList(viewModel.getAlgorithms());
		
		player.controller.onPause = ()->viewModel.pause();
		player.controller.onStop = ()->viewModel.stop();
		player.controller.onFastForward = ()->viewModel.fastForward();
		player.controller.onFFastForawrd = ()->viewModel.superFastForward();
		player.controller.onRewind = ()->viewModel.rewind();
		player.controller.onFRewind = ()->viewModel.fastRewind();
		player.controller.timeStepSlider.setMin(0);
	//	player.controller.timeStepSlider.setMax(viewModel.getMaxTimeStep());
		player.controller.onPlay = ()->{
			viewModel.play();
			es.submit(()->joystick.controller.paint());
			if(featureSelected.get()) {
				System.out.println("######################################################");
				//Platform.runLater(()->{
				es.submit(()->{
						int i=0;
					//	while(true) {
						System.out.println("The truth is out there " + i++);
						featureList.controller.featureSelected();
						//Thread.sleep(1000)
						//}
				});
			}
		};
		player.controller.flightSelection.getItems().add("Train Flight");
		player.controller.flightSelection.getItems().add("Test Flight");
		player.controller.flightSelection.setValue(null);
		
		joystick.controller.paintJoystick = ()->{
			double radius = 30.0;
			while(true) {
			GraphicsContext gc = joystick.controller.canvas.getGraphicsContext2D();
			double mx = joystick.controller.canvas.getWidth()/2; //mx = middle x
			double my = joystick.controller.canvas.getHeight()/2; //my = middle y
			double jx = Double.parseDouble(dashboard.controller.AileronVal.textProperty().get());;
			double jy = Double.parseDouble(dashboard.controller.ElevatorVal.textProperty().get());;
			gc.clearRect(0, 0, joystick.controller.canvas.getWidth(), joystick.controller.canvas.getHeight());
			gc.setFill(Paint.valueOf("BLACK"));
			gc.fillOval(mx-radius/2+jx*radius, my-radius/2+(-jy*radius), radius, radius);
		//	gc.strokeOval(mx/(jx-mx)-15, my/(jy-my)-15, 30, 30);
			gc.strokeOval(mx-radius, my-radius, 2*radius, 2*radius);
			try {Thread.sleep(50);} catch (InterruptedException e) {}
		}};
		
		featureList.controller.featSelected = ()->{
			//	if(featureSelected.get()) {	
			//if(null!=viewModel.featureSelected(featureList.controller.FeaturesList.getSelectionModel().getSelectedItem())) {
				featureSelected.set(true);
				viewModel.featureSelected(featureList.controller.FeaturesList.getSelectionModel().getSelectedItem());
				//ScatterChart.getData().addAll(series);
			//}	
			//System.out.println("Feature has no correlated feature that holds the threashold");
		};
		
	}
	
	
}
