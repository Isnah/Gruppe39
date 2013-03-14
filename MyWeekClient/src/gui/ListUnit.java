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
    private boolean isGroup = false;
    
    public ListUnit(int id, String name, boolean isGroup) {
        this.id = id;
        this.name = name;
        this.isGroup = isGroup;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
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
