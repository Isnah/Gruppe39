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
	
	public boolean startAlarmNow(Time currentTime){
		if (startAlarm.getTime() > currentTime.getTime()) return false;
		else return true;
	}
	
	public String getMessage(){
		return new String(message);
	}
	
	public Appointment getAppointment(){
		return appointment;
	}

}
