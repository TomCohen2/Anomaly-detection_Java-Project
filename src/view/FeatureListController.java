package view;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class FeatureListController {
	@FXML
	ListView<String> FeaturesList;
	
	Runnable featSelected;
	
	public void featureSelected() {
		if (featSelected!=null)
			featSelected.run();
	}
	
	public void addAll(List<String> featureList) {
		FeaturesList.getItems().addAll(featureList);
	}
	
	
}
