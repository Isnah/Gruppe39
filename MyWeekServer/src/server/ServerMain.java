package server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

import model.Group;
import model.Person;
import model.Room;
import model.XMLSerializer;
import model.appointment.Meeting;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

import server.helpers.LoginCredentials;

import database.SQLTranslator;

public class ServerMain {
	
	private Connection connection;
	private ArrayList<ConnectedUserThread> connectThreads;
	private boolean running;
	
	public ServerMain() {
		connection = SQLTranslator.connectToDatabase();
	}
	
	public void run() throws IOException {
		int port = 1234;
		running = true;
		
		ServerSocket welcomeSocket = new ServerSocket(1234);
		
		while(running) {
			Socket socket = welcomeSocket.accept();
			ConnectedUserThread thread = new ConnectedUserThread(socket);
			connectThreads.add(thread);
			new Thread(thread).start();
		}
	}
	
	public static void main(String[] args){
		ServerMain main = new ServerMain();
		try{ 
			main.run();
		} catch(IOException ex) {
			System.err.println("IOException");
			System.err.println("Message: " + ex.getMessage());
		}
		
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
		private boolean valid = false;
		private Socket socket;
		private Timestamp prevNotificationQuery;
		
		public ConnectedUserThread(Socket socket) {
			prevNotificationQuery = new Timestamp(0);
			this.socket = socket;
		}
		
		public void run() {
			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				while(true) {
					Builder builder = new Builder();
					String input = in.readUTF();
					Document doc = builder.build(input, null);
					String type = XMLSerializer.getType(doc);
					
					
					if(type.equals("login")) {
						credentials = XMLSerializer.assembleLogin(doc);
						if(credentials == null) {
							out.writeUTF("Malformed xml in credentials. Try again.");
							continue;
						}
						valid = SQLTranslator.isValidEmailAndPassword(credentials.getUser(), credentials.getPassword(), connection);
						if(!valid){
							out.writeUTF("invalid_login");
							continue;
						}
						Person userPerson = SQLTranslator.getPersonWithAppointments(credentials.getUser(), connection);
						
						Document returnDoc = XMLSerializer.modelToXml(userPerson);
						
						out.writeUTF(returnDoc.toXML());
						
						// TODO: Get all info about the logged in user from the database and send it
					} else if(!valid) {
						out.writeUTF("access_error_no_login");
					} else if(type.equals("get")) {
						Element root = doc.getRootElement();
						Elements getElem = root.getChildElements();
						
						Element ret = new Element("return");
						
						for(int i = 0; i < getElem.size(); ++i) {
							Element el = getElem.get(i);
							String elementType = XMLSerializer.getType(el);
							
							if(elementType.equals("person")) {
								Person person = SQLTranslator.getPersonWithAppointments(el.getFirstChildElement("email").getValue(), connection);
								ret.appendChild(XMLSerializer.completePersonToXml(person));
							} else if(elementType.equals("person_simple")) {
								Person person = SQLTranslator.getPerson(el.getFirstChildElement("email").getValue(), connection);
								ret.appendChild(XMLSerializer.simplePersonToXml(person));
							} else if(elementType.equals("appointment")) {
								// TODO: current consensus is that appointment will not be used. Ignore for now
							} else if(elementType.equals("meeting")) {
								Meeting meeting = SQLTranslator.getMeeting(Integer.parseInt(el.getFirstChildElement("id").getValue()), connection);
								ret.appendChild(XMLSerializer.meetingToXml(meeting));
							} else if(elementType.equals("room")) {
								Room room = SQLTranslator.getRoom(Integer.parseInt(el.getFirstChildElement("id").getValue()), connection);
								ret.appendChild(XMLSerializer.roomToXmlWithSimpleAppointment(room));
							} else if(elementType.equals("group")) {
								Group group = SQLTranslator.getGroup(Integer.parseInt(el.getFirstChildElement("id").getValue()), connection);
								ret.appendChild(XMLSerializer.groupToXml(group));
							} else if(elementType.equals("group_simple")) {
								Group group = SQLTranslator.getGroup(Integer.parseInt(el.getValue()), connection);
								ret.appendChild(XMLSerializer.simpleGroupToXml(group));
							} else if(elementType.equals("notifications_all")) {
								// get all notifications since the previous query by this user
							}
						}
						
						Document retDoc = new Document(ret);
						
						out.writeUTF(retDoc.toXML());
						
					} else if(type.equals("update")) {
						Element root = doc.getRootElement();
						
						Elements elements = root.getChildElements();
						
						for(int i = 0; i < elements.size(); ++i) {
							
							Element el = elements.get(i);
							
							if(XMLSerializer.getType(el).equals("Meeting")) {
								Meeting mtn = XMLSerializer.assembleMeeting(el);
								
								boolean wasRegisteredByUser;
								
								Meeting realMtn = SQLTranslator.getMeeting(mtn.getID(), connection);
								
								if(realMtn == null) {
									System.err.println("Error getting meeting.");
									System.err.println("ID: " + mtn.getID());
									continue;
								}
								
								Person registeredBy = realMtn.getRegisteredBy();
								
								wasRegisteredByUser = registeredBy.getEmail().equals(credentials.getUser());
								
								if(wasRegisteredByUser) {
									SQLTranslator.updateAppointmentOrMeeting(mtn, connection);
									out.writeUTF("meeting_update_ok");
								} else {
									out.writeUTF("meeting_update_failed_wrong_user");
								}
								
								
								
							} else if(XMLSerializer.getType(el).equals("Appointment")) {
								/*
								 *  TODO: Consensus is that appointments will not be used as anything
								 *  other than meeting, so this will not be in use for now
								 */
							}
							
							
						}
					} else if(type.equals("new")) {
						Element root = doc.getRootElement();
						Elements elements = root.getChildElements();
						
						for(int i = 0; i < elements.size(); ++i) {
							Element el = elements.get(i);
							
							String elementType = XMLSerializer.getType(el);
							
							if(elementType.equals("appointment")) {
								// TODO: currently not used due to consensus that meeting will always be used instead
							} else if(elementType.equals("meeting")) {
								Meeting meeting = XMLSerializer.assembleMeeting(el);
								
								if(meeting == null) {
									out.writeUTF("Malformed xml sent. Not able to assemble meeting.");
									break;
								}
								
								int id = SQLTranslator.addMeeting(meeting, connection);
								
								Document ret = new Document(XMLSerializer.idToXml(id, 'm'));
								
								out.writeUTF(ret.toXML());
							} else if(elementType.equals("alarm")) { 
								// make a new alarm and add it to the database
							} else {
								out.writeUTF("Add statement not recognized");
							}
						}
						
					}
					else if(type.equals("delete")) {
						Element root = doc.getRootElement();
						
						Elements elements = root.getChildElements();
						
						for(int i = 0; i < elements.size(); ++i) {
							Element el = elements.get(i);
							
							String elementType = XMLSerializer.getType(el);
							
							if(elementType.equals("meeting")) {
								Meeting mtn = XMLSerializer.assembleMeeting(el);
								
								Meeting realMtn = SQLTranslator.getMeeting(mtn.getID(), connection);
								if(realMtn == null) {
									System.err.println("Unable to get meeting");
									System.err.println("ID: " + mtn.getID());
									continue;
								}
								
								Person registeredBy = realMtn.getRegisteredBy();
								
								boolean registeredByUser = registeredBy.getEmail().equals(credentials.getUser());
								
								if(registeredByUser) {
									SQLTranslator.deleteAppointmentOrMeeting(mtn, connection);
								} else {
									out.writeUTF("meeting_deletion_failed_wrong_user");
								}
							}
						}
						
					} else if (type.equals("logout")) {
						out.writeUTF("Logging out of server");
						break;
					} else {
						out.writeUTF("invalid_request");
					}
				}
			} catch(IOException ex) {
				System.err.println("IO exception.");
				System.err.println("Message: " + ex.getMessage());
			} catch(ParsingException ex) {
				System.err.println("Parsing Exception.");
				System.err.println("Message: " + ex.getMessage());
			}
			
			connectThreads.remove(this);
		}
	}
}
