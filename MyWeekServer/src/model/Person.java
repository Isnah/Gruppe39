package model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;

import model.appointment.Appointment;

/**
 * 
 * @author Hans Olav Slotte
 *
 */
public class Person {
	private String lastName, firstName, email;
	private ArrayList<Appointment> appointments;
	
	/**
	 * Constructor for the model.Person object. This is currently the only time you can
	 * set these parameters in a Person.
	 * @param email This persons email
	 * @param lastName The persons last name
	 * @param firstName The persons first name
	 * @param 
	 */
	public Person(String email, String lastName, String firstName, ArrayList<Appointment> appointments) {
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
		this.appointments = appointments;
	}
	
	public Person(String email, String lastName, String firstName) {
		this(email, lastName, firstName, new ArrayList<Appointment>());
	}
	
	/**
	 * 
	 * @return A copy of the email.
	 */
	public String getEmail() {
		return new String(email);
	}
	
	/**
	 * 
	 * @return A copy of the last name
	 */
	public String getLastName()	{
		return new String(lastName);
	}
	
	/**
	 * 
	 * @return A copy of the first name
	 */
	public String getFirstName() {
		return new String(firstName);
	}
	
	/**
	 * Returns this persons appointments from start to end
	 * @param start The start of the period you want the appointments of
	 * @param end The end of the period you want the appointments of
	 * @return
	 */
	public ArrayList<Appointment> getAppointments(Time start, Time end) {
		ArrayList<Appointment> returnAppointments = new ArrayList<Appointment>();
		for(int i = 0; i < appointments.size(); ++i) {
			Appointment app = appointments.get(i);
			if(app.getStart() >= start.getTime() && app.getStart() <= end.getTime()) {
				returnAppointments.add(app);
			} else if(app.getStart() <= end.getTime() && app.getEnd() >= start.getTime()) {
				returnAppointments.add(app);
			}
		}
		return returnAppointments;
	}
	
	public void addAppointment(Appointment app) {
		appointments.add(app);
	}
	
	public Iterator<Appointment> getAppointmentIterator() {
		return appointments.iterator();
	}
}
