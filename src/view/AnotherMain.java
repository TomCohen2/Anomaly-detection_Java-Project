package view;
	
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.stage.Stage;
import test.Model;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class AnotherMain extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxml = new FXMLLoader();
			AnchorPane root = fxml.load(getClass().getResource("MainWindow.fxml").openStream());
			MainWindowController wc = fxml.getController();
//			LabelsAndValsController wc = fxml.getController();
			Model model = new Model();
//			//model.saveSettings("C:\\Users\\blind\\git\\PTM2Project\\EyalsSettings.txt");
//			//model.loadSettings("EyalsSettings.txt");
//			model.openCSVFile("C:\\Users\\blind\\git\\PTM2Project\\reg_flight.csv");
//			//model.initAnomalyTS("C:\\Users\\blind\\git\\PTM2Project\\anomaly_flight.csv"););
			ViewModel vm = new ViewModel(model);
			vm.initialize();
			wc.setViewModel(vm);
			wc.init();
			//wc.paintJoystick();
			
			
			Scene scene = new Scene(root,620,530);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
