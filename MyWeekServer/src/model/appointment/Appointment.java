package model.appointment;

import java.sql.Time;

import model.Person;
import model.Room;

/**
 * 
 * @author Hans Olav Slotte
 */
public class Appointment {
	int id;
	private Time start, end;
	private String descr;
	private Room room;
	private Person registeredBy;
	
	/**
	 * @param id
	 * @param start
	 * @param end
	 * @param descr Short description of the appointment
	 */
	public Appointment(int id, Time start, Time end, String descr, Person registeredBy) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.descr = descr;
		this.registeredBy = registeredBy;
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
	 * @return The start time in milliseconds from 1/1/1970 00:00:00 GMT
	 */
	public long getEnd() {
		return end.getTime();
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
}
