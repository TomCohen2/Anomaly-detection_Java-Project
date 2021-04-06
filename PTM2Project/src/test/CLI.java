package test;

import java.util.ArrayList;

import test.Commands.Command;
import test.Commands.DefaultIO;

public class CLI {
	//Members
	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;


	//Constructor
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		// Adding all the commands.
		commands.add(c.addCommand(c.new UploadCmd()));
		commands.add(c.addCommand(c.new algoSettingsCmd()));
		commands.add(c.addCommand(c.new detectAnoCmd()));
		commands.add(c.addCommand(c.new dispResCmd()));
		commands.add(c.addCommand(c.new uploadAndAnalyzeCmd()));
		commands.add(c.addCommand(c.new exitCmd()));

	}
	
	// Starts the server
	public void start() {
		int userMenuInput=0;
		while(c.flag == false) {
			c.writeToFile("Welcome to the Anomaly Detection Server.\nPlease choose an option:");
			c.printMenu();
			userMenuInput = Integer.parseInt(dio.readText()); // Reading input
			c.executeCommand(userMenuInput);
		} 
		
	}


	
}
