package model.notifications;

public class Alarm {
	
	int id;
	String msg;
	String email;
	int appId;
	
	public Alarm(int id, String msg, String email, int appId){
		
		this.id = id;
		this.msg = msg;
		this.email = email;
		this.appId = appId;
		
	}
	
	public int getId(){
		return id;
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

}
