package client;

import java.util.ArrayList;

public class Group {

	/**
	 * @author Endre Elvestad
	 * @kudos  Hans Olav
	 * @alsoCoolGuy Tobias Linkendal
	 */
	private int id;
	private String name;
	private ArrayList<Person> member;
	private ArrayList<Group> subgroup;
	
	//Constructor
	public Group(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * @param name The name of the group
	 */
	public void setName(String name) {
		this.name = name;
	}

	//GETTERS
	
	/**
	 * @return The id of the group
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return The name of the group
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return A list of subgroups, where the current group is the parent 
	 */
	public ArrayList<Group> getSubgroup() {
		return new ArrayList<Group>(subgroup);
	}

	/**
	 * @return All memebers in the current group - Not subgroups. 
	 */
	public ArrayList<Person> getMember() {
		return new ArrayList<Person>(member);
	}
	
	
	/**
	 * @return True if you add a new memeber to the current group
	 */
	public boolean addMember(Person person) {
		if(member.contains(person))return false;
		member.add(person);
		return true;
	}

	/**
	 * Creates a new subgroup, where the current group is parrent. 
	 * @return Returns False if allready a subgroup
	 */
	public boolean addSubgroup(Group group) {
		if(subgroup.contains(group)) return false;
		subgroup.add(group);
		return true;
	}
	
	/**
	 * @return True if the group is allready in this group
	 */
	public boolean isGroupMember(Person person) {
		return member.contains(person);
	}
}
