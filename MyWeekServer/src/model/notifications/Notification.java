package model.notifications;

/**
 *
 * @author Laxcor
 * @author Hans Olav Slotte
 */
public class Notification {
    private boolean invitation = false;
    private boolean cancelled = false;
    private int appID;
    private String message;
    
    public Notification(int id, boolean invitation, boolean cancelled, String msg) {
        this.invitation = invitation;
        this.cancelled = cancelled;
        if(!(cancelled ^ invitation)) {
        	System.err.println("cancelled can not also be an invitation");
        }
        appID = id;
        message = msg;
    }
    
    public boolean isInvitation() {
    	return invitation;
    }
    
    public boolean isCancelled() {
    	return cancelled;
    }
    
    public int getAppID() {
    	return appID;
    }
    
    public String getMessage() {
    	return new String(message);
    }
}
