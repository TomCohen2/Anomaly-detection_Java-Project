package view;

import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class AlgorithmsController {
	@FXML
	ListView<String> AlgoFilesListView;
	@FXML
	Label AlgorLabel;
	@FXML
	Label TrainFileName;
	@FXML
	Label TestFileName;
	
	public Runnable onDelete, onSelectAlgo, onNewAlgo, onUpTrainCSV, onUpTestCSV;
	
	public void delete() {
		if(onDelete!=null){
			onDelete.run();
		}
	}
	
	public void algoSelect() {
		if(onSelectAlgo!=null){
			onSelectAlgo.run();
		}
	}
	
	public void newAlgo() {
		if(onNewAlgo!=null){
			onNewAlgo.run();
			AlgoFilesListView.refresh();
		}
	}
	
	public void uploadTrainCSV() {
		if(onUpTrainCSV!=null){
			onUpTrainCSV.run();
		}
	}
	
	public void uploadTestCSV() {
		if(onUpTestCSV!=null){
			onUpTestCSV.run();
		}
	}
	
	public void bindAlgorithms(StringProperty Algo) {
		AlgorLabel.textProperty().bind(Algo);
	}
	
	public void bindTrainFileName(StringProperty TrainFN) {
		TrainFileName.textProperty().bind(TrainFN);		
	}

	public void bindTestFileName(StringProperty TestFN) {
		TestFileName.textProperty().bind(TestFN);
	}
	public void addToAlgoList(List<String> settings) {
		AlgoFilesListView.getItems().addAll(settings);
	}

	public String getSelectedAlgo() {
		//System.out.println(AlgoFilesListView.getSelectionModel().getSelectedItem());
		return AlgoFilesListView.getSelectionModel().getSelectedItem();
	}
}

