package view;

import java.io.File;
import java.util.Observable;

import ViewModel.ViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;

public class LabelsAndValsController extends Observable{
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
	@FXML
	Label maxTimeStep;
	@FXML
	Label CurTimeStep;
	@FXML
	Slider timeStepSlider;
	@FXML
	CheckBox Simulator;
	@FXML
	Button NewSettingsFile;
	@FXML
	Button LoadSettingsFile;
	@FXML
	Button UploadCSVFile;
	@FXML
	ListView<String> SettingsFilesListView;
	
	ViewModel viewModel;
	
	public void setViewModel(ViewModel viewModel) {
		this.viewModel = viewModel;
;

		viewModel.bindAileron(AileronVal.textProperty());
		viewModel.bindElevator(ElevatorVal.textProperty());
		viewModel.bindRudder(RudderVal.textProperty());
		viewModel.bindThrottle(ThrottleVal.textProperty());
		viewModel.bindFlightHeight(FlightHeightVal.textProperty());
		viewModel.bindFlightSpeed(FlightSpeedVal.textProperty());
		viewModel.bindRoll(RollVal.textProperty());
		viewModel.bindPitch(PitchVal.textProperty());
		viewModel.bindYaw(YawVal.textProperty());
		viewModel.bindMaxTimeStep(maxTimeStep.textProperty());
		viewModel.bindCurTimeStep(CurTimeStep.textProperty());
		viewModel.bindTimeSlideBar(timeStepSlider.valueProperty());
		viewModel.bindSimulatorCB(Simulator.selectedProperty());
		
		//ListView
		SettingsFilesListView.setOrientation(Orientation.VERTICAL);
		//SettingsFilesListView.setPrefSize(120, 100);
		SettingsFilesListView.getItems().setAll(viewModel.getSavedSettingFileNames());
//		SettingsFilesListView.getSelectionModel().selectedItemProperty().addListener((obj, oldVal, newVal)->{
//			SelectedFeature.setText(listView.getSelectionModel().getSelectedItem());
			System.out.println("You have selected " + SettingsFilesListView.getSelectionModel().getSelectedItem());
		SettingsFilesListView.refresh();
		viewModel.bindFileSettingsListView(SettingsFilesListView.itemsProperty());
		timeStepSlider.setMax(2743);
		//timeStepSlider.setMax(viewModel.getMaxTimeStep()-1); Future??
		timeStepSlider.setMin(0);
	}
	
	public void play() {
		viewModel.play();
		System.out.println("Play from View!");
	}
	
	public void pause() {
		viewModel.pause();
	}
	
	public void newSettingsFile() {
		viewModel.newSettingsFile();
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
}
