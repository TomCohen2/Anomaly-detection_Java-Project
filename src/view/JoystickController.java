package view;

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
			if (paintJoystick!=null)
				new Thread(()->paintJoystick.run()).start();
		}
		
		void bindThrottleSlider(DoubleProperty throttle) {
			ThrottleSlider.valueProperty().bind(throttle);
		}
		
		void bindRudderSlider(DoubleProperty rudder) {
			RudderSlider.valueProperty().bind(rudder);
		}
}

