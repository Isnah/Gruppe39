/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author Laxcor
 * @author Tobias Linkjendal
 */
public class Notification {
	private boolean invitation = false;
    private boolean cancelled = false;
    private int appID;
    private String message;
    
    public Notification(int id, boolean invitation, boolean cancelled, String msg) {
        this.invitation = invitation;
        this.cancelled = cancelled;
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
    
    @Override
    public String toString(){
    	String text = "";
    	if(!invitation && !cancelled){
    		text += "Removed from " + message;
    	}
    	else if(invitation && !cancelled){
    		text += "Invited to " + message;
    	}
    	else if(!invitation && cancelled){
    		text += "Someone has declined" + message;
    	}
    	else if(invitation && cancelled){
    		text += message + " is cancelled";
    	}
    	return text;
    }
}
