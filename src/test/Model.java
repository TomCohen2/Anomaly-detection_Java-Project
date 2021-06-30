package test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import test.TimeSeriesAnomalyDetector.GraphStruct;

public class Model extends Observable{
	//Settings membersF
	static String settingsFolder = "./SettingsFiles/";
	static String algoFolder = "./AlgoFiles/";
	String selectedFeature = "";
	HashMap<String,settingsObj> map;
	HashMap<String,TimeSeriesAnomalyDetector> Factory;
	HashMap<String,String> filePathMap, remember;
	String anomalyFlightFilePath, validFlightFilePath, algorithmFilePath;
	ObservableList<String> featureObsList,fileSettingsObsList,algoObsList;
	TimeSeries regTS, anomalyTS, selectedTS;
	int sampleRate;
	int timeStep;
	int currentTab;
	volatile AtomicBoolean stopFlag;
	volatile AtomicBoolean isSimulated;
	volatile AtomicBoolean rewindFlag;
	volatile AtomicBoolean playFlag;
	volatile AtomicBoolean secondPlayFlag;
	XYChart.Series<Number, Number> goodPoints, badPoints, lineGraph, Feature1, Feature2;
	Timer timer = null;
	String selectedAlgorithm;
	double playSpeed = 1;
	double AileronVal;
	double ElevatorVal;
	double RudderVal;
	double ThrottleVal;
	double FlightHeightVal;
	double FlightSpeedVal;
	double RollVal;
	double PitchVal;
	double YawVal;
	int maxTime;
	GraphStruct struct = null;
	TimeSeriesAnomalyDetector tsad;
	int lastTimeStep = 0;
	String tempFeature;
	AtomicBoolean corFlag = new AtomicBoolean(false);
	XYChart.Series welzlCircle, goodWelzl, badWelzl;
	volatile AtomicBoolean reportFlag;
	HashMap<Long, String> report;
	String msg, algoType;
	
	int selectedFeature1, selectedFeature2;
	float[] feature1, feature2, trimmedFeature1, trimmedFeature2;
	
	//ThreadPool
	ExecutorService es = Executors.newFixedThreadPool(10);
	private String selectedFlightToDisplay;
	
	public Model() {
		
		report = null;
		regTS = null;
		anomalyTS = null;
		selectedFlightToDisplay = null;
		reportFlag = new AtomicBoolean(false);
		
		
		fileSettingsObsList  = FXCollections.observableArrayList();
		featureObsList = FXCollections.observableArrayList();
		algoObsList = FXCollections.observableArrayList();
		map = new HashMap<>();
		remember = new HashMap<>();
		stopFlag = new AtomicBoolean(false);
		isSimulated = new AtomicBoolean(false);
		rewindFlag = new AtomicBoolean(false);
		playFlag = new AtomicBoolean(false);
		secondPlayFlag = new AtomicBoolean(false);
	
		
		selectedAlgorithm = "";
		Factory = new HashMap<>();
		FillFactory();
		File AlgoDir = new File(algoFolder);
		File SettingsDir = new File(settingsFolder);
		File[] listOfFiles = SettingsDir.listFiles();
		if (listOfFiles!=null) {
			for(int i=0;i<listOfFiles.length;i++) {
				if(listOfFiles[i].isFile()) {
					fileSettingsObsList.add(listOfFiles[i].getName());
				}
			}
		}		
		//AlgoList
		algoObsList.add("Linear-Regression");
		algoObsList.add("Z-Score");
		algoObsList.add("Hybrid");
		listOfFiles = AlgoDir.listFiles();
		if (listOfFiles!=null) {
			for(int i=0;i<listOfFiles.length;i++) {
				if(listOfFiles[i].isFile()) {
					algoObsList.add(listOfFiles[i].getName());
				}
			}
		}
		
		File xmlMap = new File("map.XML");
		if(xmlMap.exists()) loadMapFromDisk();
		loadSettings(remember.get("LastSettingsFileName"));
	}
	
	public double getPlaySpeed() {
		return playSpeed;
	}

	public void setPlaySpeed(double playSpeed) {
		this.playSpeed = playSpeed;
		if(timer!=null) {
			timer.cancel();
			timer=null;
			play();
		}
		setChanged();
		notifyObservers("PlaySpeed");
		System.out.println("Playspeed has changed to " + playSpeed);
	}
	
	private void saveMapToDisk() {
		XMLEncoder xe = null;
		try {
			xe = new XMLEncoder(new FileOutputStream(new File("map.XML")));
		} catch (FileNotFoundException e) {}
		xe.writeObject(remember);
		xe.close();
	}
	
	private void loadMapFromDisk(){
		XMLDecoder xd = null;
		 try {
			xd = new XMLDecoder(new FileInputStream(new File("map.XML")));
		} catch (FileNotFoundException e) {}
		 remember = (HashMap<String, String>) xd.readObject();
	}

	public void connectToSimulator() {
			Socket fg = null;
			try {fg = new Socket("127.0.0.1", 5400);} catch (UnknownHostException e) {} catch (IOException e) {}
			PrintWriter out = null;
			try {out = new PrintWriter(fg.getOutputStream());} catch (IOException e) {}
			while(!stopFlag.get()) {
				String line = "";
				for(int i=0;i<selectedTS.getNumOfFeatures();i++) {
					line +=selectedTS.getData()[i][timeStep]+",";
				}
				line = line.substring(0,line.length()-1);
				System.out.println(line);
				out.println(line);
				out.flush();
				try {Thread.sleep((long) (1000/(sampleRate*playSpeed)));} catch (InterruptedException e) {}
			}
			out.close();
			try {fg.close();} catch (IOException e) {}
	}

	public void play() {
		if(selectedTS!=null) {
			if(isSimulated.get()) {es.submit(()->{connectToSimulator();});}
			
			if(timer==null) {
				playFlag.set(true);
				timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						if(lastTimeStep != timeStep && !selectedFeature.equals("")) {
							redraw();
						}
						if(!rewindFlag.get()) {
							if(timeStep<maxTime-1) {
								setTimeStep(timeStep + 1);
								lastTimeStep++;
							}
							else
								pause();
						}
						else {
							if(timeStep>0) {
								setTimeStep(timeStep - 1);
								lastTimeStep--;
							}
							else
								pause();
						}
					}
				}, 0, (long)(1000/(playSpeed*sampleRate)));
			}
			else
				rewindFlag.set(false);
		}
		else {
			setAlert("please select a flight file to display");
		}
	}
	
	public String getSelectedFeature() {
		return selectedFeature;
	}

	public void setSelectedFeature(String selectedFeature) {
		this.selectedFeature = selectedFeature;
		GraphStruct tempStruct = this.tsad.display(selectedFeature);
		if (tempStruct!=null ) {
			struct = tempStruct;
			tempFeature = selectedFeature;
			parseAndFill(struct);
		}
		else {
			selectedFeature = tempFeature;
			setAlert("There is no correlated feature for the selected feature.");
			setChanged();
			notifyObservers("NoGraph!");
		}
	}

	public void pause() {
		if(timer!=null) {
			timer.cancel();
			playFlag.set(false);
			timer = null;
		}
	}
	
	public void stop() {
		if(selectedTS != null) {
			if(timer!=null) {
				timer.cancel();
				playFlag.set(false);
				timer = null;
			}
			timeStep = 0;
			setChanged();
			notifyObservers("TimeStep");
		}
	}
	
//	
	public boolean getPlayFlag() {
		return playFlag.get();
	}

	public void setPlayFlag(boolean b) {
		this.playFlag.set(b);
	}


	public void fastForward() {
		DecimalFormat df = new DecimalFormat("###.##");
		this.setPlaySpeed(Double.parseDouble(df.format(playSpeed+0.1)));
	}

	public void superFastForward() {
		DecimalFormat df = new DecimalFormat("###.##");
		this.setPlaySpeed(Double.parseDouble(df.format(playSpeed+0.5)));
	}

	public void rewind() {
		setRewindFlag(true);
	}

	public AtomicBoolean getRewindFlag() {
		return rewindFlag;
	}

	public void setRewindFlag(boolean b) {
		this.rewindFlag.set(b);
	}

	public void fastRewind() {
		if(!rewindFlag.get())
			setRewindFlag(true);
		DecimalFormat df = new DecimalFormat("###.##");
		this.setPlaySpeed(Double.parseDouble(df.format(playSpeed+0.5)));
	}
	
	private void FillFactory() {
		Factory.put("Linear-Regression", new SimpleAnomalyDetector());
		Factory.put("Z-Score", new ZscoreAnomalyDetector());
		Factory.put("Hybrid", new HybridAnomalyDetector());
	}

	//Reading settings file, if its valid, save it as another file in the system directory.
	public void saveSettings(String filePath) {
		Scanner in = null;
		boolean isCorrect = true;
		int errorInLine = 1;
		String[] path = filePath.split("\\\\");
		String fileName = path[path.length-1];
		if(fileSettingsObsList.contains(fileName)) {
			System.out.println("File already exists.");
			return;
		}
		try {
			in = new Scanner(new FileReader(filePath));
		} catch (FileNotFoundException e) {}
		
		if(!in.hasNextLine()) {
			System.out.println("Settings file is empty.");
			return;
		}
		String line = in.nextLine();
		while(!line.equals("---") && isCorrect) { // reading features
				String[] lineArgs = line.split(",");
				if (lineArgs.length!=4 && lineArgs.length!=2) {
					isCorrect = false;
					System.out.println("Error in line " + errorInLine + " in your settings file, Wrong format, expected culName,culNumber.");
				}
				if (lineArgs.length == 4) {
					try {
						Float.parseFloat(lineArgs[3]);
						Float.parseFloat(lineArgs[2]);
					} catch(NumberFormatException e){
						isCorrect = false;
						System.out.println("Error in line " + errorInLine + "in your settings file, either min/max value is incorrect.");
					}
					
				}
				try {
					Integer.parseInt(lineArgs[1]);
				} catch(NumberFormatException e) {
					isCorrect = false;
					System.out.println("Error in line " + errorInLine + " in your settings file, culNumber is incorrect.");
				}
				errorInLine++;
				line = in.nextLine();
		}// end of features
		while(in.hasNext()) {
			line = in.nextLine();
			String[] lineArgs = line.split(",");
			switch(lineArgs[0]) {
			case "sampleRate":
				try {
					Integer.parseInt(lineArgs[1]);
				} catch(NumberFormatException e) {
					System.out.println("Error in line " + errorInLine + " in your settings file, Invalid sampleRate. ");
					return;
				}
				errorInLine++;
				break;
			case "anomalyFlightFilePath":
				try {
					Scanner temp = new Scanner(new FileReader(lineArgs[1]));
					if(temp!=null)
						temp.close();
				} catch (FileNotFoundException e) {
					System.out.println("Error in line " + errorInLine + " in your settings file, Anomaly file missing. ");
					return;
				}
				
				errorInLine++;
				break;
			case "validFlightFilePame":
				try {
					Scanner temp = new Scanner(new FileReader(lineArgs[1]));
					if(temp!=null)
						temp.close();
				} catch (FileNotFoundException e) {
					System.out.println("Error in line " + errorInLine + " in your settings file, RegFlight file missing. ");
					return;
				}
				errorInLine++;
				break;
			case "algorithm":
				try {
					Scanner temp = new Scanner(new FileReader(lineArgs[1]));
					if(temp!=null)
						temp.close();
				} catch (FileNotFoundException e) {
					System.out.println("Error in line " + errorInLine + " in your settings file, Algo file missing. ");
					return;
				}
				errorInLine++;
				break;
			default:
				System.out.println("Error in line " + errorInLine + " in your settings file, Unknown line. ");
				return;
			}
		} // end of misc. if we'r passed here, the file is ok.
		copyFile(filePath, fileName,settingsFolder);
		fileSettingsObsList.add(fileName);
		in.close();
	}
	
	public void redraw() {
		lastTimeStep = timeStep;
		parseAndFill(struct);

	}
	
	private void saveAlgo(String path) {
		Scanner in = null;
		String[] pathArgs = path.split("\\\\");
		String fileName = pathArgs[pathArgs.length-1];
		if(algoObsList.contains(fileName)) {
			System.out.println("File already exists.");
			return;
		}
		try {
			in = new Scanner(new FileReader(path));
		} catch (FileNotFoundException e) {}
		
		if(!in.hasNextLine()) {
			System.out.println("Algo class file is empty.");
			return;
		}
		copyFile(path, fileName, algoFolder);
	}
	
	private void copyFile(String filePath, String fileName,String dest) {
		Scanner in = null;
		PrintWriter out = null;
		String line;
		File directory = new File(dest);
		if (! directory.exists()) {
			directory.mkdir();
		}
		try {
			in=new Scanner(new FileReader(filePath));
			out=new PrintWriter(new FileWriter(dest + fileName));			
		} catch (FileNotFoundException e) {} catch (IOException e) {}
		while(in.hasNext()) {
			line = in.nextLine();
			out.println(line);;
		}
		in.close();
		out.close();
	}

	public void loadSettings(String fileName) {
		Scanner in = null;
		try {
			in=new Scanner(new FileReader(settingsFolder+fileName));
		} catch (FileNotFoundException e) {}
		
		if(!in.hasNextLine()) {
			System.out.println("Settings file is empty.");
			return;
		}
		
		String line = in.nextLine();
		
		while(!line.equals("---")) { // reading features
			String[] lineArgs = line.split(",");
			if(lineArgs.length == 2) {
				map.put(lineArgs[0], new settingsObj(Integer.parseInt(lineArgs[1])));
			}
			else if(lineArgs.length == 4){ // lineargs = 4
				map.put(lineArgs[0], new settingsObj(Integer.parseInt(lineArgs[1]),Float.parseFloat(lineArgs[2]),Float.parseFloat(lineArgs[3])));
			}
			else {
				System.out.println("error in settings file."); // should never happen
			}
			line = in.nextLine();
		} // finished features
		while(in.hasNext()) {
			line = in.nextLine(); 
			String[] lineArgs = line.split(",");
			switch(lineArgs[0]) {
			case "sampleRate":
				sampleRate = Integer.parseInt(lineArgs[1]);
				break;
			case "anomalyFlightFilePath":
				anomalyFlightFilePath = lineArgs[1];
				break;
			case "validFlightFilePame":
				validFlightFilePath = lineArgs[1];
				break;
			case "algorithm":
				algorithmFilePath = lineArgs[1];
				break;
				
			}
		}
		in.close();
		remember.put("LastSettingsFileName", fileName);
		saveMapToDisk();
		System.out.println("Loading " + fileName + " complete.");
	}
	
	public void openCSVFile(String fileName) {
		this.selectedAlgorithm = "";
		regTS = new TimeSeries(fileName);
		for (Map.Entry<String, settingsObj> entry : map.entrySet()) {
			if (regTS.getFeatures()[entry.getValue().getCulNumber()] == null) {
				System.out.println("Error. culumn number " + entry.getValue().getCulNumber() + " doesnt match the settings file.");
			}
		}
		featureObsList.addAll(Arrays.asList(regTS.getFeatures()));
		timeStep = 0;
		setChanged();
		notifyObservers("TrainFile");
		System.out.println("Ready for simulation.");
	}
	
	public void uploadTestFile() {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)","*.csv");
		chooser.getExtensionFilters().add(extFilter);
		chooser.setTitle("Open CSV Test File");
		File file = chooser.showOpenDialog(new Stage());

		if(file == null) {
			System.out.println("No file is selected. Try again.");
			return;
		}
		remember.put("TestFileName", file.getPath());
		setChanged();
		notifyObservers("TestFile");
		initAnomalyTS(file.getPath());
	}
	
	public void initAnomalyTS(String filePath) {
		this.selectedAlgorithm = "";
		this.anomalyTS = new TimeSeries(filePath);
	}

	public void newCSVFile() {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)","*.csv");
		chooser.getExtensionFilters().add(extFilter);
		chooser.setTitle("Open CSV File");
		File file = chooser.showOpenDialog(new Stage());
		
		if (file == null) {
			setAlert("File has not been selected. \nPlease try again.");
			return;
		}
		remember.put("TrainFilePath", file.getPath());
		openCSVFile(file.getPath());
	}
	

	public String calculateMaxTimeStep() {
		return calculateTime((int)(selectedTS.getNumOfRows()/sampleRate));
		
	}

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	public String calculateTime(int totalSeconds)
	{
		String res = "";
		int hours = totalSeconds/3600;
		totalSeconds%=3600;
		int minutes = totalSeconds/60;
		int seconds = totalSeconds%60;
		if(hours<10) res+= "0"+hours+":";
		else
			res+= hours+":";
		if(minutes<10) res+= "0"+minutes+":";
		else
			res+= minutes+":";
		if(seconds<10) res+= "0"+seconds;
		else
			res+= seconds;
		return res;
	}

	public int getMaxLines() {
		return regTS.getNumOfRows();
	}


	public void newSettingsFile() {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)","*.txt");
		chooser.getExtensionFilters().add(extFilter);
		chooser.setTitle("Open txt File");
		File file = chooser.showOpenDialog(new Stage());

		if(file == null) {
			setAlert("No file has been selected.");
			return;
		}
		saveSettings(file.getPath());
		
	}
	
	public void newAlgoFile() {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CLASS files (*.class)","*.class");
		chooser.getExtensionFilters().add(extFilter);
		chooser.setTitle("Open Class File");
		File file = chooser.showOpenDialog(new Stage());

		if(file == null) {
			System.out.println("No file is selected. Try again.");
			return;
		}
		remember.put(file.getName(), file.getPath());
		algoObsList.add(file.getName().substring(0,file.getName().lastIndexOf('.')));
		setChanged();
		notifyObservers("AlgoFile");
		saveAlgo(file.getPath());
	}	

	public ObservableList<String> getFileSettingsObsList() {
		return fileSettingsObsList;
	}


	public void setFileSettingsObsList(ObservableList<String> fileSettingsObsList) {
		this.fileSettingsObsList = fileSettingsObsList;
	}


	public void deleteSettingsFile(String fileName) {
		if(fileSettingsObsList.contains(fileName))
			fileSettingsObsList.remove(fileName);
		removeFile(settingsFolder+fileName,fileName);
		
	}

	public void deleteAlgoFile(String algoName) {
		if(algoName.equals("Z-Score") || algoName.equals("Linear-Regression") || algoName.equals("Hybrid")) {
			System.out.println("Deleting a basic algorithm is forbidden.");
			return;
		}
		if(algoObsList.contains(algoName))
			algoObsList.remove(algoName);
		removeFile(algoFolder+algoName,algoName);
		
	}
	
	private void removeFile(String path,String fileName) {
		File toBeRemoved = new File(path);
		if(!toBeRemoved.delete()) 
			System.out.println("Failed to remove " + path);
		else 
			System.out.println("File " + fileName + " has been removed from the system.");
	}
	


	private void parseAndFill(GraphStruct test) {
		String[]stringData = struct.getStr().split(",");
		algoType = stringData[0];
		switch(stringData[0]) {
		case "LR":
			selectedFeature1 = StatLib.whichIndex(stringData[1], anomalyTS);
			selectedFeature2 = StatLib.whichIndex(stringData[2], anomalyTS);
			feature1 = anomalyTS.getData()[selectedFeature1];
			feature2 = anomalyTS.getData()[selectedFeature2];
			trimmedFeature1 = StatLib.TrimArr(feature1,this.getTimeStep());
			trimmedFeature2 = StatLib.TrimArr(feature2, this.getTimeStep());

			float min = Float.MAX_VALUE;
			float max = Float.MIN_VALUE;
			
			for(int i=0;i<feature1.length;i++) {
				if(feature1[i]>max)
					max = feature1[i];
				if(feature1[i]<min)
					min = feature1[i];
			}
			goodPoints = new XYChart.Series<Number, Number>();
			badPoints = new XYChart.Series<Number, Number>();
			lineGraph = new XYChart.Series<Number, Number>();
			Feature1 = new XYChart.Series<Number, Number>();
			Feature2 = new XYChart.Series<Number, Number>();
			
			lineGraph.getData().add(new XYChart.Data(min,struct.l.f(min)));
			lineGraph.getData().add(new XYChart.Data(max,struct.l.f(max)));
			for(int i=0;i<trimmedFeature1.length;i++) {
				String temp = report.get((long)i);
				if(temp == null || !temp.contains(selectedFeature)) 
					goodPoints.getData().add(new XYChart.Data(feature1[i],feature2[i]));
				else
					badPoints.getData().add(new XYChart.Data(feature1[i],feature2[i]));	
				
				Feature1.getData().add(new XYChart.Data(i,feature1[i]));
				Feature2.getData().add(new XYChart.Data(i,feature2[i]));
			}
			setChanged();
			notifyObservers("newGraphLR");
			break;
		case "Z":
			feature1 = test.getzScores();
			trimmedFeature1 = StatLib.TrimArr(feature1,this.getTimeStep());
			
			goodPoints = new XYChart.Series<Number, Number>();
			badPoints = new XYChart.Series<Number, Number>();
			lineGraph = new XYChart.Series<Number, Number>();
			
			lineGraph.getData().add(new XYChart.Data(0,test.getThreshold()));
			lineGraph.getData().add(new XYChart.Data(anomalyTS.getNumOfRows(),test.getThreshold()));
			
			for(int i=0;i<trimmedFeature1.length;i++) {
				String temp = report.get((long)i);
				if(temp == null || !temp.contains(selectedFeature)) 
					goodPoints.getData().add(new XYChart.Data(i,feature1[i]));
				else
					badPoints.getData().add(new XYChart.Data(i,feature1[i]));	
			}
			setChanged();
			notifyObservers("newGraphZ");
			break;
		case "W":
			selectedFeature1 = StatLib.whichIndex(stringData[1], anomalyTS);
			selectedFeature2 = StatLib.whichIndex(stringData[2], anomalyTS);
			feature1 = anomalyTS.getData()[selectedFeature1];
			feature2 = anomalyTS.getData()[selectedFeature2];
			trimmedFeature1 = StatLib.TrimArr(feature1,this.getTimeStep());
			trimmedFeature2 = StatLib.TrimArr(feature2, this.getTimeStep());
			
			welzlCircle = new XYChart.Series<>();
			goodWelzl = new XYChart.Series<>();
			badWelzl = new XYChart.Series<>();
		    Circle c = test.c;
			welzlCircle.getData().add(new XYChart.Data(c.center.x,c.center.y,c.radius));
			
			for(int i=0;i<trimmedFeature1.length;i++) {
				String temp = report.get((long)i);
				if(temp == null || !temp.contains(selectedFeature)) 
					goodWelzl.getData().add(new XYChart.Data(feature1[i],feature2[i],3));
				else
					badWelzl.getData().add(new XYChart.Data(feature1[i],feature2[i],3));	
				
				Feature1.getData().add(new XYChart.Data(i,feature1[i]));
				Feature2.getData().add(new XYChart.Data(i,feature2[i]));
			}
			setChanged();
			notifyObservers("newGraphW");
			break;
		}
	}
	

	/////GETTERS & SETTERS
	
	public float[] getTrimmedFeature1() {
		return trimmedFeature1;
	}

	public void setTrimmedFeature1(float[] trimmedFeature1) {
		this.trimmedFeature1 = trimmedFeature1;
	}

	public float[] getTrimmedFeature2() {
		return trimmedFeature2;
	}

	public void setTrimmedFeature2(float[] trimmedFeature2) {
		this.trimmedFeature2 = trimmedFeature2;
	}

	public int getTimeStep() {
		return timeStep;
	}

	public int getMaxTime() {
		return maxTime;
	}
	
	public void setMaxTime(int numberOfRows) {
		maxTime = numberOfRows-1;
		setChanged();
		notifyObservers("MaxTime");
	}
	
	public void setTimeStep(int timeStep) {
		this.timeStep = timeStep;
		AileronVal = Math.floor(regTS.getData()[map.get("aileron").getCulNumber()][timeStep]*100)/100;
		ElevatorVal = Math.floor(regTS.getData()[map.get("elevator").getCulNumber()][timeStep]*100)/100;
		RudderVal = Math.floor(regTS.getData()[map.get("rudder").getCulNumber()][timeStep]*100)/100;      
		ThrottleVal = Math.floor(regTS.getData()[map.get("throttle").getCulNumber()][timeStep]*100)/100;
		FlightHeightVal = Math.floor(regTS.getData()[map.get("flightHeight").getCulNumber()][timeStep]*100)/100;
		FlightSpeedVal = Math.floor(regTS.getData()[map.get("flightSpeed").getCulNumber()][timeStep]*100)/100;
		RollVal = Math.floor(regTS.getData()[map.get("roll").getCulNumber()][timeStep]*100)/100;
		PitchVal = Math.floor(regTS.getData()[map.get("pitch").getCulNumber()][timeStep]*100)/100;
		YawVal = Math.floor(regTS.getData()[map.get("yaw").getCulNumber()][timeStep]*100)/100;		
		if(playFlag.get()) {
			setChanged();
			notifyObservers("TimeStep");
		}
	}
	
	public ObservableList<String> getFeatureList() {
		return featureObsList;
	}
	public String getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void clearSeries() {
		if (goodPoints!= null)
			goodPoints.getData().clear();
		
		if (badPoints!= null)
			badPoints.getData().clear();
		
		if (lineGraph!= null)
			lineGraph.getData().clear();
		
		if (Feature1!= null)
			Feature1.getData().clear();
		
		if (Feature2!= null)
			Feature2.getData().clear();
	}
	public void setSelectedAlgorithm(String selectedAlgorithm) {
		clearSeries();
		selectedFeature = "";
		if(anomalyTS != null && regTS != null) {
			if(selectedAlgorithm != null) {
				if (!this.selectedAlgorithm.equals(selectedAlgorithm)) {
					es.submit(()->{
						tsad = Factory.get(selectedAlgorithm);
						if(tsad == null) {
							URLClassLoader urlClassLoader = null;
							try {
								System.out.println("Are we here?");
								urlClassLoader = URLClassLoader.newInstance(new URL[] {
										new URL("file://C:/College/eclipse/PTM2Project/PTM2ProjectMerged/AlgoFiles")
										});
								} catch (MalformedURLException e1) {}
							try {
								System.out.println("Are we here?2 test." +selectedAlgorithm.substring(0,selectedAlgorithm.length()-6));
								Class<?> c=urlClassLoader.loadClass("test."+selectedAlgorithm.substring(0,selectedAlgorithm.length()-6));
								System.out.println("Are we here?3");
								tsad = (TimeSeriesAnomalyDetector) c.newInstance();
								} catch (ClassNotFoundException e) {System.out.println(e.getMessage());} catch (InstantiationException e) {System.out.println(e.getMessage());} catch (IllegalAccessException e) {System.out.println(e.getMessage());}
							}
						tsad.learnNormal(regTS);
						setAnomalyReport(tsad.detect(anomalyTS));
						});
					}
				this.selectedAlgorithm = selectedAlgorithm;
				}else 
					setAlert("Please choose an Algorithem yaniv");
			}else
				setAlert("Anomaly detection requires both train and test files.");
		}

	public ObservableList<String> getAlgoList() {
		return algoObsList;
	}


	public void setAlgoList(ObservableList<String> algoList) {
		this.algoObsList.addAll(algoList);
	}

	public double getAileronVal() {
		return AileronVal;
	}

	public void setAileronVal(double aileronVal) {
		AileronVal = aileronVal;
	}

	public double getElevatorVal() {
		return ElevatorVal;
	}

	public void setElevatorVal(double elevatorVal) {
		ElevatorVal = elevatorVal;
	}

	public double getRudderVal() {
		return RudderVal;
	}

	public void setRudderVal(double rudderVal) {
		RudderVal = rudderVal;
	}

	public double getThrottleVal() {
		return ThrottleVal;
	}

	public void setThrottleVal(double throttleVal) {
		ThrottleVal = throttleVal;
	}

	public double getFlightHeightVal() {
		return FlightHeightVal;
	}

	public void setFlightHeightVal(double flightHeightVal) {
		FlightHeightVal = flightHeightVal;
	}

	public double getFlightSpeedVal() {
		return FlightSpeedVal;
	}

	public void setFlightSpeedVal(double flightSpeedVal) {
		FlightSpeedVal = flightSpeedVal;
	}

	public double getRollVal() {
		return RollVal;
	}

	public void setRollVal(double rollVal) {
		RollVal = rollVal;
	}

	public double getPitchVal() {
		return PitchVal;
	}

	public void setPitchVal(double pitchVal) {
		PitchVal = pitchVal;
	}

	public double getYawVal() {
		return YawVal;
	}

	public void setYawVal(double yawVal) {
		YawVal = yawVal;
	}

	public AtomicBoolean getIsSimulated() {
		return isSimulated;
	}

	public void setIsSimulated(boolean b) {
		this.isSimulated.set(b);
	}

	
	public AtomicBoolean getStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(boolean b) {
		this.playFlag.set(false);
		this.stopFlag.set(b);
	}
 

	public String getTrainFileName() {
		return getFileNameFromPath(remember.get("TrainFileName"));
	}


	public String getTestFileName() {
		return getFileNameFromPath(remember.get("TestFileName"));
	}

	private String getFileNameFromPath(String path) {
		String[] tokens = path.split("\\\\");
		return tokens[tokens.length-1];
	}

	public String getLastSettingsUsed() {
		return remember.get("LastSettingsFileName");
	}

	public int setCurrentTab(int i) {
		System.out.println("Current tab is " +i);
		currentTab=i;
		return currentTab;
	}

//	
	public float getMinVal(String selectedItem) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getMaxVal(String selectedItem) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSelectedFlightToDisplay(String string) {
		System.out.println("Selected flight is " + string);
		if(string!=null) {
		if(string.equals("Train Flight"))
				selectedTS = regTS;
		else if (string.equals("Test Flight"))
				selectedTS = anomalyTS;
		if(selectedTS != null) {
			this.selectedFlightToDisplay = string;
			setMaxTime(selectedTS.getNumOfRows());
		}else {
			selectedTS = null;
			selectedFlightToDisplay = null;
			setAlert("no such file: " + string);
		}
		}
	}
	
	
	public String getAlert() {
		return msg;
	}
	
	
	public void setAlert(String message) {
		this.msg=message;
		setChanged();
		notifyObservers("Alert");
	}
	
	public void setAnomalyReport(List<AnomalyReport> repo) {
		reportFlag.set(false);
		report = new HashMap<>();
		for(AnomalyReport r : repo) 
			report.put(r.timeStep, r.description);
		
		//report.forEach((k,v)->System.out.println(k+" "+v));
		
		reportFlag.set(true);
		
	}

	public boolean isAnomaly() {
		String temp = report.get((long)this.timeStep);
		if(temp == null || !temp.contains(selectedFeature)) 
				return false;
		else
			return true;

	}
	
	
	public XYChart.Series<Number, Number> getGoodPoints() {
		return goodPoints;
	}

	public XYChart.Series<Number, Number> getBadPoints() {
		return badPoints;
	}

	public boolean getReportFlag() {
		return reportFlag.get();
	}

	public Data<Number, Number> getPoint() {
		if(algoType.equals("LR"))
			return new Data<Number,Number>(feature1[timeStep],feature2[timeStep]);
		return new Data<Number,Number>(timeStep,feature1[timeStep]);
	}

	public XYChart.Series<Number, Number> getLine() {
		return lineGraph;
	}



	public Data<Number, Number> getFeaturePoint(int num) {
		float value;
		if (num == 1)
			value = feature1[this.timeStep];
		else if(num ==2)
			value = feature2[this.timeStep];
		else
			return null;
		return new Data(this.timeStep,value);
	}

	public XYChart.Series<Number, Number> getFeatureData(int i) {
		if (i== 1)
			return Feature1;
		else if (i==2)
			return Feature2;
		else
			return null;
	}

	public boolean getCorFlag() {
		return corFlag.get();
	}

	public XYChart.Series getGoodWelzel() {
		// TODO Auto-generated method stub
		return goodWelzl;
	}

	public XYChart.Series getBadWelzl() {
		// TODO Auto-generated method stub
		return badWelzl;
	}

	public XYChart.Series getWelzlCircle() {
		return welzlCircle;
	}

	public Object getWelslPoint() {
		return new XYChart.Data(feature1[timeStep],feature2[timeStep],3);
	}
	
}
