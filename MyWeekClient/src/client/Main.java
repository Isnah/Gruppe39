/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import gui.Login;
import gui.MainWindow;

/**
 *
 * @author Laxcor
 */
public class Main {
    
    private Person person;
    private MainWindow frame;
 
    /**
     * The general login method used by the login frame
     * @param username
     * @param password
     * @return 
     */
    public boolean login(String username, char[] password) {
        // TODO fix the login check
    	System.out.println("Logging in");
		return true;
    }
    
    /**
     * Called by the main window to enable this method to communicate with the frame.
     * @param frame 
     */
    public void setFrame(MainWindow frame) {
        this.frame = frame;
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
    public Person getPerson() {
        return person;
    }
    
    /**
     * Called by the main window to close all connections before disposing.
     */
    public void logout() {
        // TODO This needs a connection
    }
    
    /**
     * The main method for the application.
     * @param args 
     */
    public static void main(String[] args) {
        new Login(new Main()).setVisible(true);
    }
}
