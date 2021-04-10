package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Commands {
	// Default IO interface
	public interface DefaultIO{
		public String readText();
		public void write(String text);
		public float readVal();
		public void write(float val);
		
		// you may add default methods here
	}
	
	//Commands Members:
	DefaultIO dio;
	private SharedState sharedState=new SharedState();
	public boolean flag;
	
	//Constructor:
	public Commands(DefaultIO dio) {
		this.dio=dio;
	}
	
	// you may add other helper classes here

	// Inner class(member)
		private class SharedState{
			//SharedState members
			private int numOfCommands;
			private HashMap<Integer,Command> commandList;
			private TimeSeries trainCSV, testCSV;
			private SimpleAnomalyDetector SimpleAnoD;
			private List<AnomalyReport> reports;
			
			
			//SharedState constructor
			public SharedState() {
				this.numOfCommands = 0;
				this.commandList = null;
				this.trainCSV = null;
				this.testCSV = null;
			}
			
			//Simple getter
			public int getNumOfCommands() {
				return numOfCommands;
			}
			
			//Simple Setter
			public void setNumOfCommands(int numOfCommands) {
				this.numOfCommands = numOfCommands;
			}	
		// implement here whatever you need
	}
		
	//Simple getter
	public SharedState getSharedState() {
		return sharedState;
	}
	
	//Simple setter
	public void setSharedState(SharedState sharedState) {
		this.sharedState = sharedState;
	}
	
	//Prints userMenu.
	public void printMenu() {
		sharedState.commandList.forEach((k,v)->writeToFile(k + ". " + v.description));
		}
	
	//Executes a command
	public void executeCommand(int cmdNum) {
		sharedState.commandList.get(cmdNum).execute();
	}
	
	//Adds Command c into the shared map.
	public Command addCommand(Command c) {
		if (sharedState.commandList == null)
			sharedState.commandList = new HashMap<Integer,Command>();
			sharedState.setNumOfCommands(sharedState.getNumOfCommands()+1);
			sharedState.commandList.put(sharedState.getNumOfCommands(), c);
		return c;
	}
	
	// Command abstract class
	public abstract class Command{
		protected String description;
		protected int commandNum;
		//Constructor
		public Command(String description,int commandNum) {
			this.description=description;

		}
		
		//Simple getter
		public String getDescription() {
			return description;
		}
		
		//Simple setter
		public void setDescription(String description) {
			this.description = description;
		}

		//Abstract function
		public abstract void execute();
	}
	
		// Command class for uploading
		public class UploadCmd extends Command{
			//Constructor
			public UploadCmd() {
				super("upload a time series csv file",1);
			}
			
			@Override
			public void execute() {
				writeToFile("Please upload your local train CSV file.");
				readToFile("trainFile1.csv");
				sharedState.trainCSV = new TimeSeries("trainFile1.csv");
				writeToFile("Upload complete.");
				writeToFile("Please upload your local test CSV file.");
				readToFile("testFile1.csv");
				sharedState.testCSV = new TimeSeries("testFile1.csv");
				writeToFile("Upload complete.");
				}		
		}
		
		// Command class for algorithm settings
		public class algoSettingsCmd extends Command{
			//Constructor
			public algoSettingsCmd() {
				super("algorithm settings",2);
			}

			@Override
			public void execute() {
				String temp = "";
				sharedState.SimpleAnoD = new SimpleAnomalyDetector();
				writeToFile("The current correlation threshold is " + sharedState.SimpleAnoD.getCorOffset());
				writeToFile("Type a new threshold");
				temp = dio.readText();
				sharedState.SimpleAnoD.setCorOffset(Float.parseFloat(temp));
			}		
		}
		
		// Command class for anomaly detection
		public class detectAnoCmd extends Command{
			//Constructor
			public detectAnoCmd() {
				super("detect anomalies",3);
			}

			@Override
			public void execute() {
				sharedState.SimpleAnoD.learnNormal(sharedState.trainCSV);
				sharedState.reports = sharedState.SimpleAnoD.detect(sharedState.testCSV);
				writeToFile("anomaly detection complete.");
			}		
		}
		
		// Command class for results displaying
		public class dispResCmd extends Command{
			//Constructor
			public dispResCmd() {
				super("display results",4);
			}

			@Override
			public void execute() {
				for (AnomalyReport report : sharedState.reports) {
					writeToFile(report.timeStep + "\t" + report.description);
				}
				writeToFile("Done.");
			}		
		}
		
		// Command class for Upload and analyze
		public class uploadAndAnalyzeCmd extends Command{
			//Constructor
			public uploadAndAnalyzeCmd() {
				super("upload anomalies and analyze results",5);
			}

			@Override
			public void execute() {
				writeToFile("Please upload your local anomalies file.");
				String line = "";
				int a=0, b=0, P=0, FP=0, TP=0, taSum=0, N=0, curVal=0, nextVal,flag=0;
				HashMap<String, TreeSet<Integer>> temp = new HashMap<String, TreeSet<Integer>>(); //[("A-C", {73,74,75,76,77}), ("B,D", {33,44,55}), ("D,F", {36,37,38,57,58,59,66})]
				HashSet<Integer> extraSet = new HashSet<>(); // if there are extra not connected elements, this will hold them. at the end we'll add this to the rest.
				HashSet<HashSet<Integer>> goal = new HashSet<>();// goal : {{73-77}, {36-38}, {57,59}  {33,44,55,66}} (set of two sets.)
				TreeSet<Integer> sortedSet = new TreeSet<>();
				HashSet<Integer> tempSet = new HashSet<>();
				for (AnomalyReport report : sharedState.reports) { // filling temp.
					if (temp.get(report.description) == null)  // if temp is null
						temp.put(report.description, new TreeSet<Integer>()); // initialize it
						temp.get(report.description).add((int)report.timeStep); // insert the anomaly timestep
				}	// now temp looks like //[("A-C", {73,74,75,76,77}), ("B,D", {33,44,55})]
				
				
				//for each element ("A-C", {73,74,75,76,77})
				for (Map.Entry<String, TreeSet<Integer>> entry : temp.entrySet()) {
					TreeSet<Integer> p = entry.getValue();
					while(!p.isEmpty()) {
						if(p.size()==1 && flag ==1) // we have continuity , need to add last member.
						{
							if(Math.abs(p.first() - sortedSet.last())== 1)
								sortedSet.add(p.pollFirst());
							else
								extraSet.add(p.pollFirst());
						}
						else { // no continuity
							curVal = p.pollFirst();
							nextVal = p.first();
							if (Math.abs(curVal - nextVal) == 1) { // there's a continuity.
								flag = 1;
								sortedSet.add(curVal);
							}
							else // no continuity.
							extraSet.add(p.pollFirst());
						}
					}
					flag = 0;// reset flag
					//Storing a new set of Integer in goal set
					tempSet.addAll(sortedSet);
					sortedSet.removeAll(sortedSet);
					goal.add(new HashSet<Integer>(tempSet));
					tempSet.removeAll(tempSet);
				}
				
				//Adding the extra numbers (the ones without continuity) to goal set
				for (Integer integer : extraSet) {
					HashSet<Integer> temp2 = new HashSet<>();
					temp2.add(integer);
					goal.add(new HashSet<Integer>(temp2));						
				}
				
				
				int i = sharedState.testCSV.getNumOfRows();
				HashSet<Integer> toCompare = new HashSet<Integer>();// this set will hold the user input ranges
				//Reading user input for command 5.
				while (!(line = dio.readText()).equals("done")) 
				{
					a = Integer.parseInt(line.split(",")[0]); 
					b = Integer.parseInt(line.split(",")[1]);
					P++;
					taSum += (b-a)+1;
					
					// Filling the set
					for(int j=a;j<=b; j++)
					{
						toCompare.add(j);						
					}
				}
					

					for (HashSet<Integer> set : goal) {
						set.retainAll(toCompare);
						if(set.isEmpty())
							FP++;
						else
							TP++;
					}
				goal.removeAll(goal);
					
				DecimalFormat format = new DecimalFormat("0.0##");
				N = i - taSum;
				double TPR = (double) TP/P;
				double FPR = (double) FP/N;
				format.setRoundingMode(RoundingMode.FLOOR);
				writeToFile("Upload complete.");
				writeToFile("True Positive Rate: " + format.format(TPR));
				writeToFile("False Positive Rate: " + format.format(FPR));
			}		
		}
		
		// Command class for exiting:
		public class exitCmd extends Command{
			public exitCmd() {
				super("exit",6);
			}

			@Override
			public void execute() {
				flag = true;
			}		
		}
		
		//Reads input and paste it into a file
		public void readToFile(String fileName)
		{
			String test = "";
			try {
				PrintWriter out = new PrintWriter(new FileWriter(fileName));
				while(!(test = dio.readText()).equals("done")) {
				out.println(test);
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Simple write to file method
		void writeToFile(String string) {
			 try {
				BufferedWriter out = new BufferedWriter(new FileWriter("output.txt",true));
				
				out.write(string+"\n");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}


	
}
