/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client; 

import gui.Login;
import gui.MainWindow;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Laxcor
 */
public class Main {
    
    private Person person;
    private MainWindow frame;
    private GregorianCalendar currentCalendar = new GregorianCalendar();
 
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
        person = new Person(2,"awesome@man.com", "Man", "Awesome");
    }
    /**
     * The main method for the application.
     * @param args 
     */
    public static void main(String[] args) {
        new Login(new Main()).setVisible(true);
        
    }
}
