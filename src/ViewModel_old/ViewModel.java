package ViewModel_old;

import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import test.Model;

public class ViewModel implements Observer{
	Model model;
	IntegerProperty VM_timeStep;
	
	ViewModel(Model model){
		VM_timeStep = new SimpleIntegerProperty();
		this.model = model;
		this.VM_timeStep.addListener((obj,oldVal,newVal)->model.setTimeStep(this.VM_timeStep.get()));
		this.model.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == model)
			Platform.runLater(()->{
				VM_timeStep.set(this.model.getTimeStep());	
			});
		
	}
}
