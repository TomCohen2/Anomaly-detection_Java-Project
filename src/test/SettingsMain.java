package test;

public class SettingsMain {
	public static void main(String[] args) {
		Model model = new Model();
		System.out.println("Good luck!");
		model.saveSettings("C:\\College\\EyalsSettings.txt");
		//System.out.println(model.getFileList().get(0));
		model.loadSettings("EyalsSettings.txt");
		model.openCSVFile("C:\\Users\\blind\\git\\PTM2Project\\reg_flight.csv");
		System.out.println("We'r done!");
		//model.loadSettings("EyalsSettings.txt");
	}
}
