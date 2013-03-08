package database;

import model.*;

public class SQLTranslator {
	
	/**
	 * NOT COMPLETE, always returns true.
	 * @param person
	 * @param password
	 * @return
	 */
	public static boolean addPerson(Person person, String password) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO Person VALUES ( \"");
		query.append(person.getEmail());
		query.append("\", \"");
		query.append(person.getFirstName());
		query.append("\", \"");
		query.append(person.getLastName());
		query.append("\", \"");
		query.append(password);
		return true;
	}
}
