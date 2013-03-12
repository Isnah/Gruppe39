/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import gui.Login;

/**
 *
 * @author Laxcor
 */
public class Main {
	
	private Person person;
 
    public boolean login(String username, char[] password) {
    	System.out.println("Logging in");
		return true;
    }
    
    
    public static void main(String[] args) {
		new Login(new Main()).setVisible(true);
	}
}
