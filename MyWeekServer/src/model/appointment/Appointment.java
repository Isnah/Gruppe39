package model.appointment;

import java.sql.Time;
import java.util.ArrayList;

import model.Room;

/**
 * 
 * @author Hans Olav Slotte
 */
public class Appointment {
	int id;
	private Time start, end;
	String descr;
	Room room;
	
	private static ArrayList<Appointment> appointments = new ArrayList<Appointment>();
	
	/**
	 * Since there will be appointments made during validity checking etc., new
	 * Appointments WILL NOT be added to the static appointments list. This must
	 * be handled by Model when appointments are actually added to the working
	 * model. This can be done through the <code>addAppointment(Appointment app)
	 * </code> function.
	 * @param id
	 * @param start
	 * @param end
	 * @param descr Short description of the appointment
	 */
	public Appointment(int id, Time start, Time end, String descr) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.descr = descr;
	}
	
	/**
	 * 
	 * @return A copy of the static appointments list
	 */
	public static ArrayList<Appointment> getAppointments() {
		return new ArrayList<Appointment>(appointments);
	}
	
	/**
	 * This method adds the appointment app to the static appointments
	 * list. It automatically checks whether the given element is already
	 * in the list, but does not automatically check for whether the id
	 * is available.
	 * @param app
	 */
	public static void addAppointment(Appointment app) {
		if(!appointments.contains(app)) appointments.add(app);
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
}
