package client;

import java.sql.Time;
import java.util.ArrayList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeEvent;


/**
 * @Author Endre Elvestad & Tobias Linkjendal
 */  

public class Person {

	private String firstName;
	private String lastName;
	private String email;
	private ArrayList<Appointment> appointments;
	private PropertyChangeSupport pcs;
	
	public final static String NAME = "name";
	public final static String EMAIL = "email";
	public final static String DESCR = "descr";
	public final static String ROOM = "room";
	
	
	public Person(String email, String lastName, String firstName) {
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
	}
	
	
	public String getFirstName() {
		return new String(firstName);
	}

	
	public void setFirstName(String firstName) {
		String oldName = this.firstName;
		this.firstName = firstName;
		
		pcs.firePropertyChange(NAME, oldName, firstName);
		
	}

	public String getLastName() {
		return new String(lastName);
	}



	public void setLastName(String lastName) {
		String oldName = this.lastName;
		this.lastName = lastName;
		
		pcs.firePropertyChange(NAME, oldName, lastName);
	}

	public String getEmail() {
		return new String(email);
	}
	  
	 public void setEmail(String email) {
		String oldMail = this.email;
		this.email = email;
		
		pcs.firePropertyChange(EMAIL, oldMail, email);
	}


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
