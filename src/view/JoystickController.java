package view;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;


public class JoystickController {
		@FXML
		Canvas canvas;
		@FXML
		Slider ThrottleSlider;
		@FXML
		Slider RudderSlider;
		
		Runnable paintJoystick;
		
		void paint() {
			if (paintJoystick!=null) {
				paintJoystick.run();
				//paintJoystick.run();
			}
		}
		
		void bindThrottleSlider(DoubleProperty throttle) {
			ThrottleSlider.valueProperty().bind(throttle);
		}
		
		void bindRudderSlider(DoubleProperty rudder) {
			RudderSlider.valueProperty().bind(rudder);
		}
}

