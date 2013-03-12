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
	
	//This is awesome for GUI, they contain only persons
	private ArrayList<Person> accepted;
	private ArrayList<Person> pending;
	private ArrayList<Person> declined;
	
	public Meeting(int id, Time start, Time end, String name, String descr, Person registeredBy, ArrayList<Person> initialAttendees, ArrayList<Group> initialGroups) {
		super(id, start, end, name, descr, registeredBy);
		attendees = initialAttendees;
		groupAttendees = initialGroups;
	}
	
	
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param accepted The list of persons that have accepted
	 */
	public void setAccepted(ArrayList<Person> accepted) {
		//ArrayList<Person> oldAccepted = new ArrayList<Person>(this.accepted);
		this.accepted = new ArrayList<Person>(accepted);
		
		//pcs.firePropertyChange(LISTS, oldAccepted, accepted);
	}
	
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param declined The list of persons that have declined
	 */
	public void setDeclined(ArrayList<Person> declined) {
		//ArrayList<Person> oldDeclined = new ArrayList<Person>(this.declined);
		this.declined = new ArrayList<Person>(declined);
		
		//pcs.firePropertyChange(LISTS, oldDeclined, declined);
	}
	
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param pending The list of persons that are pending
	 */
	public void setPending(ArrayList<Person> pending) {
		//ArrayList<Person> oldPending = new ArrayList<Person>(this.pending);
		this.pending = new ArrayList<Person>(pending);
		
		//pcs.firePropertyChange(LISTS, oldPending, pending);
	}
	
	public Person getAttendee(int index) {
		return attendees.get(index);
	}
	
	public Group getGroupAttendee(int index) {
		return groupAttendees.get(index);
	}
	
	/**
	 * 
	 * @return A copy of the attendee list. Person instances in the list are still
	 * editable as normal.
	 */
	public ArrayList<Person> getAttendees() {
		return new ArrayList<Person>(attendees);
	}
	
	/**
	 * @return A copy of the group attendee list. Group instances are editable
	 * as normal.
	 */
	public ArrayList<Group> getGroupAttendees() {
		return new ArrayList<Group>(groupAttendees);
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
	 * Checks whether a person is already in the accepted list before adding
	 * them to it.
	 * @param person The person you want to add to the accepted list
	 */
	public void addToAccepted(Person person) {
		if(accepted.contains(person)) return;
		
		ArrayList<Person> oldAccepted = new ArrayList<Person>(accepted);
		accepted.add(person);
		
		pcs.firePropertyChange(LISTS, oldAccepted, accepted);
	}
	
	/**
	 * Checks whether a person is already in the declined list before adding
	 * them to it.
	 * @param person The person you want to add to the declined list
	 */
	public void addToDeclined(Person person) {
		if(declined.contains(person)) return;
		
		ArrayList<Person> oldDeclined = new ArrayList<Person>(declined);
		declined.add(person);
		
		pcs.firePropertyChange(LISTS, oldDeclined, declined);
	}
	
	/**
	 * Checks whether a person is already in the pending list before adding
	 * them to it.
	 * @param person The person you want to add to the pending list
	 */
	public void addToPending(Person person) {
		if(pending.contains(person)) return;
		
		ArrayList<Person> oldPending = new ArrayList<Person>(pending);
		pending.add(person);
		
		pcs.firePropertyChange(LISTS, oldPending, pending);
	}
	
	public boolean isAttending(Person person) {
		return attendees.contains(person);
	}
	
	public boolean isAttendingGroup(Group group) {
		return groupAttendees.contains(group);
	}
}
