package client;

import java.sql.Time;
import java.util.ArrayList;

import client.Appointment;
import client.Person;
import client.Group;

/**
 * @author Tobias Linkjendal
 */
public class Meeting extends Appointment {
	private ArrayList<Person> attendees;
	private ArrayList<Group> groupAttendees;
	
	//This is awesome for GUI
	private ArrayList<Person> accepted;
	private ArrayList<Person> pending;
	private ArrayList<Person> declined;
	
	public Meeting(int id, Time start, Time end, String name, String descr, Person registeredBy, ArrayList<Person> initialAttendees, ArrayList<Group> initialGroups) {
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
	 * Checks whether a person is already in the accepted list before adding
	 * them to it.
	 * @param person The person you want to add to the accepted list
	 */
	public void addToAccepted(Person person) {
		if(accepted.contains(person)) return;
		accepted.add(person);
	}
	
	/**
	 * Checks whether a person is already in the declined list before adding
	 * them to it.
	 * @param person The person you want to add to the declined list
	 */
	public void addToDeclined(Person person) {
		if(declined.contains(person)) return;
		declined.add(person);
	}
	
	/**
	 * Checks whether a person is already in the pending list before adding
	 * them to it.
	 * @param person The person you want to add to the pending list
	 */
	public void addToPending(Person person) {
		if(pending.contains(person)) return;
		pending.add(person);
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
}
