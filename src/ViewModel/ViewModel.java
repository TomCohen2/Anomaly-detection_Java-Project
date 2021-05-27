package ViewModel;

import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
//import PTM2Proect.src.test.Model;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import test.Model;

public class ViewModel implements Observer{
	Model model;
	DoubleProperty VM_AileronVal;
	DoubleProperty VM_ElevatorVal;
	DoubleProperty VM_RudderVal;
	DoubleProperty VM_ThrottleVal;
	DoubleProperty VM_FlightHeightVal;
	DoubleProperty VM_FlightSpeedVal;
	DoubleProperty VM_RollVal;
	DoubleProperty VM_PitchVal;
	DoubleProperty VM_YawVal;
	StringProperty VM_MaxTimeStep;
	IntegerProperty VM_timeStep;
	IntegerProperty VM_CurTimeStep;
	StringProperty VM_StringTimeStep;
	BooleanProperty VM_isSimulated;
	ObjectProperty<ObservableList<String>> VM_SettingsFilesList;
	               
	public ViewModel(Model model) {
		VM_timeStep = new SimpleIntegerProperty();
		VM_CurTimeStep = new SimpleIntegerProperty();
		VM_StringTimeStep = new SimpleStringProperty();
		VM_MaxTimeStep = new SimpleStringProperty();
		VM_ElevatorVal = new SimpleDoubleProperty();
		VM_AileronVal = new SimpleDoubleProperty();
		VM_RudderVal = new SimpleDoubleProperty();
		VM_ThrottleVal = new SimpleDoubleProperty();
		VM_FlightHeightVal = new SimpleDoubleProperty();
		VM_FlightSpeedVal = new SimpleDoubleProperty();
		VM_RollVal = new SimpleDoubleProperty();
		VM_PitchVal = new SimpleDoubleProperty();
		VM_YawVal = new SimpleDoubleProperty();
		VM_isSimulated = new SimpleBooleanProperty();
		VM_SettingsFilesList = new SimpleObjectProperty<ObservableList<String>>();
		this.model = model;
		this.model.addObserver(this);
		this.VM_timeStep.addListener((obj,oldVal,newVal)->model.setTimeStep((int)this.VM_timeStep.get()));
		//this.VM_timeStep.addListener((obj,oldVal,newVal)->VM_CurTimeStep.setValue(VM_timeStep.get()));
		this.VM_CurTimeStep.addListener((obj,oldVal,newVal)->model.setTimeStep((int)this.VM_CurTimeStep.get()));
		this.VM_isSimulated.addListener((obj,oldVal,newVal)->model.setIsSimulated(this.VM_isSimulated.get()));
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == model) {
			Platform.runLater(()->{
				VM_ElevatorVal.set(this.model.getElevatorVal());
				VM_AileronVal.set(this.model.getAileronVal());
				VM_RudderVal.set(this.model.getRudderVal());
				VM_ThrottleVal.set(this.model.getThrottleVal());
				VM_FlightHeightVal.set(this.model.getFlightHeightVal());
				VM_FlightSpeedVal.set(this.model.getFlightSpeedVal());
				VM_RollVal.set(this.model.getRollVal());
				VM_PitchVal.set(this.model.getPitchVal());
				VM_YawVal.set(this.model.getYawVal());
				VM_MaxTimeStep.set(this.model.calculateMaxTimeStep());
				VM_StringTimeStep.set(this.model.calculateTime(this.model.getTimeStep()/this.model.getSampleRate()));
				//System.out.println(this.model.getTimeStep());
				VM_CurTimeStep.set(this.model.getTimeStep());
				VM_SettingsFilesList.set(this.model.getFileSettingsObsList());
			});
		}
		
	}

	public void bindAileron(StringProperty textProperty) {
		textProperty.bind(VM_AileronVal.asString());
		
	}

	public void bindElevator(StringProperty textProperty) {
		textProperty.bind(this.VM_ElevatorVal.asString());
		
	}

	public void bindRudder(StringProperty textProperty) {
		textProperty.bind(this.VM_RudderVal.asString());
		
	}

	public void bindThrottle(StringProperty textProperty) {
		textProperty.bind(this.VM_ThrottleVal.asString());
		
	}

	public void bindFlightHeight(StringProperty textProperty) {
		textProperty.bind(this.VM_FlightHeightVal.asString());
		
	}

	public void bindFlightSpeed(StringProperty textProperty) {
		textProperty.bind(this.VM_FlightSpeedVal.asString());
		
	}

	public void bindRoll(StringProperty textProperty) {
		textProperty.bind(this.VM_RollVal.asString());
		
	}

	public void bindPitch(StringProperty textProperty) {
		textProperty.bind(this.VM_PitchVal.asString());
		
	}

	public void bindYaw(StringProperty textProperty) {
		textProperty.bind(this.VM_YawVal.asString());
		
	}

	public void initialize() {
		VM_SettingsFilesList.set(this.model.getFileSettingsObsList());;
	}
	
	public void play() {
		System.out.println("Play from ViewModel!");
		model.play();
	}
	
	public void pause() {
		model.pause();
	}

	public void bindMaxTimeStep(StringProperty textProperty) {
		textProperty.bind(this.VM_MaxTimeStep);
		
	}

	public void bindCurTimeStep(StringProperty textProperty) {
		textProperty.bind(VM_StringTimeStep);
		
	}

	public void bindTimeSlideBar(DoubleProperty valueProperty) {
		this.VM_CurTimeStep.bindBidirectional(valueProperty);
		
	}

	public void bindSimulatorCB(BooleanProperty b) {
		this.VM_isSimulated.bind(b);
		
	}

	public int getMaxTimeStep() {
		return model.getMaxLines();
	}

	public void newSettingsFile() {
		model.newSettingsFile();
		
	}
	
	public void openCSVFile() {
		model.newCSVFile();
	}

	public void bindFileSettingsListView(ObjectProperty<ObservableList<String>> itemsProperty) {
		itemsProperty.bind(this.VM_SettingsFilesList);
		
	}

	public ObservableList<String> getSavedSettingFileNames() {
		
		return model.getFileSettingsObsList();
	}

	public void selectSettings(String choice) {
		model.loadSettings(choice);
		
	}

	public void deleteSettingsFile(String fileName) {
		model.deleteSettingsFile(fileName);
		
	}
}
