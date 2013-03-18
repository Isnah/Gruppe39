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
	/*
	INV		CAN			HVA				MSG	     		app_id
	F		F		"du er fjernet"		møtenavn		nei			fra CancelNotification

	T		F		vanlig invitiation	møtenavn		ja			fra MeetingAnswer

	F		T		person decl			møtenavn		ja			fra MeetingAnswer

	T		T		møtet cancelled		møtenavn		nei			fra CancelNotification
	*/
	
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
