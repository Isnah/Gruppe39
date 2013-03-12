package model.appointment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import model.Person;
import model.Group;

/**
 * @author Hans Olav Slotte
 */
public class Meeting extends Appointment {
	private ArrayList<Person> attendees;
	private ArrayList<Group> groupAttendees;
	
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
