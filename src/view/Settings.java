package view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class Settings extends AnchorPane{
	public final SettingsController controller;
	
	public Settings() {
		FXMLLoader fxl = new FXMLLoader();
		AnchorPane ap = null;
		try {
			ap = fxl.load(getClass().getResource("Settings.fxml").openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ap!=null) {
			controller = fxl.getController();
			this.getChildren().add(ap);
		}else
			controller = null;
	}

}
