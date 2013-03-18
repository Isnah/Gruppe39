package model.appointment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import model.Group;
import model.Person;

/**
 * @author Hans Olav Slotte
 * @author Endre Elvestad
 */
public class Meeting extends Appointment {
	private ArrayList<Person> attendees;
	private ArrayList<Group> groupAttendees;
	
	
	private ArrayList<Person> accepted;
	private ArrayList<Person> pending;
	private ArrayList<Person> declined;
	
	
	//SETTERS
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param accepted The list of persons that have accepted
	 */
	public void setAccepted(ArrayList<Person> accepted) {
		this.accepted = new ArrayList<Person>(accepted);
		
	}
	
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param declined The list of persons that have declined
	 */
	public void setDeclined(ArrayList<Person> declined) {
		this.declined = new ArrayList<Person>(declined);

	}
	
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param pending The list of persons that are pending
	 */
	public void setPending(ArrayList<Person> pending) {
		this.pending = new ArrayList<Person>(pending);

	}
	
	
	
	
	public void addToAccepted(Person person) {
		if(accepted.contains(person)) {
                    return;
                }

		

	}
	
	/**
	 * Checks whether a person is already in the declined list before adding
	 * them to it.
	 * @param person The person you want to add to the declined list
	 */
	public void addToDeclined(Person person) {
		if(declined.contains(person)) {
                    return;
                }
		

		

	}
	
	/**
	 * Checks whether a person is already in the pending list before adding
	 * them to it.
	 * @param person The person you want to add to the pending list
	 */
	public void addToPending(Person person) {
		if(pending.contains(person)) {
                    return;
                }
		}
	
	

	
	public Meeting(int id, Timestamp start, Timestamp end, String name, String descr, Person registeredBy, ArrayList<Person> initialAttendees, ArrayList<Group> initialGroups) {
		super(id, start, end, name, descr, registeredBy);
		attendees = initialAttendees;
		groupAttendees = initialGroups;
	}
	
	public Person getAttendee(int index) {
		return attendees.get(index);
	}
	
	public Group getGroupAttendee(int index) {
		return groupAttendees.get(index);
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
	
	/**
	 * @return A copy of the group attendee list. Group instances are editable
	 * as normal.
	 */
	public ArrayList<Group> getGroupAttendees() {
		return new ArrayList<Group>(groupAttendees);
	}
	
	public boolean isAttendingGroup(Group group) {
		return groupAttendees.contains(group);
	}
	
	public boolean isMeeting() {
		return true;
	}
	
	/**
	 * 
	 * @return A {@link java.util.Iterator} of the attendees list.
	 */
	public Iterator<Person> getAttendeeIterator() {
		return attendees.iterator();
	}
	
	/**
	 * 
	 * @return A {@link java.util.Iterator} of the group attendees list
	 */
	public Iterator<Group> getGroupAttendeeIterator() {
		return groupAttendees.iterator();
	}
}
