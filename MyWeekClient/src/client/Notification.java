/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author Laxcor
 */
class Notification {
    private boolean invitation = false;
    private boolean canceled = false;
    private int meetingID;
    private String message;
    
    public Notification(int id, boolean invitation) {
        this.invitation = invitation;
        this.meetingID = id;
    }
    public Notification(String msg) {
        canceled = true;
    }
}
