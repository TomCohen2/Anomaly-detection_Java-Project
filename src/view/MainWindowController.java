package view;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import ViewModel.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Paint;
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
	StringProperty alert;
	AtomicBoolean playFlag;
	AtomicBoolean featureSelected;
	ViewModel viewModel;
	ExecutorService es = Executors.newFixedThreadPool(10);
	TimeSeriesAnomalyDetector.GraphStruct test;
	
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
		
		featureSelected = new AtomicBoolean(false);
		playFlag = new AtomicBoolean(false);
		
		//binding tabs for some system logic
		viewModel.getIProperty("Tabs").bind(TabPanel.getSelectionModel().selectedIndexProperty());
		
										//Settings
		
		//binding Settings
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
		
		//binding Graphs panel
		graphs.controller.bindFlightGearBoolean(viewModel.getBProperty("FlightGear"));
		graphs.controller.bindBadPoints(viewModel.getBadPoints());
		graphs.controller.bindGoodPoints(viewModel.getGoodPoints());
		graphs.controller.bindLine(viewModel.getLine());
		graphs.controller.bindFeature1(viewModel.getFeature1());
		graphs.controller.bindFeature2(viewModel.getFeature2());
		graphs.controller.bindLineChartVis(viewModel.getBProperty("LCVis"));
		graphs.controller.bindBubbleChartVis(viewModel.getBProperty("BCVis"));
		graphs.controller.bindFeature1LCVis(viewModel.getBProperty("F1Vis"));
		graphs.controller.bindFeature2LCVis(viewModel.getBProperty("F2Vis"));
		graphs.controller.bindWelzlCircle(viewModel.getWelzlCircle());
		graphs.controller.bindBadWelzl(viewModel.getBadWelzl());
		graphs.controller.bindGoodWelzl(viewModel.getGoodWelzl());
		
		//binding Player panel
		player.controller.bindCurTimeStep(viewModel.getSProperty("CurTimeStep"));
		player.controller.bindMaxTimestep(viewModel.getSProperty("MaxTimeStep"));
		player.controller.bindPlaySpeed(viewModel.getSProperty("PlaySpeed"));
		player.controller.bindTimeStepSlider(viewModel.getIProperty("TimeStep"));
		player.controller.bindTimeStepSliderChange(viewModel.getBProperty("BarChanging"));
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
		algorithms.controller.onSelectAlgo = ()->{
			String str = algorithms.controller.getSelectedAlgo();
			viewModel.algoSelected(str);
			if(str!=null) {
			switch(str) {
			case "Linear-Regression":
				graphs.controller.linearView();
				break;
			case "Z-Score":
				graphs.controller.zScoreView();
				break;
			case "Hybrid":
				graphs.controller.welzlView();
			}
			}
		};
		algorithms.controller.onUpTrainCSV = settings.controller.onCSVFile;
		algorithms.controller.onUpTestCSV = ()->viewModel.uploadTestFile();
		algorithms.controller.addToAlgoList(viewModel.getAlgorithms());
		
		player.controller.onPause = ()->{
			viewModel.pause();
			playFlag.set(false);
		};
		player.controller.onStop = ()->{
			viewModel.stop();
			playFlag.set(false);
		};
		
		player.controller.onFastForward = ()->viewModel.fastForward();
		player.controller.onFFastForawrd = ()->viewModel.superFastForward();
		player.controller.onRewind = ()->viewModel.rewind();
		player.controller.onFRewind = ()->viewModel.fastRewind();
		player.controller.timeStepSlider.setMin(0);
		player.controller.onPlay = ()->{
			if (!playFlag.get()) {
				playFlag.set(true);
				viewModel.play();
				es.submit(()->joystick.controller.paint());
				}
			viewModel.play();
		};
		
		player.controller.flightSelection.getItems().add("Train Flight");
		player.controller.flightSelection.getItems().add("Test Flight");
		player.controller.flightSelection.setValue(null);
		
		joystick.controller.paintJoystick = ()->{
				double radius = 30.0;
				GraphicsContext gc = joystick.controller.canvas.getGraphicsContext2D();
				while(playFlag.get()) {
					double mx = joystick.controller.canvas.getWidth()/2; //mx = middle x
					double my = joystick.controller.canvas.getHeight()/2; //my = middle y
					double jx = Double.parseDouble(dashboard.controller.AileronVal.textProperty().get());;
					double jy = Double.parseDouble(dashboard.controller.ElevatorVal.textProperty().get());;
					gc.clearRect(0, 0, joystick.controller.canvas.getWidth(), joystick.controller.canvas.getHeight());
					gc.setFill(Paint.valueOf("BLACK"));
					gc.fillOval(mx-radius/2+jx*radius, my-radius/2+(-jy*radius), radius, radius);
					gc.strokeOval(mx-radius, my-radius, 2*radius, 2*radius);
					try {Thread.sleep(100);} catch (InterruptedException e) {}	
				}
		};
		
		featureList.controller.featSelected = ()->{
				featureSelected.set(true);
				graphs.controller.clearSeries();
				viewModel.featureSelected(featureList.controller.FeaturesList.getSelectionModel().getSelectedItem());
		};
		
	}
}