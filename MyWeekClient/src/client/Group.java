package client;

import java.util.ArrayList;





public class Group {

	/**
	 * @Author Endre Elvestad
	 * @Kudos  Hans Olav
	 */
	final int id;
	String name;
	ArrayList<Person> member;
	ArrayList<Group> subgroup;
	
	//Constructor
	public Group(int id, String name) {
		this.id = id;
		this.name = name;
	}

	//Primary key
	public int getId() {
		return id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	//Returns all memebers in the current group - Not subgroups. 
	public ArrayList<Person> getMember() {
		return new ArrayList<Person>(member);
	}
	
	//Chekcs to see if a Person isntance is a memeber of the current group
	public boolean isGroupMember(Person person) {
		return member.contains(person);
	}

	
	//add a new memeber to the current group
	public boolean addMember(Person person) {
		if(member.contains(person))return false;
		member.add(person);
		return true;
	}

	//Get a list of subgroups, where the current group is the parrent 
	public ArrayList<Group> getSubgroup() {
		return new ArrayList<Group>(subgroup);
	}

	//Creates a new subgroup, where the current group is parrent. Returns False if allready a subgroup
	public boolean addSubgroup(Group group) {
		if(subgroup.contains(group)) return false;
		subgroup.add(group);
		return true;
		
	}
	
	
	
	
	
	

}
