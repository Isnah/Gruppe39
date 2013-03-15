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

/**
 *
 * @author Laxcor
 */
public class Main {
    
    private Person person;
    private MainWindow frame;
    private GregorianCalendar currentCalendar = new GregorianCalendar();
    private Thread server;
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
        
        //FIX THIS!!!
        addAppointment(model);
        //REALLY NEEDS FIXING!
    }
    private void addAppointment(Meeting model) {
        person.addAppointment(model);
        if (getAppointmentsForCurrentWeek().contains(model)) {
            frame.addAppointment(model);
        }
    }

    public void editAppointment(Meeting model) {
        // TODO send server update
        addAppointment(model);
    }
    public void removeAppointment(Meeting model) {
        // TODO send server update
        person.removeAppointment(model);
    }
    public Main() {
        person = new Person("awesome@man.com", "Man", "Awesome");
        //run();
    }
    public void run()  {
    	int port;
    	String host;
    	Socket socket = null;
    	ConnectionThread thread;
    	try {
        	port = 1234;
        	host = "localhost";
        	socket = new Socket(host, port);
    	}catch (IOException e) {
    		System.err.println("No connection to the server");
    		System.exit(0);
    	}
    	thread = new ConnectionThread(socket, this);
    	new Thread(thread).start();
    }
    /**
     * The main method for the application.
     * @param args 
     */
    public static void main(String[] args) {
        new Login(new Main()).setVisible(true);
        
    }
    private class ConnectionThread implements Runnable {

    	private Socket socket;
    	private Main main;
    	
    	
		@Override
		public void run() {
			
		}
		
		public ConnectionThread(Socket socket, Main main) {
			this.socket = socket;
			this.main = main;
		}
    	
    }
}
