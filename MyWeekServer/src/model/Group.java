package model;

import java.util.ArrayList;

/**
 * 
 * @author Hans Olav Slotte
 *
 */
public class Group {
	ArrayList<Person> members;
	ArrayList<Group> subgroups;
	String name;
	int id;
	
	public static ArrayList<Group> groups = new ArrayList<Group>();
	
	/**
	 * Just as with Person there is <b> no check for whether the id is available </b>
	 * Users will need to deal with this appropriately.
	 * @param id
	 * @param name
	 */
	public Group(int id, String name) {
		this.id = id;
		this.name = name;
		groups.add(this);
	}
	
	public int getID() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isDirectMember(Person person) {
		return members.contains(person);
	}
	
	/**
	 * Get the current members of this group.
	 * @return A copy of the member list. You cannot edit the actual group list
	 * directly, but the members are pointers to the person instances.
	 */
	public ArrayList<Person> getMembers() {
		return new ArrayList<Person>(members);
	}
}
