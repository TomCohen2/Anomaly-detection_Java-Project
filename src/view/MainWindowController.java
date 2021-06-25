package view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ViewModel.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Paint;
import test.Line;
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
	
	ViewModel viewModel;
	
	
	StringProperty alert;
	ExecutorService es = Executors.newFixedThreadPool(2);
	
	
	public void setViewModel(ViewModel viewModel){
		this.viewModel = viewModel;
		
		alert = new SimpleStringProperty();
		alert.bind(viewModel.getSProperty("Alert"));
		alert.addListener((obs,old,nw)->{
			if(nw != "") {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Alert");
				alert.setContentText(nw);
				alert.showAndWait();
				if(nw.contains("no such file: ")) {
					Platform.runLater(()->player.controller.flightSelection.getSelectionModel().clearSelection());
					}	
				}
			});
		
		
		
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
		player.controller.onPlay = ()->{
			viewModel.play();
			es.submit(()->joystick.controller.paint());
		};
		player.controller.flightSelection.getItems().add("Train Flight");
		player.controller.flightSelection.getItems().add("Test Flight");

		
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
				if(null!=viewModel.featureSelected(featureList.controller.FeaturesList.getSelectionModel().getSelectedItem())) {
					System.out.println("yaniv2");
					graphs.controller.removeFromLineChart();
					float min,max;
					//min = viewModel.getMinValue(FeaturesList.getSelectionModel().getSelectedItem());
					//max = viewModel.getMaxValue(FeaturesList.getSelectionModel().getSelectedItem());
					TimeSeriesAnomalyDetector.GraphStruct test = viewModel.featureSelected(featureList.controller.FeaturesList.getSelectionModel().getSelectedItem());
					min = test.getMinVal();
					max = test.getMaxVal();
					
					String[]args = test.getStr().split(",");
					//xAxis = new NumberAxis(0,50,1);
					//yAxis = new NumberAxis(-25,25,1);
					//xAxis.setAutoRanging(true);
					//yAxis.setAutoRanging(true);
					XYChart.Series<Number,Number> series = new XYChart.Series<>();
					XYChart.Series<Number,Number> series2 = new XYChart.Series<>();//for the line
					XYChart.Series<Number,Number> feature1Series = new XYChart.Series<>();
					XYChart.Series<Number,Number> feature2Series = new XYChart.Series<>();
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
					series2.getData().add(new Data<Number, Number>(min,l.f(min)));
					series2.getData().add(new XYChart.Data<Number, Number>(max,l.f(max)));
					graphs.controller.LineChart.setCreateSymbols(true);
					graphs.controller.Feature1LC.setCreateSymbols(false);
					graphs.controller.Feature2LC.setCreateSymbols(false);
					//LineChart.getXAxis().setLayoutX(100);
					//LineChart.getYAxis().setLayoutY(100);
					graphs.controller.add2LineChart(series2);
					graphs.controller.add2LineChart(series);
					graphs.controller.add2Feature1(feature1Series);
					graphs.controller.add2Feature2(feature2Series);
					//ScatterChart.getData().addAll(series);
				}	
				System.out.println("Feature has no correlated feature that holds the threashold");
		};
		
	}
	
	
}
