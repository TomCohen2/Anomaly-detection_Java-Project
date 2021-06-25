package view;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;


public class JoystickController {
		@FXML
		Canvas canvas;
		@FXML
		Slider ThrottleSlider;
		@FXML
		Slider RudderSlider;
		
		Runnable paintJoystick;
		
		
		
		Label jxAileronVal;
		@FXML
		Label jyElevatorVal;
		double jx,jy;
		
		
		void paint() {
			if (paintJoystick!=null)
				paintJoystick.run();
		}
		
		void bindThrottleSlider(DoubleProperty throttle) {
			ThrottleSlider.valueProperty().bind(throttle);
		}
		
		void bindRudderSlider(DoubleProperty rudder) {
			RudderSlider.valueProperty().bind(rudder);
		}
		
		public void bindJxAileron(DoubleProperty aileron) {
			jxAileronVal.textProperty().bind(aileron.asString());
		}
		
		public void bindJyElevator(DoubleProperty elevator) {
			jyElevatorVal.textProperty().bind(elevator.asString());
		}
}

