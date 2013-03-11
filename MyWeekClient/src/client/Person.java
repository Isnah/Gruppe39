package client;

//import java.beans.PropertyChangeEvent;



public class Person {

	/**
	 * @Author Endre Elvestad
	 * 
	 */  
	

	private String firstName;
	private String lastName;
	private String email;
	

	public Person(String email, String lastName, String firstName) {
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
	}
	
	
	public String getFirstName() {
		return firstName;
	}

/**
	public void setFirstName(String firstName) {
		this.firstName = firstName;
		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_PROPERTY_NAME, oldName, name);
		propChangeSupp.firePropertyChange(event);
	}
*/

	public String getLastName() {
		return lastName;
	}


	/**
	public void setLastName(String lastName) {
		this.lastName = lastName;
		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_PROPERTY_NAME, oldName, name);
		propChangeSupp.firePropertyChange(event);
	}

	 */
	public String getEmail() {
		return email;
	}


	/**
	  
	 public void setEmail(String email) {
		this.email = email;
		PropertyChangeEvent event = new PropertyChangeEvent(this, NAME_PROPERTY_NAME, oldName, name);
		propChangeSupp.firePropertyChange(event);
	}
	
	*/


	
	




}
