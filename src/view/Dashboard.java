package view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class Dashboard extends AnchorPane{
	public final DashboardController controller;
	
	public Dashboard() {
		FXMLLoader fxl = new FXMLLoader();
		AnchorPane ap = null;
		try {
			ap = fxl.load(getClass().getResource("Dashboard.fxml").openStream());
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
