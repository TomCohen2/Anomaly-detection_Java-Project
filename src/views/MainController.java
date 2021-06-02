package views;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import view_model.ViewModel;

public class MainController {
	
	@FXML Pannel pannel;
	@FXML Label alt;
	
	public MainController() {
		
	}
	
	public void init() {
		ViewModel vm = new ViewModel();//can be sent from the main
		
		alt.textProperty().bind(vm.getProperty("alt").asString());
		// bind other properties as well...
		
		pannel.controller.onPlay = vm.play;
		pannel.controller.onPause = vm.pause;
		pannel.controller.onStop = vm.stop;
	}
	
}
