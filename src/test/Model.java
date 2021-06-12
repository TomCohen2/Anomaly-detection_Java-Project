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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import test.TimeSeriesAnomalyDetector.GraphStruct;

public class Model extends Observable{
	//Settings members
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
	
	//ThreadPool
	ExecutorService es = Executors.newFixedThreadPool(10);
	private String selectedFlightToDisplay;
	
	public Model() {
		
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
		
		selectedAlgorithm = "Linear-Regression";
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

	
	public void finalize() {
		System.out.println("TEST");
	}
	public void connectToSimulator() {
		es.submit(()->{
			Socket fg = null;
			try {fg = new Socket("127.0.0.1", 5400);} catch (UnknownHostException e) {} catch (IOException e) {}
			PrintWriter out = null;
			try {out = new PrintWriter(fg.getOutputStream());} catch (IOException e) {}
			while(!stopFlag.get()) {
				String line = "";
				for(int i=0;i<anomalyTS.getNumOfFeatures();i++) {
					line +=anomalyTS.getData()[i][timeStep]+",";
				}
				line = line.substring(0,line.length()-1);
				out.println(line);
				out.flush();
				try {Thread.sleep((long) (1000/(sampleRate*playSpeed)));} catch (InterruptedException e) {}
			}
			out.close();
			try {fg.close();} catch (IOException e) {}
		});
	}

	
	public void play1() {
		setStopFlag(false);
		System.out.println("Play from Model! playflag = " + playFlag.get() + "StopFlag = " + stopFlag.get());
		if(isSimulated.get()) {
		//	System.out.println("After the check!");
			es.submit(()->{
				connectToSimulator();
			});
		}

		es.submit(()->{
			if(secondPlayFlag.get()) {
				setRewindFlag(false);
				return;
			}
			if(regTS==null) {
				System.out.println("Please upload a CSV File.");
				return;
			}
			while(!stopFlag.get() && playFlag.get()) {//NEXTTT
				setSecondPlayFlag(true);
				if(rewindFlag.get()==false && timeStep<regTS.getNumOfRows()-1)
					setTimeStep(timeStep+1);
				else if(timeStep>0)
					setTimeStep(timeStep-1);
				else
					pause();
				try {
					Thread.currentThread();
					Thread.sleep((long) (1000/(sampleRate*playSpeed)));
				} catch (InterruptedException e) {}
			}
		});
	}
	
	
	
	public void play() {
		Platform.runLater(()->{

		if(rewindFlag.get())
			rewindFlag.set(false);
		if(timer==null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if(!rewindFlag.get()) {
						if(timeStep<maxTime-1)
							setTimeStep(timeStep + 1);
						else
							pause();
					}
					else {
						if(timeStep>0)
							setTimeStep(timeStep - 1);
						else
							pause();
					}
					if (!selectedFeature.equals(""))
						displayGraphsCall(selectedFeature);
				}
			}, 0, (long)(1000/(playSpeed*sampleRate)));
		}
		});
	}
	
	public String getSelectedFeature() {
		return selectedFeature;
	}

	public void setSelectedFeature(String selectedFeature) {
		this.selectedFeature = selectedFeature;
	}

	public void pause() {
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
	}
	
	public void stop() {
		if(timer!=null) {
			timer.cancel();
			timer = null;
		}
		timeStep = 0;
		setChanged();
		notifyObservers("TimeStep");
	}
	
	public boolean getSecondPlayFlag() {
		return secondPlayFlag.get();
	}

	public void setSecondPlayFlag(boolean b) {
		this.secondPlayFlag.set(b);
	}

	public boolean getPlayFlag() {
		return playFlag.get();
	}

	public void setPlayFlag(boolean b) {
		this.playFlag.set(b);
	}

	public void pause1() {
		System.out.println("Hello from pause model");
		es.submit(()-> {
			setStopFlag(true);
		});
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
		//if(!rewindFlag.get())
			setRewindFlag(true);
		
	}

	public AtomicBoolean getRewindFlag() {
		return rewindFlag;
	}

	public void setRewindFlag(boolean b) {
		this.rewindFlag.set(b);
	}

	public void fastRewind() {
			rewind();
		DecimalFormat df = new DecimalFormat("###.##");
		this.setPlaySpeed(Double.parseDouble(df.format(playSpeed+0.5)));
		//setChanged();
		//notifyObservers("PlaySpeed");
		
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
					@SuppressWarnings("unused")
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
					@SuppressWarnings("unused")
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
					@SuppressWarnings("unused")
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
		regTS = new TimeSeries(fileName);
		for (Map.Entry<String, settingsObj> entry : map.entrySet()) {
			if (regTS.getFeatures()[entry.getValue().getCulNumber()] == null) {
				System.out.println("Error. culumn number " + entry.getValue().getCulNumber() + " doesnt match the settings file.");
			}
		}
		featureObsList.addAll(Arrays.asList(regTS.getFeatures()));
		featureObsList.forEach((a)->System.out.println(a));
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
		System.out.println(file.getPath());
		remember.put("TestFileName", file.getPath());
		setChanged();
		notifyObservers("TestFile");
		initAnomalyTS(file.getPath());
	}
	
	public void initAnomalyTS(String filePath) {
		this.anomalyTS = new TimeSeries(filePath);
	}

	public void newCSVFile() {
		FileChooser chooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)","*.csv");
		chooser.getExtensionFilters().add(extFilter);
		chooser.setTitle("Open CSV File");
		File file = chooser.showOpenDialog(new Stage());
		
		if (file == null) {
			System.out.println("File selection error. try again.");
			newCSVFile();
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
			System.out.println("No file is selected. Loading last loaded file.");
			loadSettings(remember.get("LSU"));
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
	
	

	
	@SuppressWarnings("unused")
	private void loadClass(String path) {
		// TODO Auto-generated method stub
		
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
	
	public GraphStruct displayGraphsCall(String selectedFeature) {
		System.out.println("From model Str = " + selectedFeature);
		GraphStruct struct = this.tsad.display(selectedFeature);
		System.out.println(struct.getStr());
		
		parseAndFill(struct);
		System.out.println("Test points number is " + struct.getPoints().getData().size());
//		for (Point p : test.getPoints()) {
//			System.out.println(p);
//		}
		Series<Number,Number> series = new Series<>();
		return struct;
	}


	private void parseAndFill(GraphStruct test) {
		System.out.println(test.getStr());
		String[]stringData = test.getStr().split(",");
		switch(stringData[0]) {
		case "LR":
			System.out.println(stringData[0] + " " + stringData[1] + " " + stringData[2]);
			int feature1Idx = StatLib.whichIndex(stringData[1], regTS);
			int feature2Idx = StatLib.whichIndex(stringData[2], regTS);
			float[] tempFloatsFeature1 = StatLib.TrimArr(anomalyTS.data[feature1Idx],this.getTimeStep());
			float[] tempFloatsFeature2 = StatLib.TrimArr(anomalyTS.data[feature2Idx], this.getTimeStep());
			//System.out.println("Feature 1 = " + stringData[1]);
//			for(int i=0;i<tempFloatsFeature1.length;i++) {
//				System.out.println(tempFloatsFeature1[i]);
//			}
//			System.out.println("Feature 2 = " + stringData[2]);
//			for(int i=0;i<tempFloatsFeature2.length;i++) {
//				System.out.println(tempFloatsFeature2[i]);
//			}
			Point[] temp = StatLib.arrToPoints(tempFloatsFeature1, tempFloatsFeature2);
			for(int i=0;i<temp.length;i++) {
				//System.out.println(temp[i].x + "," + temp[i].y);
				test.getPoints().getData().add(new XYChart.Data<>(temp[i].x,temp[i].y));
				////test.getFeature1Points().getData().add(new XYChart.Data<>(i,temp[i].x));
				test.getFeature1Points().getData().add(new XYChart.Data<>(i,temp[i].x));
				test.getFeature2Points().getData().add(new XYChart.Data<>(i,temp[i].y));
			}
			test.setMaxVal(StatLib.findMax(tempFloatsFeature1));
			test.setMinVal(StatLib.findMin(tempFloatsFeature1));
			break;
		case "Z":
			break;
		case "W":
			break;
		}
		
	}
	
	/////GETTERS & SETTERS
	
	public int getTimeStep() {
		return timeStep;
	}

	public int getMaxTime() {
		return maxTime;
	}
	
	public void setMaxTime(int numberOfRows) {
		maxTime = numberOfRows;
		System.out.println("testttt");
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
		setChanged();
		notifyObservers("TimeStep");
	}
	
	public ObservableList<String> getFeatureList() {
		return featureObsList;
	}
	public String getSelectedAlgorithm() {
		return selectedAlgorithm;
	}


	public void setSelectedAlgorithm(String selectedAlgorithm) {
		if (tsad==null) {
//			if(currentTab==1) {
				es.submit(()->{
						if(selectedAlgorithm.equals(""))
							tsad = Factory.get("Linear-Regression");
						else
							tsad = Factory.get(selectedAlgorithm);
							if(tsad == null) {
								URLClassLoader urlClassLoader = null;
								try {
									urlClassLoader = URLClassLoader.newInstance(new URL[] {
											new URL("file://"+algoFolder+selectedAlgorithm)
											});
								} catch (MalformedURLException e1) {}
										try {
											Class<?> c=urlClassLoader.loadClass(selectedAlgorithm);
										} catch (ClassNotFoundException e) {}
							}
							tsad.learnNormal(regTS);
							//tsad.detect(anomalyTS);				
				});
//			}
		}
		this.selectedAlgorithm = selectedAlgorithm;
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
		System.out.println(path);
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

	public void stop1() {
		this.setStopFlag(true);
		this.setTimeStep(0);	
		this.setPlaySpeed(1.0);
		setChanged();
		notifyObservers("PlaySpeed");
	}

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
		if(string.equals("Train Flight"))
				selectedTS = regTS;
		else if (string.equals("Test Flight"))
				selectedTS = anomalyTS;
		this.selectedFlightToDisplay = string;
		setMaxTime(selectedTS.getNumOfRows());
	}

}

