/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client; 

import gui.Login;
import gui.MainWindow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import client.XMLSerializer;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import nu.xom.Element;
import nu.xom.Elements;

/**
 *
 * @author Laxcor
 */
public class Main {
    
    private Person person;
    private MainWindow frame;
    private GregorianCalendar currentCalendar = new GregorianCalendar();
    private Thread server;
    private Meeting meeting;
    private Element command;
    private ConnectionThread ct = new ConnectionThread();
    /**
     * The general login method used by the login frame
     * @param username
     * @param password
     * @return 
     */
    public boolean login(String username, char[] password) {
        // TODO fix the login check
        return true;
    }
    
    /**
     * Called by the main window to enable this method to communicate with the frame.
     * @param frame 
     */
    public void setFrame(MainWindow frame) {
        this.frame = frame;
        
        for (Meeting app : getAppointmentsForCurrentWeek()) {
            addAppointment(app);
        }
    }
    
    private ArrayList<Meeting> getAppointmentsForCurrentWeek() {
        
        GregorianCalendar first = (GregorianCalendar)currentCalendar.clone();
        first.add(Calendar.DAY_OF_WEEK, 
              first.getFirstDayOfWeek() - first.get(Calendar.DAY_OF_WEEK));

        // and add six days to the end date
        GregorianCalendar last = (GregorianCalendar) first.clone();
        last.add(Calendar.DAY_OF_YEAR, 6);
        return person.getAppointments(new Time(first.getTimeInMillis()),new Time(last.getTimeInMillis()));
    }
    /**
     * Sends an alarm to the frame
     * @param alarm 
     */
    private void sendAlarm(Alarm alarm) {
        frame.fireAlarm(alarm);
    }
    public ArrayList<Room> getAllRooms() {
        return new ArrayList<Room>();
    }
    /**
     * Used by the main window to get its model
     * @return 
     */
    public String getPersonName() {
        return person.getFirstName() + " " + person.getLastName();
    }
    public void setCurrentWeek(int week) {
        currentCalendar.set(Calendar.WEEK_OF_YEAR, week);
        
        for (Meeting app : getAppointmentsForCurrentWeek()) {
            addAppointment(app);
        }
    }
    
    /**
     * Called by the main window to close all connections before disposing.
     */
    public void logout() {
        // TODO This needs a connection
    }
    
    public Meeting getMeeting(int id) {
        // TODO ask the server
        
        //FOR OFFLINE SERVICE
        return person.getMeeting(id);
        //END OFFLINE
    }
    
    public String getEmail() {
    	return person.getEmail(); 
    }
    public Person getPersonByEmail(String email) {
        // TODO return person from server
        return new Person(email, "name", email);
    }
    public Group getGroupById(int id) {
        // TODO return group from server
        return new Group(8, "a random group");
    }
    
    public void newAppointment(Meeting model) {
        //TODO send appointment to the server
    	command = new Element("new");
        
        //FIX THIS!!!
        addAppointment(model);
        //REALLY NEEDS FIXING!
    }
    private void addAppointment(Meeting model) {
    	if (!person.getAllAppointments().contains(model)) {
	    	try {
				ct.addAppointment(model);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        person.addAppointment(model);
	        if (getAppointmentsForCurrentWeek().contains(model)) {
	            frame.addAppointment(model);
	        }
    	}
    }

    public void editAppointment(Meeting model) {
        // TODO send server update
    	command = new Element("update");
    	
        addAppointment(model);
    }
    public void removeAppointment(Meeting model) {
        // TODO send server update
    	command = new Element("delete");
    	
        person.removeAppointment(model);
    }
    public Main() {
        person = new Person("awesome@man.com", "Man", "Awesome");
        //run();
    }
    public void run()  {
    	int port = 1234;
    	String serverName = "localhost";
    	Socket client = null;
    	ConnectionThread thread;
    	try {
    		System.out.println("Connecting to " + serverName + " on port " + port);
        	client = new Socket(serverName, port);
        	System.out.println("Connected to " + client.getRemoteSocketAddress());
    	}catch (IOException e) {
    		System.err.println("No connection to the server");
    		System.exit(0);
    	}
    	thread = new ConnectionThread(client, this);
    	new Thread(thread).start();
    }
    /**
     * The main method for the application.
     * @param args 
     */
    public static void main(String[] args) {
    	try {
    		for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
    			if ("Windows".equals(info.getName())) {
    				javax.swing.UIManager.setLookAndFeel(info.getClassName());
    				break;
    			}
    		}
    	} catch (Exception e) {
    		
    	}
    	new Login(new Main()).setVisible(true);
    }
    private class ConnectionThread implements Runnable {

    	private Socket client;
    	private Main main;
    	private DataInputStream in;
    	private DataOutputStream out;
    	
		@Override
		public void run() {
			try {
				in = new DataInputStream(client.getInputStream());
				out = new DataOutputStream(client.getOutputStream());
				
				while(true) {
					Builder builder = new Builder();
					String input = in.readUTF();
					Document doc = builder.build(input, null);
					String type = XMLSerializer.getType(doc);
					Element root = doc.getRootElement();
					
					if(type.equals("model")) {
						person = XMLSerializer.assembleCompletePerson(root);
					}
					else if (type.equals("return")) {
						Elements getElem = root.getChildElements();
						
						for(int i = 0; i < getElem.size(); i++) {
							Element el = getElem.get(i);
							String elementType = XMLSerializer.getType(el);
							
							if(elementType.equals("person_simple")) {
								person = XMLSerializer.assembleSimplePerson(el);
							}
							else if(elementType.equals("meeting")) {
								meeting = XMLSerializer.assembleMeeting(el);
							}
							else if(elementType.equals("room")) {
								
							}
							else if(elementType.equals("group")) {
								
							}
						}
					}
					else {
						System.out.println(input);
					}
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void addAppointment(Meeting model) throws IOException {
			Element temp = new Element("a"); 
			temp = XMLSerializer.meetingToXml(model);
			Document send = new Document(temp);
			out.writeUTF(send.toXML());
		}
		
		public ConnectionThread(Socket socket, Main main) {
			this.client = socket;
			this.main = main;
		}

		public ConnectionThread() {	}
    }
}
