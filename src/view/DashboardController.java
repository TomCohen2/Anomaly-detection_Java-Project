package view;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
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
	
	void bindAileronVal(DoubleProperty aileron) {
		AileronVal.textProperty().bind(aileron.asString());
	}
	void bindElevatorVal(DoubleProperty elevators) {
		ElevatorVal.textProperty().bind(elevators.asString());
	}
	void bindRudderVal(DoubleProperty rudder) {
		RudderVal.textProperty().bind(rudder.asString());
	}
	void bindThrottleVal(DoubleProperty throttle) {
		ThrottleVal.textProperty().bind(throttle.asString());
	}
	void bindFlightHeightVal(DoubleProperty flightHeight) {
		FlightHeightVal.textProperty().bind(flightHeight.asString());
	}
	void bindFlightSpeedVal(DoubleProperty flightSpeed) {
		FlightSpeedVal.textProperty().bind(flightSpeed.asString());
	}
	void bindRollVal(DoubleProperty roll) {
		RollVal.textProperty().bind(roll.asString());
	}
	void bindPitchVal(DoubleProperty pitch) {
		PitchVal.textProperty().bind(pitch.asString());
	}
	void bindYawVal(DoubleProperty yaw) {
		YawVal.textProperty().bind(yaw.asString());
	}
	
	
}
