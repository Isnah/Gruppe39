package model.appointment;

import java.sql.Time;

import model.Person;
import model.Room;

/**
 * 
 * @author Hans Olav Slotte
 */
public class Appointment {
	private int id;
	private Time start, end;
	private Room room;
	private Person registeredBy;
	private String name, descr, roomDescr;
	
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
		
	}
	
	public void setStart(Time start) {
		this.start = start;
		end = new Time(start.getTime() + 1);
	}
	
	/**
	 * 
	 * @param start The time in milliseconds from 1/1/1979 00:00:00 GMT
	 */
	public void setStart(long start) {
		this.start = new Time(start);
		end = new Time(start + 1);
	}
	
	/**
	 * 
	 * @return The start time in milliseconds from 1/1/1970 00:00:00 GMT
	 */
	public long getStart() {
		return start.getTime();
	}
	
	public void setEnd(Time end) throws Exception {
		if(end.getTime() < start.getTime()) throw new Exception("INVALID! End earlier than start.");
		this.end = end;
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
		this.name = name;
	}
	
	/**
	 * 
	 * @return A copy of the description
	 */
	public String getDescr() {
		return new String(descr);
	}
	
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	
	/**
	 * 
	 * @return A copy of the room description
	 */
	public String getRoomDescr(){
		if(roomDescr == null) return null;
		return new String(roomDescr);
	}
	
	public void setRoomDescr(String roomDescr) {
		this.roomDescr = roomDescr;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
	
	/**
	 * Returns the room of this appointment, <b>not a copy!</b>
	 * @return
	 */
	public Room getRoom() {
		return room;
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
	 * @return String of the time format for when the meeting is planned: xx:xx-xx:xx
	 */
	public String getTimeFormat(){
		String timeFormat = start.toString().substring(0, 5); //xx:xx
		timeFormat += "-" + end.toString().substring(0, 5); //-xx:xx
		return timeFormat;
	}
	
	public boolean isMeeting() {
		return false;
	}
}
