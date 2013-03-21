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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Meeting meeting;
    private Element command;
    private ConnectionThread ct;
    private ArrayList<Room> allRooms;
    
    private boolean loggedIn = false;
    /**
     * The general login method used by the login frame
     * @param username
     * @param password
     * @return 
     * @throws IOException 
     */
    public boolean login(String username, char[] password) throws IOException {
    	ct.login(username, password);
        
        try {
    		Thread.sleep(500);
    	} catch(InterruptedException ex) {
    	}
    	
        return loggedIn;
    }
    
    /**
     * Called by the main window to enable this method to communicate with the frame.
     * @param frame 
     */
    public void setFrame(MainWindow frame) {
        this.frame = frame;
        updateRooms();
        for (Meeting app : getAppointmentsForCurrentWeek()) {
            showAppointments(app);
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
    public ArrayList<Room> getRoomList() {
        return allRooms;
    }
    private void updateRooms() {
            allRooms = new ArrayList<Room>();
            ct.getAllRooms();
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
            showAppointments(app);
        }
    }
    
    /**
     * Called by the main window to close all connections before disposing.
     */
    public void logout() {
        ct.logout();
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
    	command = new Element("new");
        
        //FIX THIS!!!
        addAppointment(model);
	person.addAppointment(model);
        //REALLY NEEDS FIXING!
    }
    private void addAppointment(Meeting model) {
	    	try {
				ct.addAppointment(model);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
                updateRooms();
    }
    
    private void showAppointments(Meeting model) {
    	if (!person.getAllAppointments().contains(model)) {
	        person.addAppointment(model);
        }
    	if (getAppointmentsForCurrentWeek().contains(model)) {
            System.out.println("adding to frame");
            frame.addAppointment(model);
            System.out.println("out");
        }
    }

    public void editAppointment(Meeting model) {
    	command = new Element("update");
        addAppointment(model);
    }
    public void removeAppointment(Meeting model) {
    	command = new Element("delete");
        addAppointment(model);
        person.removeAppointment(model);
    }
    public Main() {

        run();
    }
    public void run()  {
    	int port = 1234;
    	String serverName = "localhost";
    	Socket client = null;
    	try {

        	client = new Socket(serverName, port);

    	}catch (IOException e) {

    	}
    	ct = new ConnectionThread(client, this);
    	new Thread(ct).start();
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
                                        System.out.println(input);
                                        
                                        if(input.equals("invalid_login")) continue;
                                        else if(input.equals("access_error_no_login")) continue;
                                        else if(input.equals("meeting_update_ok")) continue;
                                        else if(input.equals("meeting_update_failed_wrong_user")) continue;
                                        else if(input.equals("Malformed xml sent. Not able to assemble meeting.")) continue;
                                        else if(input.equals("Add statement not recognized")) continue;
                                        else if(input.equals("meeting_deletion_failed_wrong_user")) continue;
                                        else if(input.equals("Logging out of server")) continue;
                                        else if(input.equals("invalid_request")) continue;

					Document doc = builder.build(input, null);
					String type = XMLSerializer.getType(doc);

					Element root = doc.getRootElement();
					
					
					
					if (type.equals("return")) {
						Elements getElem = root.getChildElements();
						
						for(int i = 0; i < getElem.size(); i++) {
							Element el = getElem.get(i);
							String elementType = XMLSerializer.getType(el);

							
							if(elementType.equals("model")) {

								loggedIn = true;
								person = XMLSerializer.assembleCompletePerson(root.getFirstChildElement("model").getFirstChildElement("person"));
							}
							else if(elementType.equals("person_simple")) {
								person = XMLSerializer.assembleSimplePerson(el);
							}
							else if(elementType.equals("meeting")) {
								System.out.println(doc.toXML());
								meeting = XMLSerializer.assembleMeeting(el);
								showAppointments(meeting);
								
								
							}
							else if(elementType.equals("app_id")) {
								Element element = new Element("get");
								Element mtnEl = new Element("meeting");
								Element idEl = new Element("id");
								idEl.appendChild(el.getValue());
								mtnEl.appendChild(idEl);
								element.appendChild(mtnEl);
								Document getDoc = new Document(element);
								out.writeUTF(getDoc.toXML());
							}
							else if(elementType.equals("room")) {
                                                                Room room = XMLSerializer.assembleRoom(el);
                                                                allRooms.add(room);
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
			System.out.println(model.getRegisteredBy().getEmail());
			Element temp = XMLSerializer.meetingToXml(model);
			System.out.println(temp.getLocalName());
			
			command.appendChild(temp);
			
			Document send = new Document(command);
			System.out.println(send.toXML());
			out.writeUTF(send.toXML());
		}
		
		public void logout() {
			try {
				out.writeUTF("logout");
				in.close();
				out.close();
				client.close();
				loggedIn = false;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public ConnectionThread(Socket socket, Main main) {
			this.client = socket;
			this.main = main;
		}

		public ConnectionThread() {	}
		
		public void login(String user, char[] password) throws IOException {
			out.writeUTF(XMLSerializer.loginToXml(user, password).toXML());
		}

        private void getAllRooms() {
            try {
                Element root = new Element("get");
                Element temp = new Element("room_all");

                root.appendChild(temp);

                Document doc = new Document(root);
                out.writeUTF(doc.toXML());
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
