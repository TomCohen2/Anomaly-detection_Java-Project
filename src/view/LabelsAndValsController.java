package view;

import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import com.sun.prism.paint.Color;

import ViewModel.ViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.paint.Paint;
import test.Line;
import test.TimeSeriesAnomalyDetector;

public class LabelsAndValsController extends Observable{// implements Initializable {
	
	//Data
	@FXML
	Label AileronVal;
	@FXML
	Label ElevatorVal;
	@FXML
	Label RudderVal;
	@FXML
	Label ThrottleVal;
	@FXML
	Label FlightHeightVal;
	@FXML
	Label FlightSpeedVal;
	@FXML
	Label RollVal;
	@FXML
	Label PitchVal;
	@FXML
	Label YawVal;
	
	//PlayerTime
	@FXML
	Label maxTimeStep;
	@FXML
	Label CurTimeStep;
	@FXML
	Slider timeStepSlider;
	@FXML
	TextField PlaySpeed;
	
	//Simulator
	@FXML
	CheckBox Simulator;
	
	//Settings tab
	@FXML
	Button NewSettingsFile;
	@FXML
	Button LoadSettingsFile;
	@FXML
	Button UploadCSVFile;
	@FXML
	ListView<String> SettingsFilesListView;
	@FXML
	Label StatusLabel;
	@FXML
	Label AlgorLabel;
	@FXML
	Label TrainFileName;
	@FXML
	Label TestFileName;
	@FXML 
	Label LoadedSettings;

	@FXML
	ListView<String> AlgoFilesListView;
	
	//Joystick
	@FXML
	Canvas canvas;
	@FXML
	Slider ThrottleSlider;
	@FXML
	Slider RudderSlider;
	
	
	//AnomalyDetectionTab
	@FXML
	TabPane TabPanel;
	//Graphs
	@FXML
	ListView<String> FeaturesList;
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
	
	//@FXML
	//ImageView ImageView;
	ViewModel viewModel;
	
	
	public void setViewModel(ViewModel viewModel) {
		this.viewModel = viewModel;
		
									//binding doubles
		//DashBoard
		AileronVal.textProperty().bind(viewModel.getDProperty("Aileron").asString());
		ElevatorVal.textProperty().bind(viewModel.getDProperty("Elevator").asString());
		RudderVal.textProperty().bind(viewModel.getDProperty("Rudder").asString());
		ThrottleVal.textProperty().bind(viewModel.getDProperty("Throttle").asString());
		FlightHeightVal.textProperty().bind(viewModel.getDProperty("FlightHeight").asString());
		FlightSpeedVal.textProperty().bind(viewModel.getDProperty("FlightSpeed").asString());
		RollVal.textProperty().bind(viewModel.getDProperty("Roll").asString());
		PitchVal.textProperty().bind(viewModel.getDProperty("Pitch").asString());
		YawVal.textProperty().bind(viewModel.getDProperty("Yaw").asString());
		//Joystick
		RudderSlider.valueProperty().bind(viewModel.getDProperty("Rudder"));
		ThrottleSlider.valueProperty().bind(viewModel.getDProperty("Throttle"));
		//Player
		maxTimeStep.textProperty().bind(viewModel.getSProperty("MaxTimeStep"));
		CurTimeStep.textProperty().bind(viewModel.getSProperty("CurTimeStep"));

		
		
									//Binding integers
		//Player
		timeStepSlider.valueProperty().bindBidirectional(viewModel.getIProperty("TimeStep"));
		
		
		
									//Binding booleans
		//Simulator
		viewModel.getBProperty("Simulator").bind(Simulator.selectedProperty());	
		SettingsFilesListView.setOrientation(Orientation.VERTICAL);
		SettingsFilesListView.getItems().setAll(viewModel.getSavedSettingFileNames());
		SettingsFilesListView.refresh();
		AlgoFilesListView.setOrientation(Orientation.VERTICAL);
		AlgoFilesListView.getItems().setAll(viewModel.getAlgorithms());
		AlgoFilesListView.refresh();
		FeaturesList.setOrientation(Orientation.VERTICAL);
		FeaturesList.getItems().setAll(viewModel.getFeatureList());
		FeaturesList.refresh();
		SettingsFilesListView.itemsProperty().bind(viewModel.getList("SettingsFileList"));
		AlgoFilesListView.itemsProperty().bind(viewModel.getList("AlgoFiles"));
		FeaturesList.itemsProperty().bind(viewModel.getList("FeaturesList"));
		//SettingsFilesListView.itemsProperty().bind(viewModel.getList("SettingsFileList"));
		timeStepSlider.setMax(viewModel.getMaxTimeStep());
		timeStepSlider.setMin(0);
		viewModel.getIProperty("Tabs").bind(TabPanel.getSelectionModel().selectedIndexProperty());
//		viewModel.bindElevator(ElevatorVal.textProperty());
//		viewModel.bindRudder(RudderVal.textProperty());
//		viewModel.bindThrottle(ThrottleVal.textProperty());
//		viewModel.bindFlightHeight(FlightHeightVal.textProperty());
//		viewModel.bindFlightSpeed(FlightSpeedVal.textProperty());
//		viewModel.bindRoll(RollVal.textProperty());
//		viewModel.bindPitch(PitchVal.textProperty());
//		viewModel.bindYaw(YawVal.textProperty());
//		viewModel.bindMaxTimeStep(maxTimeStep.textProperty());
//		viewModel.bindCurTimeStep(CurTimeStep.textProperty());
//		viewModel.bindTimeSlideBar(timeStepSlider.valueProperty());	
//		viewModel.bindSimulatorCB(Simulator.selectedProperty());
//		viewModel.bindRudder(RudderSlider.valueProperty());
//		viewModel.bindThrottle(ThrottleSlider.valueProperty());
//	    viewModel.bindFeature1(Feature1LC.get)
	
////		SettingsFilesListView.getSelectionModel().selectedItemProperty().addListener((obj, oldVal, newVal)->{
////			SelectedFeature.setText(listView.getSelectionModel().getSelectedItem());
			////System.out.println("You have selected " + SettingsFilesListView.getSelectionModel().getSelectedItem());

		
		//viewModel.bindFileSettingsListView(SettingsFilesListView.itemsProperty());
		//viewModel.bindAlgoFilesListView(AlgoFilesListView.itemsProperty());
		//viewModel.bindFeaturesListView(FeaturesList.itemsProperty());
		
		//timeStepSlider.setMax(viewModel.getMaxTimeStep()-1); //Future??
		
		//Setting's labels
		LoadedSettings.textProperty().bind(viewModel.getSProperty("LastSettings"));
		AlgorLabel.textProperty().bind(viewModel.getSProperty("AlgoLabel"));
		TrainFileName.textProperty().bind(viewModel.getSProperty("TrainFileName"));
		TestFileName.textProperty().bind(viewModel.getSProperty("TestFileName"));
		viewModel.getIProperty("Tabs").bind(TabPanel.getSelectionModel().selectedIndexProperty());
		PlaySpeed.textProperty().bindBidirectional(viewModel.getSProperty("PlaySpeed"));
		//viewModel.bindLoadedSettings(LoadedSettings.textProperty());
		//viewModel.bindAlgorithmSelected(AlgorLabel.textProperty());
		//viewModel.bindTrainFileName(TrainFileName.textProperty());
		//viewModel.bindTestFileName(TestFileName.textProperty());
		//viewModel.bindTabPane(TabPanel.getSelectionModel().selectedIndexProperty());
		//viewModel.bindPlaySpeed(PlaySpeed.textProperty());
		//ListView
		
	}
	
	public void play() {
		viewModel.play();
		paintJoystick();
		System.out.println("Play from View!");
	}
	
	public void pause() {
		viewModel.pause();
	}
	
	public void stop() {
		viewModel.stop();
	}
	
	public void fastForward() {
		viewModel.fastForward();
	}
	
	public void superFastForward() {
		viewModel.superFastForward();
	}
	
	public void rewind() {
		viewModel.rewind();
	}
	
	public void fastRewind() {
		viewModel.fastRewind();
	}
	
	public void newSettingsFile() {
		viewModel.newSettingsFile();
	}
	public void newAlgoFile() {
		viewModel.newAlgoFile();
	}
	public void openCSVFile() {
		viewModel.openCSVFile();
	}
	
	public void selectSettings() {
		viewModel.selectSettings(SettingsFilesListView.getSelectionModel().getSelectedItem());
	}
	
	public void deleteSettingsFile() {
		viewModel.deleteSettingsFile(SettingsFilesListView.getSelectionModel().getSelectedItem());
	}
	
	public void deleteAlgo() {
		viewModel.deleteAlgo(AlgoFilesListView.getSelectionModel().getSelectedItem());
	}
	
	
	public void paintJoystick() {
		new Thread(()->{
				double radius = 30.0;
				while(true) {
				GraphicsContext gc = canvas.getGraphicsContext2D();
				double mx = canvas.getWidth()/2; //mx = middle x
				double my = canvas.getHeight()/2; //my = middle y
				double jx = Double.parseDouble(AileronVal.textProperty().get());;
				double jy = Double.parseDouble(ElevatorVal.textProperty().get());;
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				gc.setFill(Paint.valueOf("BLACK"));
				gc.fillOval(mx-radius/2+jx*radius, my-radius/2+(-jy*radius), radius, radius);
				//gc.strokeOval(mx/(jx-mx)-15, my/(jy-my)-15, 30, 30);
				gc.strokeOval(mx-radius, my-radius, 2*radius, 2*radius);
				try {Thread.sleep(50);} catch (InterruptedException e) {}
				}
		}).start();
	}
	
	public void featureSelected() {
		if(null!=viewModel.featureSelected(FeaturesList.getSelectionModel().getSelectedItem())) {
			float min,max;
			//min = viewModel.getMinValue(FeaturesList.getSelectionModel().getSelectedItem());
			//max = viewModel.getMaxValue(FeaturesList.getSelectionModel().getSelectedItem());
			TimeSeriesAnomalyDetector.GraphStruct test = viewModel.featureSelected(FeaturesList.getSelectionModel().getSelectedItem());
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
			series2.getData().add(new Data<Number, Number>(0.0,l.f(0.0f)));
			series2.getData().add(new XYChart.Data<Number, Number>(max,l.f(max)));
			LineChart.setCreateSymbols(true);
			//LineChart.getXAxis().setLayoutX(100);
			//LineChart.getYAxis().setLayoutY(100);
			LineChart.getData().addAll(series2,series);
			Feature1LC.setCreateSymbols(false);
			Feature2LC.setCreateSymbols(false);
			Feature1LC.getData().addAll(feature1Series);
			Feature2LC.getData().addAll(feature2Series);
			//ScatterChart.getData().addAll(series);
		}	
		System.out.println("Feature has no correlated feature that holds the threashold");
	}
	
	public void algoSelected() {
		viewModel.algoSelected(AlgoFilesListView.getSelectionModel().getSelectedItem());
	}
	
	public void uploadTestFile() {
		viewModel.uploadTestFile();
	}
	

//	@Override
//	public void initialize(URL location, ResourceBundle resources) {
//		File file = new File("./Resources/Picture.jpg");
//		Image image = new Image(file.toURI().toString());
//		ImageView.setImage(image);
//	}
	
}
