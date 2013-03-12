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
	String email;
	int id;
	
	/**
	 * @param id
	 * @param name
	 */
	public Group(int id, String name, String email) {
		this.id = id;
		this.name = name;
		this.email = email;
	}
	
	public String getEmail(){
		return email;
	}
	
	public int getID() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * @return A copy of the name
	 */
	public String getName() {
		return new String(name);
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
	
	/**
	 * Get the subgroups of this group
	 * @return A copy of the group list. The actual group list cannot be edited
	 * through this list, but the members of the list can be.
	 */
	public ArrayList<Group> getSubgroups() {
		return new ArrayList<Group>(subgroups);
	}
	
	/**
	 * Add a group to the subgroups of this group
	 * @param group The group you wish to add
	 * @return true if the group was successfully added, false if the
	 * subgroups list already contains this group.
	 */
	public boolean addSubgroup(Group group) {
		if(subgroups.contains(group)) return false;
		subgroups.add(group);
		return true;
	}
}