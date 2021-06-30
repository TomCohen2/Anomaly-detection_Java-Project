package ViewModel;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.beans.binding.NumberExpressionBase;
import javafx.beans.property.BooleanProperty;
//import PTM2Proect.src.test.Model;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import test.Model;
import test.TimeSeriesAnomalyDetector;

public class ViewModel implements Observer{
	Model model;
	HashMap<String,NumberExpressionBase> NPropertyMap;
	HashMap<String,StringProperty> SPropertyMap;
	HashMap<String,BooleanProperty> BPropertyMap;
	HashMap<String,ObjectProperty<ObservableList<String>>> LPropertyMap;
	AtomicBoolean selectedFeatureBool;
	String selectedFeature,algoGraph;
	XYChart.Series<Number, Number> goodPoints;
	XYChart.Series<Number, Number> badPoints;
	XYChart.Series<Number, Number> line;
	XYChart.Series<Number,Number> Feature1;
	XYChart.Series<Number,Number> Feature2;
	XYChart.Series welzlCircle;
	XYChart.Series badWelzl;
	XYChart.Series goodWelzl;

	public ViewModel(Model model) {
		NPropertyMap = new HashMap<>();
		SPropertyMap = new HashMap<>();
		BPropertyMap = new HashMap<>();
		LPropertyMap = new HashMap<>();
		selectedFeatureBool = new AtomicBoolean();

		this.model = model;
		this.model.addObserver(this);
		getIProperty("TimeStep").addListener((obj,oldVal,newVal)->{
				model.setTimeStep(getIProperty("TimeStep").get());
		});
		getBProperty("FlightGear").addListener((obj,oldVal,newVal)->model.setIsSimulated(getBProperty("FlightGear").get()));
		getIProperty("Tabs").addListener((obj,oldVal,newVal)->model.setCurrentTab(getIProperty("Tabs").get()));
		getSProperty("SelectedFlightToDisplay").addListener((obj,oldVal,newVal)->model.setSelectedFlightToDisplay(getSProperty("SelectedFlightToDisplay").get()));
		StringProperty temp = getSProperty("PlaySpeed");
		temp.addListener((obd,oldVal,newVal)->{
			double val = 1;
			try {
				val = Double.parseDouble(temp.get());
			}catch(NumberFormatException e) {
				model.setAlert("Invalid play speed input.");
			}
			if(val > 100) {
				model.setAlert("Invalid play speed, Limit: 100.");
				val=100;
			}
			model.setPlaySpeed(val);
		});
		
	}

	public boolean getSelectedFeatureBool() {
		return selectedFeatureBool.get();
	}

	public void setSelectedFeatureBool(boolean selectedFeatureBool) {
		this.selectedFeatureBool.set(selectedFeatureBool);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == model) {
			if (arg.equals("AlgoFile")) { // New algo file has been added
				Platform.runLater(()->{
					getList("AlgoFiles").set(this.model.getAlgoList());
				});
			}
			else if(arg.equals("TrainFile")) { //New train file has been selected
				Platform.runLater(()->{
					System.out.println("idan" +this.model.getTrainFileName());
					getSProperty("TrainFileName").set(this.model.getTrainFileName());
					getList("Features").set(this.model.getFeatureList());
					System.out.println(getList("Features").asString());
					System.out.println("Done");
				});
			}
			
			else if(arg.equals("TestFile")) { // New test file has been selected
				Platform.runLater(()->{
					getSProperty("TestFileName").set(this.model.getTestFileName());
					this.model.getFeatureList();
					
				});
			}
			else if(arg.equals("TimeStep")) { // TimeStep has been changed.
				Platform.runLater(()->{
					getIProperty("TimeStep").set(this.model.getTimeStep());
					getDProperty("Elevator").set(this.model.getElevatorVal());
					getDProperty("Aileron").set(this.model.getAileronVal());
					getDProperty("Rudder").set(this.model.getRudderVal());
					getDProperty("Throttle").set(this.model.getThrottleVal());
					getDProperty("FlightHeight").set(this.model.getFlightHeightVal());
					getDProperty("FlightSpeed").set(this.model.getFlightSpeedVal());
					getDProperty("Roll").set(this.model.getRollVal());
					getDProperty("Pitch").set(this.model.getPitchVal());
					getDProperty("Yaw").set(this.model.getYawVal());
					getSProperty("MaxTimeStep").set((this.model.calculateMaxTimeStep()));
					getSProperty("CurTimeStep").set(model.calculateTime(model.getTimeStep()/model.getSampleRate()));
					
					if(selectedFeatureBool.get()) {
						if(!model.isAnomaly())
							if(!algoGraph.equals("W"))
								goodPoints.getData().add(model.getPoint());
							else
								goodWelzl.getData().add(model.getWelslPoint());
						else
							if(!algoGraph.equals("W"))
								badPoints.getData().add(model.getPoint());
							else
								goodWelzl.getData().add(model.getWelslPoint());
						if(!algoGraph.equals("Z")) {
							Feature1.getData().add(model.getFeaturePoint(1));
							Feature2.getData().add(model.getFeaturePoint(2));
						}
					}
				});
			}
			else if(arg.equals("PlaySpeed")) { // PlaySpeed has been changed.
					Platform.runLater(()->{
						getSProperty("PlaySpeed").set(""+this.model.getPlaySpeed());
					});
			}
						
			else if(arg.equals("Settings")) {
				Platform.runLater(()->{
					getSProperty("LoadedSettings").set(this.model.getLastSettingsUsed());
				});
			}
			
			else if(arg.equals("MaxTime")) {
				Platform.runLater(()->{
					getSProperty("CurTimeStep").set(model.calculateTime(model.getTimeStep()));
					getSProperty("MaxTimeStep").set(model.calculateMaxTimeStep());
					getIProperty("MaxTime").set(this.model.getMaxTime());
				});
			}
			
			else if(arg.equals("newGraphLR")) {
				Platform.runLater(()->{
				 clearGraph();
				 goodPoints.nameProperty().set("Normal");
				 badPoints.nameProperty().set("Anomaly");
				 getBProperty("LCVis").set(true);
				 getBProperty("BCVis").set(false);
				 getBProperty("F1Vis").set(true);
				 getBProperty("F2Vis").set(true);
				 goodPoints.getData().addAll(model.getGoodPoints().getData());
				 badPoints.getData().addAll(model.getBadPoints().getData()); 
				 line.getData().addAll(model.getLine().getData());
				 Feature1.getData().addAll(model.getFeatureData(1).getData());
				 Feature2.getData().addAll(model.getFeatureData(2).getData());
				 algoGraph = "LR";
				 selectedFeatureBool.set(true);
				});
			}
			
			else if(arg.equals("newGraphZ")) {
				Platform.runLater(()->{
					 getBProperty("LCVis").set(true);
					 getBProperty("BCVis").set(false);
					 getBProperty("F1Vis").set(false);
					 getBProperty("F2Vis").set(false);
					 clearGraph();
					 goodPoints.getData().addAll(model.getGoodPoints().getData());
					 badPoints.getData().addAll(model.getBadPoints().getData()); 
					 line.getData().addAll(model.getLine().getData());	
					 algoGraph = "Z";
					 selectedFeatureBool.set(true);
				});
			}
			
			else if(arg.equals("newGraphW")) {
				Platform.runLater(()->{
					 getBProperty("LCVis").set(false);
					 getBProperty("BCVis").set(true);
					 getBProperty("F1Vis").set(true);
					 getBProperty("F2Vis").set(true);
					 goodWelzl.getData().addAll(model.getGoodWelzel().getData());
					 badWelzl.getData().addAll(model.getBadWelzl().getData()); 
					 welzlCircle.getData().addAll(model.getWelzlCircle().getData());
					 algoGraph = "W";
					 selectedFeatureBool.set(true);
				});
			}
			
			else if(arg.equals("Alert")) {
				Platform.runLater(()->{					
					getSProperty("Alert").set(model.getAlert());
					getSProperty("Alert").set("");
				});
			}
			
			else if(arg.equals("pleaseClear")) {
				Platform.runLater(()->{					
					goodPoints.getData().clear();
					badPoints.getData().clear();
					goodPoints.getData().addAll(model.getGoodPoints().getData());
					badPoints.getData().addAll(model.getBadPoints().getData()); 
				});
			}
			
			else if(arg.equals("NoGraph!")) {
				Platform.runLater(()->{					
					clearGraph();
					selectedFeatureBool.set(false);
				});
			}
		}
	}
	
	private void clearGraph() {
		 goodPoints.getData().clear();
		 badPoints.getData().clear();
		 line.getData().clear();
		 Feature1.getData().clear();
		 Feature2.getData().clear();
		 welzlCircle.getData().clear();
		 goodWelzl.getData().clear();
		 badWelzl.getData().clear();
	}
	
	public BooleanProperty getBProperty(String property) {
		if(!BPropertyMap.containsKey(property))
			BPropertyMap.put(property, new SimpleBooleanProperty());
		return  BPropertyMap.get(property);
	}
	
	public IntegerProperty getIProperty(String property) {
		if(!NPropertyMap.containsKey(property))
			NPropertyMap.put(property, new SimpleIntegerProperty());
		return (IntegerProperty) NPropertyMap.get(property);
	}
	
	public DoubleProperty getDProperty(String property) {
		if(!NPropertyMap.containsKey(property))
			NPropertyMap.put(property, new SimpleDoubleProperty());
		return (DoubleProperty)NPropertyMap.get(property);
	}
	
	public StringProperty getSProperty(String property) {
		if(!SPropertyMap.containsKey(property))
			SPropertyMap.put(property, new SimpleStringProperty());
		return SPropertyMap.get(property);
	}
	
	public ObjectProperty<ObservableList<String>> getList(String property){
		if(!LPropertyMap.containsKey(property))
			LPropertyMap.put(property, new SimpleObjectProperty<ObservableList<String>>());
		return LPropertyMap.get(property);
		
	}

	public void initialize() {
		getList("SettingsFileList").set(this.model.getFileSettingsObsList());;
		getList("AlgoFiles").set(this.model.getAlgoList());
		getSProperty("LastSettings").set(this.model.getLastSettingsUsed());
		getSProperty("PlaySpeed").set(""+this.model.getPlaySpeed());
		getSProperty("SampleRate").set(""+model.getSampleRate());
		getSProperty("SelectedFeature").set("");
		getSProperty("AlgoLabel").set("");
		goodPoints = new XYChart.Series<Number,Number>();
		badPoints = new XYChart.Series<Number,Number>();
		line = new XYChart.Series<Number,Number>();
		Feature1 = new XYChart.Series<Number,Number>();
		Feature2 = new XYChart.Series<Number,Number>();
		welzlCircle = new XYChart.Series<>();
		goodWelzl = new XYChart.Series<>();
		badWelzl = new XYChart.Series<>();
	}
	
	public void play() {
		model.setRewindFlag(false);
		if(!model.getPlayFlag())
			model.play();
		}
	
	public void pause() {
		model.pause();
	}

	public void stop() {
		model.stop();
	}
	
	public void fastForward() {
		model.fastForward();
	}

	public void superFastForward() {
		model.superFastForward();
	}

	public void rewind() {
		model.rewind();
	}

	public void fastRewind() {
		model.fastRewind();
	}

	public void newSettingsFile() {
		model.newSettingsFile();
		getList("SettingsFileList").set(this.model.getFileSettingsObsList());;
	}
	
	public void openCSVFile() {
		model.newCSVFile();
		getList("Features").set(this.model.getFeatureList());
	}
	
	public ObservableList<String> getSavedSettingFileNames() {
		return model.getFileSettingsObsList();
	}

	public void selectSettings(String choice) {
		model.loadSettings(choice);
		getSProperty("LastSettings").set(choice);
	}

	public void deleteSettingsFile(String fileName) {
		model.deleteSettingsFile(fileName);
		getList("SettingsFileList").set(this.model.getFileSettingsObsList());;
	}
	

	public void deleteAlgo(String algoName) {
		model.deleteAlgoFile(algoName);
		getList("AlgoFiles").set(this.model.getAlgoList());
	}

	public ObservableList<String> getFeatureList() {
		return model.getFeatureList();
	}

	public void featureSelected(String selectedFeature) {
		if ( !getSProperty("AlgoLabel").get().equals("")) {
			getSProperty("SelectedFeature").set(selectedFeature);
			model.setSelectedFeature(selectedFeature);			
		}
		else
			model.setAlert("Please choose an algorithm.");
	}
	
	public ObservableList<String> getAlgorithms() {
		return model.getAlgoList();
	}

	public void algoSelected(String selectedItem) {
		clearGraph();
		selectedFeatureBool.set(false);
		model.setSelectedAlgorithm(selectedItem);
		getSProperty("AlgoLabel").set(selectedItem);
	}

	public void newAlgoFile() {
		model.newAlgoFile();
		getList("AlgoFiles").set(this.model.getAlgoList());
	}

	public void uploadTestFile() {
		model.uploadTestFile();
	}

	public float getMinValue(String selectedItem) {
		return model.getMinVal(selectedItem);
	}

	public float getMaxValue(String selectedItem) {
		return model.getMaxVal(selectedItem);
	}

	public Series<Number, Number> getBadPoints() {
		return badPoints;
	}

	public Series<Number, Number> getFeature1() {
		return Feature1;
	}
	public Series<Number, Number> getFeature2() {
		return Feature2;
	}
	public Series<Number, Number> getGoodPoints() {
		return goodPoints;
	}

	public Series<Number, Number> getLine() {
		return line;
	}

	public Series getWelzlCircle() {
		return welzlCircle;
	}

	public Series getBadWelzl() {
		return badWelzl;
	}

	public Series getGoodWelzl() {
		return goodWelzl;
	}
}
