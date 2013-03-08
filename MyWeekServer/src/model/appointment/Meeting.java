package model.appointment;

import java.sql.Time;
import java.util.ArrayList;

import model.Person;

/**
 * @author Hans Olav Slotte
 */
public class Meeting extends Appointment {
	private Person registeredBy;
	private ArrayList<Person> attendees;
	
	public Person getRegisteredBy() {
		return registeredBy;
	}
	
	public Meeting(int id, Time start, Time end, String descr, Person registeredBy, ArrayList<Person> initialAttendees) {
		super(id, start, end, descr);
		this.registeredBy = registeredBy;
		attendees = initialAttendees;
	}
	
	/**
	 * Checks if this person registered the meeting
	 * @param person
	 * @return True if person is who registered the Meeting
	 */
	public boolean wasRegisteredBy(Person person) {
		return person == registeredBy;
	}
	
	public Person getAttendee(int index) {
		return attendees.get(index);
	}
	
	/**
	 * Checks whether a person is already in the attendees list before adding
	 * them to it.
	 * @param person The person you want to add to the attendees list
	 */
	public void addAttendee(Person person) {
		if(attendees.contains(person)) return;
		attendees.add(person);
	}
	
	/**
	 * 
	 * @return A copy of the attendee list. Person instances in the list are still
	 * editable as normal.
	 */
	public ArrayList<Person> getAttendees() {
		return new ArrayList<Person>(attendees);
	}
	
	public boolean isAttending(Person person) {
		return attendees.contains(person);
	}
}
