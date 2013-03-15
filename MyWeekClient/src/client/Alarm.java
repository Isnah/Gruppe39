package client;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * @author Endre Elvestad
 * @author Tobias Linkjendal
 */

public class Alarm {
	private Timestamp startAlarm;
	private int appId;
	private String message;
	private String email;
	
	public Alarm( Timestamp startAlarm, int appId, String message, String email){
		this.startAlarm = startAlarm;
		this.appId = appId;
		this.message = message;
		this.email = email;
	}
	

	//GETTERS & SETTERS  

	
	public String getMessage(){
		return new String(message);
	}
	
	
        @Override
     public String toString() {
            return getMessage();
        }
	/**
	 * @param currentTime The time now at this very moment
	 * @return Boolean if it should start this alarm or not
	 */
	public boolean startAlarmNow(Time currentTime){
		if (startAlarm.getTime() > currentTime.getTime()) return false;
		else return true;
	}
	
	public void setAlarmMessage(String message) {
			this.message = message;
				
	}
	public int getAppId(){
		return appId;
	}
	public String getMsg(){
		return message;
	}
	public String getEmail(){
		return email;
	}

}
