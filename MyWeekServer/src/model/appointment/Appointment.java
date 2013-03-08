package model.appointment;

import java.sql.Time;

import model.Room;

/**
 * 
 * @author Hans Olav Slotte
 */
public class Appointment {
	int id;
	private Time start, end;
	private String name, descr, roomDescr;
	Room room;
	
	/**
	 * @param id
	 * @param start When the appointment start
	 * @param end When the appointment end
	 * @param name The name of the appointment
	 * @param descr Short description of the appointment
	 */
	public Appointment(int id, Time start, Time end, String name, String descr) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.name = name; 
		this.descr = descr;
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
		return new String(roomDescr);
	}
	
	public void setRoomDescr(String roomDescr) {
		this.roomDescr = roomDescr;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
}
