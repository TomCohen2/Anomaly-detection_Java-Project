package views;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class Pannel extends HBox{

	public final PannelController controller;
	
	public Pannel() {
		FXMLLoader fxl = new FXMLLoader();
		HBox hb = null;
		try {
			hb = fxl.load(getClass().getResource("Pannel.fxml").openStream());
		} catch (IOException e) {e.printStackTrace();}
		if(hb!=null) {
			controller = fxl.getController();
			this.getChildren().add(hb);
		}else
			controller = null;
	}
	
}
