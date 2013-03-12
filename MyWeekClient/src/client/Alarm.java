package client;

import java.sql.Time;

import client.Appointment;

/**
 * 
 * @author Tobias Linkjendal
 */

public class Alarm {
	private Time startAlarm;
	private Appointment appointment;
	private String message;
	
	public Alarm(Time startAlarm, Appointment appointment, String message){
		this.startAlarm = startAlarm;
		this.appointment = appointment;
		this.message = message;
	}

}
