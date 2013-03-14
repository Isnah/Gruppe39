package client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Time;
import java.util.ArrayList;


/**
 * @author Endre Elvestad 
 * @kudos Tobias Linkjendal
 */  

public class Person {

        private int id;
	private String firstName;
	private String lastName;
	private String email;
	private ArrayList<Meeting> appointments;
        private ArrayList<Notification> notifications;
	private PropertyChangeSupport pcs;
	
	public final static String NAME = "name";
	public final static String EMAIL = "email";
	
	
	public Person(int id, String email, String lastName, String firstName) {
                this.id = id;
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
		
		this.appointments = new ArrayList<Meeting>();
		
		pcs = new PropertyChangeSupport(this);
	}
	
	
	//SETTERS
	
	public void setFirstName(String firstName) {
		String oldName = this.firstName;
		this.firstName = firstName;
		
		pcs.firePropertyChange(NAME, oldName, firstName);
		
	}
	
	public void setLastName(String lastName) {
		String oldName = this.lastName;
		this.lastName = lastName;
		
		pcs.firePropertyChange(NAME, oldName, lastName);
	}
	
	public void setEmail(String email) {
		String oldMail = this.email;
		this.email = email;
		
		pcs.firePropertyChange(EMAIL, oldMail, email);
	}
	
	
	//GETTERS
	
        /**
         * 
         * @return int with the persons id
         */
        public int getId() {
            return id;
        }
	/**
	 * @return String with this persons first name
	 */
	public String getFirstName() {
		return new String(firstName);
	}

	/**
	 * @return String with this persons last name
	 */
	public String getLastName() {
		return new String(lastName);
	}

	/**
	 * @return String with this persons email
	 */
	public String getEmail() {
		return new String(email);
	}
	
        /**
         * 
         * @return ArrayList with all the persons notifications
         */
        public ArrayList<Notification> getNotifications() {
            return notifications;
        }
	/**
	 * This function is usefull to show info about a meeting
	 * @param start The time you start looking for appointments
	 * @param end The time you End looking for appointments
	 * @return ArrayList with the appointments for this person in the time period
	 */
	public ArrayList<Meeting> getAppointments(Time start, Time end) {
		ArrayList<Meeting> returnAppointments = new ArrayList<Meeting>();
		for(int i = 0; i < appointments.size(); ++i) {
			Meeting app = appointments.get(i);
			if(app.getStart() > start.getTime() && app.getStart() < end.getTime()) {
				returnAppointments.add(app);
			} else if(app.getStart() < end.getTime() && app.getEnd() > start.getTime()) {
				returnAppointments.add(app);
			}
		}
		return returnAppointments;
	}
	
	
	//ADDERS lol
	
        public void addNotification(Notification not) {
            notifications.add(not);
        }
	public void addAppointment(Meeting app) {
            appointments.add(app);
	} 
        public void removeAppointment(Meeting app) {
            appointments.remove(app);
        }
	 
	public void addPropertyChangeListener(PropertyChangeListener listener) 
	{
		pcs.addPropertyChangeListener(listener);
	}

}
