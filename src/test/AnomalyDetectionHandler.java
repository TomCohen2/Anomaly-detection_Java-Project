package test;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import test.Commands.DefaultIO;
import test.Server.ClientHandler;

public class AnomalyDetectionHandler implements ClientHandler{
	
	
	public class SocketIO implements DefaultIO{
		Scanner in;
		PrintWriter out;
		
		public SocketIO(InputStream inStream, OutputStream outStream) {
				in = new Scanner(new BufferedReader(new InputStreamReader(inStream)));
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outStream)));

		}
		@Override
		public String readText() {
				return in.nextLine();

		}

		@Override
		public void write(String text) {
			out.print(text);
			out.flush();
			
		}

		@Override
		public float readVal() {
			return in.nextFloat();
		}

		@Override
		public void write(float val) {
			out.print(val);
			out.flush();
			
		}
		public void close() {
			in.close();
			out.close();
			
		}

	}

	@Override
	public void handleClient(InputStream inFromClient, OutputStream outToClient) {
		SocketIO sio = new SocketIO(inFromClient, outToClient);
		CLI cli = new CLI(sio);
		cli.start();
		sio.write("bye");
		sio.close();
	}


}
