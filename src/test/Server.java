package test;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	final private int clientLimit = 50;
	private int numOfCurClients;
	volatile boolean stop;
	
	public interface ClientHandler{
		public InputStream in = null;
		public OutputStream out = null;
		// define...
		void handleClient(InputStream inFromClient, OutputStream outToClient);
	}

	
	public Server() {
		stop=false;
		numOfCurClients=0;
	}
	
	public Server(int port, ClientHandler ch)
	{
		this.numOfCurClients = 0;
		stop=false;
	}
	
	
	private void startServer(int port, ClientHandler ch) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
		} catch (IOException e3) {
			e3.printStackTrace();
		}
//		try {
//			server.setSoTimeout(999999);
//		} catch (SocketException e2) {
//			e2.printStackTrace();
//		}
		while(!stop) {
			Socket aClient = null;
			try {
				aClient = server.accept();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				ch.handleClient(aClient.getInputStream(), aClient.getOutputStream());
				
				aClient.getInputStream().close();
				aClient.getOutputStream().close();
				aClient.close();
			}catch (IOException e) {
				
			}
		}
		
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// runs the server in its own thread
	public void start(int port, ClientHandler ch) {
		PrintWriter pout = new PrintWriter(new BufferedOutputStream(ClientHandler.out));
		if (numOfCurClients==clientLimit) {
			pout.write("Too many users");
			pout.flush();
		}
		else
			new Thread(()->startServer(port,ch)).start();
	}

	
	public void stop() {
		stop=true;
		
	}
}
