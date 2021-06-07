package view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class Graphs extends AnchorPane{
	public final GraphsController controller;
	
	public Graphs() {
		FXMLLoader fxl = new FXMLLoader();
		AnchorPane ap = null;
		try {
			ap = fxl.load(getClass().getResource("Graphs.fxml").openStream());
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
