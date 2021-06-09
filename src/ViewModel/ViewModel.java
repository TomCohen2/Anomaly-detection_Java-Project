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
	//
//	DoubleProperty VM_AileronVal;
//	DoubleProperty VM_ElevatorVal;
//	DoubleProperty VM_RudderVal;
//	DoubleProperty VM_ThrottleVal;
//	DoubleProperty VM_FlightHeightVal;
//	DoubleProperty VM_FlightSpeedVal;
//	DoubleProperty VM_RollVal;
//	DoubleProperty VM_PitchVal;
//	DoubleProperty VM_YawVal;
	
//	StringProperty VM_MaxTimeStep;
//	StringProperty VM_LoadedSettings;
//	StringProperty VM_AlgorithmSelected;
//	StringProperty VM_TrainFileName;
//	StringProperty VM_TestFileName;
//	IntegerProperty VM_timeStep;
//	IntegerProperty VM_CurTimeStep;
//	StringProperty VM_StringTimeStep;
//	BooleanProperty VM_isSimulated;
//	IntegerProperty VM_DISP_Feature1;
//	IntegerProperty VM_DISP_Feature2;
//	ObjectProperty<ObservableList<String>> VM_SettingsFilesList;
//	ObjectProperty<ObservableList<String>> VM_FeaturesList;
//	ObjectProperty<ObservableList<String>> VM_AlgoFiles;
//	IntegerProperty VM_TabPane;
//	StringProperty VM_PlaySpeed;
	 
	public ViewModel(Model model) {
		NPropertyMap = new HashMap<>();
		SPropertyMap = new HashMap<>();
		BPropertyMap = new HashMap<>();
		LPropertyMap = new HashMap<>();
		selectedFeatureBool = new AtomicBoolean();
//		VM_timeStep = new SimpleIntegerProperty();
//		VM_CurTimeStep = new SimpleIntegerProperty();
//		VM_StringTimeStep = new SimpleStringProperty();
//		VM_MaxTimeStep = new SimpleStringProperty();
//		VM_ElevatorVal = new SimpleDoubleProperty();
//		VM_AileronVal = new SimpleDoubleProperty();
//		VM_RudderVal = new SimpleDoubleProperty();
//		VM_ThrottleVal = new SimpleDoubleProperty();
//		VM_FlightHeightVal = new SimpleDoubleProperty();
//		VM_FlightSpeedVal = new SimpleDoubleProperty();
//		VM_RollVal = new SimpleDoubleProperty();
//		VM_PitchVal = new SimpleDoubleProperty();
//		VM_YawVal = new SimpleDoubleProperty();
//		VM_isSimulated = new SimpleBooleanProperty();
//		VM_SettingsFilesList = new SimpleObjectProperty<ObservableList<String>>();
//		VM_FeaturesList = new SimpleObjectProperty<ObservableList<String>>();
//		VM_AlgoFiles = new SimpleObjectProperty<ObservableList<String>>();
//		//Graphs
//		VM_DISP_Feature1 = new SimpleIntegerProperty();
//		VM_DISP_Feature2 = new SimpleIntegerProperty();
//		VM_LoadedSettings = new SimpleStringProperty();
//		VM_AlgorithmSelected = new SimpleStringProperty();
//		VM_TrainFileName = new SimpleStringProperty();
//		VM_TestFileName = new SimpleStringProperty();
//		VM_TabPane = new SimpleIntegerProperty();
//		VM_PlaySpeed = new SimpleStringProperty();
//		
		this.model = model;
		this.model.addObserver(this);
		getIProperty("TimeStep").addListener((obj,oldVal,newVal)->model.setTimeStep(getIProperty("TimeStep").get()));
		//this.VM_timeStep.addListener((obj,oldVal,newVal)->VM_CurTimeStep.setValue(VM_timeStep.get()));
		//this.VM_CurTimeStep.addListener((obj,oldVal,newVal)->model.setTimeStep((int)this.VM_CurTimeStep.get()));
		getBProperty("Simulator").addListener((obj,oldVal,newVal)->model.setIsSimulated(getBProperty("Simulator").get()));
		getIProperty("Tabs").addListener((obj,oldVal,newVal)->model.setCurrentTab(getIProperty("Tabs").get()));
		
		StringProperty temp = getSProperty("PlaySpeed");
		temp.addListener((obd,oldVal,newVal)->{
		if(!temp.get().equals("") && !temp.get().equals("0") && !temp.get().equals("0.0") && !temp.get().equals("0."))// && temp.get().matches("\\d+"))
			model.setPlaySpeed(Double.parseDouble(temp.get()));
		else
			model.setPlaySpeed(1.0); // awful patch.
		});
	}

	public boolean getSelectedFeatureBool() {
		return selectedFeatureBool.get();
	}

	public void setSelectedFeatureBool(boolean selectedFeatureBool) {
		this.selectedFeatureBool.set(selectedFeatureBool);;
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
					getSProperty("TrainFileName").set(this.model.getTrainFileName());
					getList("Features").set(this.model.getFeatureList());
					getSProperty("CurTimeStep").set(model.calculateTime(model.getTimeStep()));
					getSProperty("MaxTimeStep").set(model.calculateMaxTimeStep());
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
					getDProperty("R").set(this.model.getRollVal());
					getDProperty("Pitch").set(this.model.getPitchVal());
					getDProperty("Yaw").set(this.model.getYawVal());
					getSProperty("MaxTimeStep").set((this.model.calculateMaxTimeStep()));
					//getSProperty("CurTimeStepString").set((this.model.calculateTime(this.model.getTimeStep()/this.model.getSampleRate())));
					getSProperty("CurTimeStep").set(model.calculateTime(model.getTimeStep()/model.getSampleRate()));
					if(selectedFeatureBool.get()) {
						model.displayGraphsCall(getSProperty("SelectedFeature").get());
					}
				});
			}
			else if(arg.equals("PlaySpeed")) { // PlaySpeed has been changed.
					Platform.runLater(()->{
						getSProperty("PlaySpeed").set(""+this.model.getPlaySpeed());
					});
				}
						
			}
			else if(arg.equals("Settings")) {
				Platform.runLater(()->{
					getSProperty("LoadedSettings").set(this.model.getLastSettingsUsed());
				});
			}
			
			//	VM_SettingsFilesList.set(this.model.getFileSettingsObsList());
			//	VM_FeaturesList.set(this.model.getFeatureList());
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
//	
//	
//	public void bindBooleanProperty() {
//		
//	}
//	
//	public void bindDoubleProperty() {
//		
//	}
//	
//	public void bindAileron(StringProperty textProperty) {
//		textProperty.bind(VM_AileronVal.asString());
//		
//	}
//
//	public void bindElevator(StringProperty textProperty) {
//		textProperty.bind(this.VM_ElevatorVal.asString());
//		
//	}
//
//	public void bindRudder(StringProperty textProperty) {
//		textProperty.bind(this.VM_RudderVal.asString());
//		
//	}
//	
//	public void bindRudder(DoubleProperty valueProperty) {
//		valueProperty.bind(this.VM_RudderVal);		
//	}
//
//	public void bindThrottle(StringProperty textProperty) {
//		textProperty.bind(this.VM_ThrottleVal.asString());
//		
//	}
//
//	public void bindThrottle(DoubleProperty valueProperty) {
//		valueProperty.bind(this.VM_ThrottleVal);
//	}
//
//	public void bindFlightHeight(StringProperty textProperty) {
//		textProperty.bind(this.VM_FlightHeightVal.asString());
//		
//	}
//
//	public void bindFlightSpeed(StringProperty textProperty) {
//		textProperty.bind(this.VM_FlightSpeedVal.asString());
//		
//	}
//
//	public void bindRoll(StringProperty textProperty) {
//		textProperty.bind(this.VM_RollVal.asString());
//		
//	}
//
//	public void bindPitch(StringProperty textProperty) {
//		textProperty.bind(this.VM_PitchVal.asString());
//		
//	}
//
//	public void bindYaw(StringProperty textProperty) {
//		textProperty.bind(this.VM_YawVal.asString());
//		
//	}

	public void initialize() {
		getList("SettingsFileList").set(this.model.getFileSettingsObsList());;
		getList("AlgoFiles").set(this.model.getAlgoList());
		getSProperty("LastSettings").set(this.model.getLastSettingsUsed());
		getSProperty("PlaySpeed").set(""+this.model.getPlaySpeed());
	}
	
	public void play() {
//		System.out.println("Play from ViewModel!");
//		if(!model.getPlayFlag()) {
//		model.setPlayFlag(true);
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
	
//	public void bindMaxTimeStep(StringProperty textProperty) {
//		textProperty.bind(this.VM_MaxTimeStep);
//		
//	}
//
//	public void bindCurTimeStep(StringProperty textProperty) {
//		textProperty.bind(VM_StringTimeStep);
//		
//	}
//	public void bindLoadedSettings(StringProperty textProperty) {
//		textProperty.bind(VM_LoadedSettings);
//		
//	}
//
//	public void bindAlgorithmSelected(StringProperty textProperty) {
//		textProperty.bind(VM_AlgorithmSelected);
//		
//	}
//
//	public void bindTrainFileName(StringProperty textProperty) {
//		textProperty.bind(VM_TrainFileName);
//	}
//
//	public void bindTestFileName(StringProperty textProperty) {
//		textProperty.bind(VM_TestFileName);
//	}
//
//	public void bindTimeSlideBar(DoubleProperty valueProperty) {
//		this.VM_CurTimeStep.bindBidirectional(valueProperty);
//		
//	}
//
//	public void bindSimulatorCB(BooleanProperty b) {
//		this.VM_isSimulated.bind(b);
//		
//	}

	public int getMaxTimeStep() {
		//return model.getMaxLines();
		return 1000;
	}

	public void newSettingsFile() {
		model.newSettingsFile();
		
	}
	
	public void openCSVFile() {
		model.newCSVFile();
		getList("Features").set(this.model.getFeatureList());
		this.model.getFeatureList().forEach((a)->System.out.println(a));
		//getList("Feature")
	}

//	public void bindFileSettingsListView(ObjectProperty<ObservableList<String>> itemsProperty) {
//		itemsProperty.bind(this.VM_SettingsFilesList);
//		
//	}
//	
//
//	public void bindAlgoFilesListView(ObjectProperty<ObservableList<String>> itemsProperty) {
//		itemsProperty.bind(this.VM_AlgoFiles);
//	}
//
//	public void bindFeaturesListView(ObjectProperty<ObservableList<String>> itemsProperty) {
//		itemsProperty.bind(this.VM_FeaturesList);
//	}
	
	public ObservableList<String> getSavedSettingFileNames() {
		
		return model.getFileSettingsObsList();
	}

	public void selectSettings(String choice) {
		model.loadSettings(choice);
		getSProperty("LastSettings").set(choice);
		
	}

	public void deleteSettingsFile(String fileName) {
		model.deleteSettingsFile(fileName);
	}
	

	public void deleteAlgo(String algoName) {
		model.deleteAlgoFile(algoName);
	}

	public ObservableList<String> getFeatureList() {
		return model.getFeatureList();
	}

	public TimeSeriesAnomalyDetector.GraphStruct featureSelected(String selectedFeature) {
		selectedFeatureBool.set(true);
		getSProperty("SelectedFeature").set(selectedFeature);;
		return model.displayGraphsCall(selectedFeature);
		
	}

	public ObservableList<String> getAlgorithms() {
		return model.getAlgoList();
	}

	public void algoSelected(String selectedItem) {
		model.setSelectedAlgorithm(selectedItem);
		getSProperty("AlgoLabel").set(selectedItem);
	}

	public void newAlgoFile() {
		model.newAlgoFile();
	}

	public void uploadTestFile() {
		model.uploadTestFile();
		
	}
	

//	public void bindTabPane(ReadOnlyIntegerProperty selectedIndexProperty) {
//		VM_TabPane.bind(selectedIndexProperty);	
//	}

//	public void bindPlaySpeed(StringProperty textProperty) {
//		textProperty.bindBidirectional(this.VM_PlaySpeed);
//		
//	}

	public float getMinValue(String selectedItem) {
		return model.getMinVal(selectedItem);
	}

	public float getMaxValue(String selectedItem) {
		return model.getMaxVal(selectedItem);
	}












}
