package database;

import java.sql.*;
import java.util.Properties;

import model.*;
import model.appointment.Appointment;
import model.appointment.Meeting;

/**
 * 
 * This class is used to hold static methods for connecting and interfacing with the
 * database.
 * 
 * @author Hans Olav Slotte
 *
 */
public class SQLTranslator {
	
	/**
	 * This method connects to the database and returns this connection. Remember to
	 * send this connection to all methods through this class to ensure that the
	 * connection is always the same.
	 * TODO: Set correct username and password in properties, as well as the database
	 * url.
	 * @return A connection to the database.
	 */
	public static Connection connectToDatabase() {
		Properties properties = new Properties();
		properties.put("user", "server");
		properties.put("password", "admin39");
		properties.put("characterEncoding", "ISO-8859-1");
		properties.put("useUnicode", "true");
		String url = "database url here";
		
		try {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection c = DriverManager.getConnection(url, properties);
		return c;
		} catch (SQLException ex) {
			System.err.println("SQL exception. url=" + url);
			System.err.println("Message: " + ex.getMessage());
			System.err.println("SQL state: " + ex.getSQLState());
			return null;
		} catch (ClassNotFoundException ex) {
			System.err.println("Class not found exception in connectToDatabase");
			System.err.println("Message: " + ex.getMessage());
			return null;
		} catch (InstantiationException ex) {
			System.err.println("Instantiation exception in connect to database");
			System.err.println("Message: " + ex.getMessage());
			return null;
		} catch (IllegalAccessException ex) {
			System.err.println("Illegal access exception in connect to database");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
	}
	
	/**
	 * Adds a person to the database
	 * @param person The person you wish to add.
	 * @param password The password this person is supposed to have in the system
	 * @param c The connection to the database.
	 * @return True if the addition was successful, false if an exception is met
	 * during execution.
	 */
	public static boolean addPerson(Person person, String password, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO Person VALUES ( \"");
		query.append(person.getEmail());
		query.append("\", \"");
		query.append(person.getFirstName());
		query.append("\", \"");
		query.append(person.getLastName());
		query.append("\", \"");
		query.append(password);
		query.append("\" )");
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
			return true;
		} catch (SQLException ex) {
			System.err.println("SQL exception in person addition");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
	}
	
	/**
	 * Adds the appointment to the database. After doing so it adds a relation between
	 * the person who created the appointment and the appointment.
	 * @param app The appointment you wish to add.
	 * @param c The connection to the database.
	 * @return True if the appointment and personAppointment were successfully added
	 * to. False if any of these queries fail.
	 */
	
	public static boolean addAppointment(Appointment app, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO Appointment (name, start, end_time, descr, ");
		if(app.getRoomDescr() != null) query.append("room_descr, ");
		else if(app.getRoom() != null) query.append("room_id, ");
		query.append("created_by ) VALUES ( \"");
		query.append(app.getName());
		query.append("\", ");
		query.append(app.getStart());
		query.append(", ");
		query.append(app.getEnd());
		query.append(", \"");
		query.append(app.getDescr());
		query.append("\", ");
		if(app.getRoomDescr() != null) {
			query.append("\"");
			query.append(app.getRoomDescr());
			query.append("\", ");
		}
		else if(app.getRoom() != null) {
			query.append(app.getRoom().getID());
			query.append(", ");
		}
		query.append("\"");
		query.append(app.getRegisteredBy().getEmail());
		query.append("\" );\n");
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
		} catch (SQLException ex) {
			System.err.println("SQLException in add appointment");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
		
		query = new StringBuilder();
		
		query.append("INSERT INTO PersonAppointment VALUES ( LAST_INSERT_ID(), \"");
		query.append(app.getRegisteredBy().getEmail());
		query.append("\" );");
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Adds a meeting to the database
	 * @param mtn
	 * @param c
	 * @return true if successful, false otherwise
	 */
	public static boolean addMeeting(Meeting mtn, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO Appointment VALUES ( name, start, end_time, descr, ");
		if(mtn.getRoom() != null) query.append("room_id, ");
		query.append("created_by ) VALUES ( \"");
		query.append(mtn.getName() + "\", ");
		query.append(mtn.getStart() + ", ");
		query.append(mtn.getEnd() + ", \"");
		query.append(mtn.getDescr() + "\", ");
		if(mtn.getRoom() != null) query.append(mtn.getRoom().getID() + ", ");
		query.append("\"");
		query.append(mtn.getRegisteredBy().getEmail());
		query.append("\" );\n");
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
		} catch (SQLException ex) {
			System.err.println("SQLException while creating meeting");
			System.err.println("Message: " + ex.getMessage() );
			return false;
		}
		
		for(int i = 0; i < mtn.getAttendees().size(); ++i) {
			query = new StringBuilder();
			/* TODO: Very unsure about next command. LAST_INSERT_ID() will work in
			 * this case.
			 */
			query.append("INSERT INTO PersonAppointment ( app_id, email ) VALUES ( LAST_INSERT_ID(), \"");
			query.append(mtn.getAttendee(i).getEmail());
			
			try {
				Statement s = c.createStatement();
				s.executeUpdate(query.toString());
			} catch (SQLException ex) {
				System.err.println("SQLExeption in adding meeting while adding personAppointments");
				System.err.println("Current index: " + i);
				System.err.println("Message: " + ex.getMessage());
				return false;
			}
		}
		
		for(int i = 0; i < mtn.getGroupAttendees().size(); ++i) {
			Group grp = mtn.getGroupAttendee(i);
			for(int j = 0; j < grp.getMembers().size(); ++j) {
				query = new StringBuilder();
				
				query.append("INSERT INTO PersonAppointment ( app_id, email, added_by ) VALUES ( LAST_INSERT_ID(), ");
				query.append("\" " + grp.getMembers().get(j).getEmail() + "\", ");
				query.append(grp.getID() + " );");
				
				try {
					Statement s = c.createStatement();
					s.executeUpdate(query.toString());
				} catch (SQLException ex) {
					System.err.println("SQLException in addMeeting while adding members of groups");
					System.err.println("i: " + i + ", j: " + j);
					System.err.println("Message: " + ex.getMessage());
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static Person getPerson(String email, Connection c) {
		
		//SELECT COUNT(*) FROM Person WHERE email=[email];
		
		StringBuilder query1 = new StringBuilder(); 
		query1.append("SELECT COUNT(*) FROM Person WHERE email=");
		query1.append(email);
		query1.toString();
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query1.toString());
			//Les: http://docs.oracle.com/javase/1.4.2/docs/api/java/sql/ResultSet.html
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			//return false;
		}
		
		
		
		String lastname;
		String firstname;
		
		
		
		Person person = new Person("email", "lastname", "firstname");
		
		return person;
		
	}
	
	/*
	 * Ikke enda implementerte metoder:
	 * 
	 * addGroup
	 * getPerson(id)
	 * getAppointment(id)
	 * getMeeting(id)
	 * 
	 */
}
