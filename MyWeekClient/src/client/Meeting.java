package client;

import java.sql.Time;
import java.util.ArrayList;

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
                
                if (attendees == null) {
                    attendees = new ArrayList<>();
                }
                if (groupAttendees == null) {
                    groupAttendees = new ArrayList<>();
                }
	}
	
	
	//SETTERS
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param accepted The list of persons that have accepted
	 */
	public void setAccepted(ArrayList<Person> accepted) {
		//ArrayList<Person> oldAccepted = new ArrayList<Person>(this.accepted);
		this.accepted = new ArrayList<>(accepted);
		
		//pcs.firePropertyChange(LISTS, oldAccepted, accepted);
	}
	
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param declined The list of persons that have declined
	 */
	public void setDeclined(ArrayList<Person> declined) {
		//ArrayList<Person> oldDeclined = new ArrayList<Person>(this.declined);
		this.declined = new ArrayList<>(declined);
		
		//pcs.firePropertyChange(LISTS, oldDeclined, declined);
	}
	
	/**
	 * This is the function that should immediately set one of the three GUI-lists
	 * @param pending The list of persons that are pending
	 */
	public void setPending(ArrayList<Person> pending) {
		//ArrayList<Person> oldPending = new ArrayList<Person>(this.pending);
		this.pending = new ArrayList<>(pending);
		
		//pcs.firePropertyChange(LISTS, oldPending, pending);
	}
	
	
	//GETTERS
	
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
		return new ArrayList<>(attendees);
	}
	
	/**
	 * @return A copy of the group attendee list. Group instances are editable
	 * as normal.
	 */
	public ArrayList<Group> getGroupAttendees() {
		return new ArrayList<>(groupAttendees);
	}
	
	
	//ADDERS
	
	/**
	 * Checks whether a person is already in the attendees list before adding
	 * them to it.
	 * @param person The person you want to add to the attendees list
	 */
	public void addAttendee(Person person) {
		for (Person p : attendees) {
                    if (p.getEmail().equals(person.getEmail())) {
                        return;
                    }
                }
                ArrayList<Person> old = new ArrayList<>(attendees);
                attendees.add(person);
                pcs.firePropertyChange(LISTS, old, attendees);
        }
        
        public void addGroupAttendee(Group group) {
            for (Group g : groupAttendees) {
                if(groupAttendees.contains(group)) {
                    return;
                }
            }    
                
                ArrayList<Group> old = new ArrayList<>(groupAttendees);
                groupAttendees.add(group);
                pcs.firePropertyChange(LISTS, old, groupAttendees);
        }
	
	/**
	 * Checks whether a person is already in the accepted list before adding
	 * them to it.
	 * @param person The person you want to add to the accepted list
	 */
	public void addToAccepted(Person person) {
		if(accepted.contains(person)) {
                    return;
                }
		
		ArrayList<Person> oldAccepted = new ArrayList<>(accepted);
		accepted.add(person);
		
		pcs.firePropertyChange(LISTS, oldAccepted, accepted);
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
		
		ArrayList<Person> oldDeclined = new ArrayList<>(declined);
		declined.add(person);
		
		pcs.firePropertyChange(LISTS, oldDeclined, declined);
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
		
		ArrayList<Person> oldPending = new ArrayList<>(pending);
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
