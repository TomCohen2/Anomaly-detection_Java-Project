package view;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


public class SettingsController {
	@FXML
	ListView<String> SettingsFilesListView;
	@FXML
	Label LoadedSettings;
	@FXML
	CheckBox FlightGear;
	
	Runnable onDelete, onLoadSettings, onNewSettings, onCSVFile;
	
	public void delete() {
		if (onDelete!=null)
			onDelete.run();
	}
	
	public void LoadSettings() {
		if (onLoadSettings!=null)
			onLoadSettings.run();
	}
	
	public void newSettings() {
		if (onNewSettings!=null)
			onNewSettings.run();
	}
	
	public void openCSVFile() {
		if (onCSVFile!=null)
			onCSVFile.run();
	}
	
	void bindLoadedSettings(StringProperty settings) {
		LoadedSettings.textProperty().bind(settings);
	}
	
	void addToSettingsList(List<String> settings) {
		SettingsFilesListView.getItems().addAll(settings);
	}
		
	void bindFlightGearBoolean(BooleanProperty FG) {
		FG.bindBidirectional(FlightGear.selectedProperty());
	}

}
