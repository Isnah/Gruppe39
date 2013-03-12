package server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;

import server.helpers.LoginCredentials;

import database.SQLTranslator;

public class ServerMain {
	
	private Connection connection;
	private ArrayList<ConnectedUserThread> connectThreads;
	
	public ServerMain() {
		connection = SQLTranslator.connectToDatabase();
	}
	
	public void run() throws IOException {
		int port = 1234;
		
		ServerSocket welcomeSocket = new ServerSocket(port);
		
		while(true) {
			Socket socket = welcomeSocket.accept();
			
		}
	}
	
	public void removeConnectedUser(ConnectedUserThread thread) {
		connectThreads.remove(thread);
	}
	
	public static void main(String[] args) {
		
	}
	
	
	// Private classes:
	
	/**
	 * Used for creating new communication threads when people connect
	 * to the server
	 * @author hansi
	 *
	 */
	private class ConnectedUserThread implements Runnable {
		
		private LoginCredentials credentials;
		private Socket socket;
		
		public ConnectedUserThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			
			
			
			
			removeConnectedUser(this);
		}

	}
}
