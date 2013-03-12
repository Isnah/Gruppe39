package server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.util.ArrayList;

import model.XMLSerializer;
import model.appointment.Appointment;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

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
			ConnectedUserThread thread = new ConnectedUserThread(socket);
			connectThreads.add(thread);
			new Thread(thread).start();
		}
	}
	
	public static void main(String[] args) {
		
	}
	
	
	// Private classes:
	
	/**
	 * Used for creating new communication threads when people connect
	 * to the server
	 * @author Hans Olav Slotte
	 *
	 */
	private class ConnectedUserThread implements Runnable {
		private LoginCredentials credentials;
		private boolean valid;
		private Socket socket;
		
		public ConnectedUserThread(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				while(true) {
					Builder builder = new Builder();
					Document doc = builder.build(in); // TODO: Look here if the input is not getting through. Might not be a good way of doing things
					String type = XMLSerializer.getType(doc);
					
					if(type.equals("login")) {
						credentials = XMLSerializer.assembleLogin(doc);
						valid = SQLTranslator.isValidEmailAndPassword(credentials.getUser(), credentials.getPassword(), connection);
						// TODO: Get all info about the logged in user from the database and send it
					} else if(!valid) {
						break;
					} else if(type.equals("get")) {
						Element root = doc.getRootElement();
						// find out what we want to get
					} else if(type.equals("update")) {
						Element root = doc.getRootElement();
						// find out what we need to update
					} else if(type.equals("new")) {
						Element root = doc.getRootElement();
						// find out what we need to create
					}
					if(!valid) {
						out.write("Invalid email or password");
					}
				}
			} catch(IOException ex) {
				System.err.println("IO exception.");
				System.err.println("Message: " + ex.getMessage());
			} catch(ParsingException ex) {
				System.err.println("Parsing Exception.");
				System.err.println("Message: " + ex.getMessage());
			}
			
			//connectThreads.remove(this);
		}
	}
}
