/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

/**
 *
 * @author Laxcor
 */
public class ListUnit {
    private int id;
    private String name;
    private String email;
    private boolean isGroup = false;
    
    public ListUnit(int id, String name) {
        this.id = id;
        this.name = name;
        isGroup = true;
    }
    public ListUnit(String email, String name) {
        this.email = email;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
    public boolean isGroup() {
        return isGroup;
    }
    @Override
    public String toString() {
        if (isGroup) {
            return "Group: " + name;
        }
        else {
            return name;
        }
    }
}
