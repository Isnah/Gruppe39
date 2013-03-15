package model.notifications;

import java.sql.Timestamp;

/**
 * @author Thor Jarle Skinstad
 * @author Tor �kland Barstad
 *
 */
public class Alarm {
	private String msg;
	private String email;
	private int appId;
	private Timestamp startAlarm;
	
	public Alarm(String msg, String email, int appId, Timestamp startAlarm){
		this.msg = msg;
		this.email = email;
		this.appId = appId;
		this.startAlarm = startAlarm;
	}
	
	public int getAppId(){
		return appId;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public String getEmail(){
		return email;
	}
	
	public Timestamp getStartAlarm()
	{
		return startAlarm;
	}
}
