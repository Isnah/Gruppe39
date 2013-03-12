package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Alarm;

/*
 * Ikke enda implementerte metoder:
 * 
 * addGroup
 * 
 */

public class SQLTranslator2 {
	
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