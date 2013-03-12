package client;

import java.sql.Time;
import java.util.ArrayList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;


//NB skal vi gjøre dette som vi har gjort i øvingen 


public class Person {

	/**
	 * @Author Endre Elvestad
	 */  
	

	private String firstName;
	private String lastName;
	private String email;
	private ArrayList<Appointment> appointments;
	private PropertyChangeSupport pcs;
	
	
	public Person(String email, String lastName, String firstName) {
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
	}
	
	
	public String getFirstName() {
		return new String(firstName);
	}

/**
	public void setFirstName(String firstName) {
		this.firstName = firstName;
		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_PROPERTY_NAME, oldName, name);
		propChangeSupp.firePropertyChange(event);
	}
*/

	public String getLastName() {
		return new String(lastName);
	}


	/**
	public void setLastName(String lastName) {
		this.lastName = lastName;
		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_PROPERTY_NAME, oldName, name);
		propChangeSupp.firePropertyChange(event);
	}

	 */
	public String getEmail() {
		return new String(email);
	}


	/** 
	  
	 public void setEmail(String email) {
		this.email = email;
		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_PROPERTY_NAME, oldName, name);
		propChangeSupp.firePropertyChange(event);
	}
	
	*/


	public ArrayList<Appointment> getAppointments(Time start, Time end) {
		ArrayList<Appointment> returnAppointments = new ArrayList<Appointment>();
		for(int i = 0; i < appointments.size(); ++i) {
			Appointment app = appointments.get(i);
			if(app.getStart() > start.getTime() && app.getStart() < end.getTime()) {
				returnAppointments.add(app);
			} else if(app.getStart() < end.getTime() && app.getEnd() > start.getTime()) {
				returnAppointments.add(app);
			}
		}
		return returnAppointments;
	}
	
	public void addAppointment(Appointment app) {
		appointments.add(app);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) 
	{
		pcs.addPropertyChangeListener(listener);
	}

}
