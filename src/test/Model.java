package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Model extends Observable{
	//Settings members
	HashMap<String,settingsObj> map;
	HashMap<String,String> filePathMap, remember;
	String anomalyFlightFilePath, validFlightFilePath, algorithmFilePath;
	Set<String> fileList;
	List<String> featureList;
	TimeSeries regTS, anomalyTS;
	int sampleRate;
	int timeStep;
	Thread t;
	AtomicBoolean stopFlag;
	AtomicBoolean isSimulated;
	ObservableList<String> fileSettingsObsList;


	double AileronVal;
	double ElevatorVal;
	double RudderVal;
	double ThrottleVal;
	double FlightHeightVal;
	double FlightSpeedVal;
	double RollVal;
	double PitchVal;
	double YawVal;
	
	
	TimeSeriesAnomalyDetector testAD;
	
	public void connectToSimulator() {
		new Thread(()->{
			Socket fg = null;
			try {
				fg = new Socket("127.0.0.1", 5400);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PrintWriter out = null;
			try {
				out = new PrintWriter(fg.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("Before while");
			while(!stopFlag.get()) {
				//System.out.println("Inside While");
				//System.out.println(String.valueOf(anomalyTS.getData()[timeStep]));
				String line = "";
				for(int i=0;i<anomalyTS.getNumOfFeatures();i++) {
					line +=anomalyTS.getData()[i][timeStep]+",";
				//	System.out.println(anomalyTS.getData()[i][timeStep]);
				}
				line = line.substring(0,line.length()-1);
				//System.out.println(line);
				out.println(line);
				out.flush();
				try {
					Thread.sleep(1000/sampleRate);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			out.close();
			try {
				fg.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
	
	
	public void pause() {
		System.out.println("Hello from pause model");
		new Thread(()-> {
			setStopFlag(true);
		}).start();
	}
	
	public void play() {
		System.out.println("Play from Model! ");
		setStopFlag(false);
		if(isSimulated.get()) {
		//	System.out.println("After the check!");
			new Thread(()->{
				connectToSimulator();
			}).start();
		}
		new Thread(()->{
			while(!stopFlag.get() && timeStep<anomalyTS.getNumOfRows()-1) {
				setTimeStep(timeStep+1);
			//	System.out.println("In while. timestep = " + timeStep + "Flag = " + stopFlag);
				try {
					Thread.currentThread();
					Thread.sleep(1000/sampleRate);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
			}
			}
		}).start();
	}
	
	public Model() {
		fileList = new HashSet<>();
		fileSettingsObsList  = FXCollections.observableArrayList();
		map = new HashMap<>();
		featureList = new ArrayList<>();
		stopFlag = new AtomicBoolean();
		isSimulated = new AtomicBoolean(false);
		File directory = new File("./SettingsFiles");
		File[] listOfFiles = directory.listFiles();
		if (listOfFiles!=null) {
			for(int i=0;i<listOfFiles.length;i++) {
				if(listOfFiles[i].isFile()) {
					fileList.add(listOfFiles[i].getName());
					fileSettingsObsList.add(listOfFiles[i].getName());
				}
			}
		}
		
	}
	
	public int getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(int timeStep) {
		this.timeStep = timeStep;
		//DecimalFormat df = new DecimalFormat("####.###");
		AileronVal = Math.floor(anomalyTS.getData()[map.get("aileron").getCulNumber()][timeStep]*100)/100;
		ElevatorVal = Math.floor(anomalyTS.getData()[map.get("elevator").getCulNumber()][timeStep]*100)/100;
		RudderVal = Math.floor(anomalyTS.getData()[map.get("rudder").getCulNumber()][timeStep]*100)/100;      
		ThrottleVal = Math.floor(anomalyTS.getData()[map.get("throttle").getCulNumber()][timeStep]*100)/100;
		FlightHeightVal = Math.floor(anomalyTS.getData()[map.get("flightHeight").getCulNumber()][timeStep]*100)/100;
		FlightSpeedVal = Math.floor(anomalyTS.getData()[map.get("flightSpeed").getCulNumber()][timeStep]*100)/100;
		RollVal = Math.floor(anomalyTS.getData()[map.get("roll").getCulNumber()][timeStep]*100)/100;
		PitchVal = Math.floor(anomalyTS.getData()[map.get("pitch").getCulNumber()][timeStep]*100)/100;
		YawVal = Math.floor(anomalyTS.getData()[map.get("yaw").getCulNumber()][timeStep]*100)/100;
		setChanged();
		notifyObservers();
	}
	
	public Set<String> getFileList() {
		return fileList;
	}

	public void setFileList(Set<String> fileList) {
		if(this.fileList == null) this.fileList = new HashSet<>();
		for (String string : fileList) {
			this.fileList.add(string);
		}
	}
	
	//Reading settings file, if its valid, save it as another file in the system directory.
	public void saveSettings(String filePath) {
		Scanner in = null;
		boolean isCorrect = true;
		int errorInLine = 1;
		//System.out.println(filePath);
		String[] path = filePath.split("\\\\");
		//System.out.println(Arrays.toString(path));
		String fileName = path[path.length-1];
		if(fileSettingsObsList.contains(fileName)) {
			System.out.println("File already exists.");
			return;
		}
		try {
			in = new Scanner(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(!in.hasNextLine()) {
			System.out.println("Settings file is empty.");
			return;
		}
		String line = in.nextLine();
		while(!line.equals("---") && isCorrect) { // reading features
				String[] lineArgs = line.split(",");
				//System.out.println(Arrays.toString(lineArgs) + " length is " + lineArgs.length );
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
		copyFile(filePath, fileName);
		fileList.add(fileName);
		fileSettingsObsList.add(fileName);
		in.close();
	}
	
	
	private void copyFile(String filePath, String fileName) {
		Scanner in = null;
		PrintWriter out = null;
		String line;
		String path = "./SettingsFiles/";
		File directory = new File(path);
		if (! directory.exists()) {
			directory.mkdir();
		}
		//if (!Files.exists(path))
		//System.out.println(".\\SettingsFiles\\" + fileName);
		try {
			in=new Scanner(new FileReader(filePath));
			out=new PrintWriter(new FileWriter(path + fileName));			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(in.hasNext()) {
			line = in.nextLine();
			//System.out.println("Copying -----" + line);
			out.println(line);;
		}
		in.close();
		out.close();
	}

	public void loadSettings(String fileName) {
		Scanner in = null;
		try {
			in=new Scanner(new FileReader("./SettingsFiles/"+fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
		remember.put("LSU", fileName);
		System.out.println("Loading " + fileName + " complete.");
	}
	
	public void openCSVFile(String fileName) {
		anomalyTS = new TimeSeries(fileName);
		for (Map.Entry<String, settingsObj> entry : map.entrySet()) {
			if (anomalyTS.getFeatures()[entry.getValue().getCulNumber()] == null) {
				System.out.println("Error. culumn number " + entry.getValue().getCulNumber() + " doesnt match the settings file.");
			}
		}
		featureList.addAll(Arrays.asList(anomalyTS.getFeatures()));
		timeStep = 0;
		System.out.println("Ready for simulation.");
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
		openCSVFile(file.getPath());
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
		this.stopFlag.set(b);
	}

	public String calculateMaxTimeStep() {
		return calculateTime(anomalyTS.getNumOfRows()/sampleRate);
		
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
//	public boolean isStopFlag() {
//		return stopFlag;
//	}
//
//	public void setStopFlag(boolean stopFlag) {
//		this.stopFlag = stopFlag;
//	}


	public int getMaxLines() {
		return anomalyTS.getNumOfRows();
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


	public ObservableList<String> getFileSettingsObsList() {
		return fileSettingsObsList;
	}


	public void setFileSettingsObsList(ObservableList<String> fileSettingsObsList) {
		this.fileSettingsObsList = fileSettingsObsList;
	}


	public ObservableList<String> getSettingFileList() {
		ObservableList<String> obsl = FXCollections.observableArrayList();
		obsl.addAll(fileList);
		System.out.println("TEST");
		for (String string : obsl) {
			System.out.println(string);
		}
		for (String string : fileList) {
			System.out.println(string);
		}
		return obsl;
	}


	public void deleteSettingsFile(String fileName) {
		if(fileSettingsObsList.contains(fileName))
			fileSettingsObsList.remove(fileName);
		File toBeRemoved = new File("./SettingsFiles/"+fileName);
		if(!toBeRemoved.delete()) 
			System.out.println("Failed to remove file " + fileName);
		else 
			System.out.println("File " + fileName + " has been removed from the system.");
	}


}

