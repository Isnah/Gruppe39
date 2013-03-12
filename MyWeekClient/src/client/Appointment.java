package client;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
//import java.beans.PropertyChangeEvent;

import client.Person;
import client.Room;


/**
 * 
 * @author Tobias Linkjendal
 */

public class Appointment {
	private int id;
	private Time start, end;
	private Room room;
	private String name, descr, roomDescr;
	private Person registeredBy;
	private ArrayList<Alarm> alarms;
	private PropertyChangeSupport pcs;
	
	public final static String START = "start";
	public final static String END = "end";
	public final static String NAME = "name";
	public final static String DESCR = "descr";
	public final static String ROOM = "room";
	
	
	/**
	 * @param id The id of the appointment. This is the only place this is set, only
	 * important and correct for appointments that have been fetched from the
	 * database, upon insertion into the database, it will be ignored due to the
	 * auto increment. <b>QUESTION: Do we want to make this more important and use it
	 * as a check during insertion to avoid duplicates?</b>
	 * @param start When the appointment starts
	 * @param end When the appointment ends
	 * @param name The name of the appointment
	 * @param descr Short description of the appointment
	 */
	public Appointment(int id, Time start, Time end, String name, String descr, Person registeredBy) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.name = name; 
		this.descr = descr;
		this.registeredBy = registeredBy;
		
		this.roomDescr = null; //This might never ever be set
		this.room = null; //This one as well
		this.alarms = new ArrayList<Alarm>();
		
		pcs = new PropertyChangeSupport(this);
	}
	
	/**
	 * Decides if this appointment should come before/after another appointment
	 * @param other Is another Appointment that this is compared to
	 * @return int -1: This comes before. 1: This comes after. 0: This is equal
	 */
	public int compareTo(Appointment other){
		if (other.getStart() > start.getTime()) return -1;
		else if (other.getStart() < start.getTime()) return 1;
		else if (other.getEnd() > end.getTime()) return -1;
		else if (other.getEnd() < end.getTime()) return 1;
		else return 0;
	}
	
	public void setStart(Time start) {
		Time oldStart = this.start;
		this.start = start;
		
		pcs.firePropertyChange(START, oldStart, start);
	}
	
	/**
	 * 
	 * @param start The time in milliseconds from 1/1/1979 00:00:00 GMT
	 */
	public void setStart(long start) {
		Time oldStart = this.start;
		this.start = new Time(start);
		
		pcs.firePropertyChange(START, oldStart, this.start);
	}
	
	/**
	 * 
	 * @return The start time in milliseconds from 1/1/1970 00:00:00 GMT
	 */
	public long getStart() {
		return start.getTime();
	}
	
	public void setEnd(Time end) throws Exception {
		Time oldEnd = this.end;
		if(end.getTime() < start.getTime()) throw new Exception("INVALID! End earlier than start.");
		this.end = end;
		
		pcs.firePropertyChange(END, oldEnd, end);
	}
	
	/**
	 * 
	 * @return The end time in milliseconds from 1/1/1970 00:00:00 GMT
	 */
	public long getEnd() {
		return end.getTime();
	}
	
	
	/**
	 * 
	 * @return A copy of the appointment name
	 */
	public String getName() {
		return new String(name);
	}
	
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		
		pcs.firePropertyChange(NAME, oldName, name);
	}
	
	/**
	 * 
	 * @return A copy of the description
	 */
	public String getDescr() {
		return new String(descr);
	}
	
	public void setDescr(String descr) {
		String oldDescr = this.descr; 
		this.descr = descr;
		
		pcs.firePropertyChange(DESCR, oldDescr, descr);
	}
	
	
	/**
	 * 
	 * @return A copy of the room description
	 */
	public String getRoomDescr(){
		return new String(roomDescr);
	}
	
	public void setRoomDescr(String roomDescr) {
		String oldRoomDescr = this.roomDescr;
		this.roomDescr = roomDescr;
		
		pcs.firePropertyChange(ROOM, oldRoomDescr, roomDescr);
	}
	
	/**
	 * Returns the room of this appointment, <b>not a copy!</b>
	 * @return
	 */
	public Room getRoom() {
		return room;
	}
	
	public void setRoom(Room room) {
		Room oldRoom = this.room;
		this.room = room;
		
		pcs.firePropertyChange(ROOM, oldRoom, room);
	}
	
	public Person getRegisteredBy() {
		return registeredBy;
	}
	
	/**
	 * Checks if this person registered the appointment
	 * @param person
	 * @return True if person is who registered the Appointment
	 */
	public boolean wasRegisteredBy(Person person) {
		return person == registeredBy;
	}
	
	public int getID() {
		return id;
	}
	
	/**
	 * This function is usefull in Room.java and to show info about a meeting
	 * @return String of the time format for when the meeting is planned: xx:xx-xx:xx
	 */
	public String getTimeFormat(){
		String timeFormat = start.toString().substring(0, 5); //xx:xx
		timeFormat += "-" + end.toString().substring(0, 5); //-xx:xx
		return timeFormat;
	}
	
	/**
	 * This function is usefull to show info about a meeting
	 * @return String of the time format for when the meeting is planned: xx/xx/xxxx
	 */
	public String getDateFormat(){
		Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(start.getTime());
    	
		String dateFormat = c.get(Calendar.DATE) + "/" + 
							(c.get(Calendar.MONTH) + 1) + "/" +
							c.get(Calendar.YEAR);
		return dateFormat;
	}
	
	public void addAlarm(Alarm a){
		alarms.add(a);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) 
	{
		pcs.addPropertyChangeListener(listener);
	}
}
