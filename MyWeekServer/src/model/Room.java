package model;

import java.util.ArrayList;

import model.appointment.Appointment;

/**
 * 
 * @author Hans Olav Slotte
 *
 */
public class Room {
	private int id;
	private int space;
	private String name;
	private ArrayList<Appointment> appointments;
	
	/**
	 * @param id
	 * @param space
	 * @param name
	 */
	public Room(int id, int space, String name) {
		this.id = id;
		this.space = space;
		this.name = name;
	}
	/**
	 * DO NOT USE! Under construction. Needs model change events.
	 * @param space
	 * @param name
	 */
	public Room(int space, String name) {
		// TODO 
	}
	
	public int getID() {
		return id;
	}
	
	public int getSpace() {
		return space;
	}
	
	/**
	 * 
	 * @return A copy of the name
	 */
	public String getName() {
		return new String(name);
	}
	
	/**
	 * Method for room availibility
	 * @param start The start time of the desired frame in milliseconds
	 * @param end End time
	 * @return True if available, false if unavailable
	 */
	public boolean isAvailable(long start, long end) {
		// TODO: Possibly consider sorting the appointments list on start time
		//       and doing a binary search to see if the room is available at
		//       the time
		for(int i = 0; i < appointments.size(); ++i) {
			Appointment app = appointments.get(i);
			if(app.getStart() > start && app.getStart() < end) return false;
			if(app.getEnd() < end && app.getEnd() > start) return false;
		}
		
		return true;
	}
	
	/**
	 * This adds an appointment to the room if it is available
	 * @param app The appointment you wish to add.
	 * @return Returns true if it is able to add the appointment to the room.
	 * False otherwise.
	 */
	public boolean addAppointment(Appointment app) {
		if(!isAvailable(app.getStart(), app.getEnd())) return false;
		appointments.add(app);
		app.setRoom(this);
		return true;
	}
}
