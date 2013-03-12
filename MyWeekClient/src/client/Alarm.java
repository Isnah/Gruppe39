package client;

import java.sql.Time;

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
	
	
	//GETTERS
	
	public String getMessage(){
		return new String(message);
	}
	
	public Appointment getAppointment(){
		return appointment;
	}
	
	/**
	 * @param currentTime The time now at this very moment
	 * @return Boolean if it should start this alarm or not
	 */
	public boolean startAlarmNow(Time currentTime){
		if (startAlarm.getTime() > currentTime.getTime()) return false;
		else return true;
	}
	


}
