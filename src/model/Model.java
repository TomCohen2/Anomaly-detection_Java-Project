package model;

import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.property.IntegerProperty;

public class Model {
	
	Timer t = null;
	IntegerProperty timeStep;
	
	
	public Model(IntegerProperty timeStep) {
		this.timeStep=timeStep;
	}
	
	public void play() {
		if(t==null) {
			t = new Timer();
			t.scheduleAtFixedRate(new TimerTask() {
				
				@Override
				public void run() {
					System.out.println("sending row " + timeStep.get());
					timeStep.set(timeStep.get() + 1);
				}
			}, 0, 1000);
		}
	}
	
	public void pause() {
		t.cancel();
		t = null;
	}
	
	public void stop() {
		t.cancel();
		t = null;
		timeStep.set(0);
	}
	
	
}
