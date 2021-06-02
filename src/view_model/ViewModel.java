package view_model;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import model.Model;

public class ViewModel {
	
	public IntegerProperty timeStep;
	public final Runnable play,pause,stop; 
	private HashMap<String, DoubleProperty> displayVariables;
	
	public ViewModel() {		
		timeStep = new SimpleIntegerProperty(0);
		displayVariables = new HashMap<String, DoubleProperty>();
		
		Model m = new Model(timeStep);
		
		// read from file clock from fxml
		
		displayVariables.put("alt", new SimpleDoubleProperty());
		timeStep.addListener((obs,old,nw)->{
			//get data of display variables at time step (nw)
			Platform.runLater(()->displayVariables.get("alt").set(nw.doubleValue()));
		});
		
		play=()->m.play();
		pause=()->m.pause();
		stop=()->m.stop();
	}
	
	public DoubleProperty getProperty(String name) {
		return displayVariables.get(name);
	}
	
}
