package view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class PlayerController {
	@FXML
	Label maxTimeStep;
	@FXML
	Label curTimeStep;
	@FXML
	Slider timeStepSlider;
	@FXML
	TextField playSpeed;
	@FXML
	ComboBox<String> flightSelection;
	
	public Runnable onPlay, onPause, onStop, onFastForward, onFFastForawrd, onRewind, onFRewind;
	
	public void play() {
		if(onPlay!=null){
			onPlay.run();
		}
	}
	
	public void pause() {
		if(onPause!=null){
			onPause.run();
		}
	}
	
	public void stop() {
		if(onStop!=null){
			onStop.run();
		}
	}
	
	public void forward() {
		if(onFastForward!=null){
			onFastForward.run();
		}
	}
	
	public void fForward() {
		if(onFFastForawrd!=null){
			onFFastForawrd.run();
		}
	}
	
	public void rewind() {
		if(onRewind!=null){
			onRewind.run();
		}
	}
	
	public void fRewind() {
		if(onFRewind!=null){
			onFRewind.run();
		}
	}
	
	public void bindMaxTimestep(StringProperty maxTS) {
		maxTimeStep.textProperty().bind(maxTS);
	}
	
	public void bindCurTimeStep(StringProperty curTS) {
		curTimeStep.textProperty().bind(curTS);		
	}
	
	public void bindPlaySpeed(StringProperty pSpeed) {
		playSpeed.textProperty().bindBidirectional(pSpeed);
	}
	
	public void bindTimeStepSlider(IntegerProperty timeStep) {
		timeStepSlider.valueProperty().bindBidirectional(timeStep);;
		
	}
	
	public void bindTimeStepSliderChange(BooleanProperty bool) {
		bool.bind(timeStepSlider.valueChangingProperty());
	}
	
	public void bindFlightSelected(StringProperty selectedFlight) {
		selectedFlight.bind(flightSelection.valueProperty());
	}

	public void bindTimeSliderMax(IntegerProperty iProperty) {
		timeStepSlider.maxProperty().bind(iProperty);
		//timeStepSlider.setMax(iProperty.get());
		
	}

	public void bindUserTimeStepSlider(IntegerProperty iProperty) {
		iProperty.bind(timeStepSlider.valueProperty());
		
	}
}
