package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Alarm;
import model.Group;
import model.Person;

/*
 * Ikke enda implementerte metoder:
 * 
 * addGroup
 * 
 */

public class SQLTranslator2 {




	public static ArrayList<Person> getGroupMembers(int groupId, Connection c){
		ArrayList<String> ids = getIdsGroupMembers(groupId, c);
		ArrayList<Person> personer = new ArrayList<Person>();
		for(String x : ids){
			personer.add(getPerson(x, c));
		}

		return personer;
	}

	public static ArrayList<String> getIdsGroupMembers(int groupId, Connection c){

		//SELECT email FROM MemberGroup WHERE grp_id=[groupId];

		StringBuilder query = new StringBuilder(); 
		query.append("SELECT email FROM MemberGroup WHERE grp_id=");
		query.append(groupId);

		ArrayList<String> ids = new ArrayList<String>();

		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			while(r.next()){
				ids.add(r.getString(1));
			}

		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}

		return ids;

		/*
		CREATE TABLE MemberGroup(
				email		varchar(50) NOT NULL,
				grp_id		int NOT NULL,
				PRIMARY KEY (email,grp_id),
				FOREIGN KEY (grp_id) REFERENCES Group_i(id)  ON UPDATE CASCADE ON DELETE CASCADE,
				FOREIGN KEY (email) REFERENCES Person(email)  ON UPDATE CASCADE ON DELETE CASCADE
			);
		 */

	}

	public static Alarm getAlarm(int id, Connection c){

		String msg;
		String email;
		int appId;

		//SELECT msg, email, app_id FROM Alarm WHERE id=[id]

		StringBuilder query = new StringBuilder(); 
		query.append("SELECT msg, email, app_id FROM Alarm WHERE id=");
		query.append(id);

		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			r.next();
			msg = r.getString(1);
			email = r.getString(2);
			appId = r.getInt(3);

		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	

		return new Alarm(id, msg, email, appId);

	}
	
	//KUN FOR Å UNNGÅ FEILMELDING!:
	
	public static Person getPerson(String email, Connection c) {

		//SELECT first_name, last_name FROM Person WHERE email=[email];

		StringBuilder query = new StringBuilder(); 
		query.append("SELECT first_name, last_name FROM Person WHERE email='");
		query.append(email);
		query.append("'");

		String firstname, lastname;

		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			r.next();
			firstname = r.getString(1);
			lastname = r.getString(2);
			r.close();
		} catch (SQLException ex) {
			System.err.println("SQLException while getting a person");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	

		return new Person(email, lastname, firstname);

	}

}

/*

CREATE TABLE Alarm(
id 		int NOT NULL AUTO_INCREMENT,
msg		varchar(255),
email		varchar(50) NOT NULL,
app_id		int NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (email) REFERENCES Person(email) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (app_id) REFERENCES Appointment(id) ON DELETE CASCADE ON UPDATE CASCADE
);

 */