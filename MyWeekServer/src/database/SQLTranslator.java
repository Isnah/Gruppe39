package database;

import java.sql.*;
import java.util.ArrayList;
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
 * @author Thor Jarle Skinstad
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
	
	/**
	 * Adds a room to the database
	 * @param room The room you wish to add.
	 * @param c The connection to the database.
	 * @return True if the addition was successful, false if an exception is met
	 * during execution.
	 */
	public static boolean addRoom(Room room, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO Room VALUES ( ");
		query.append(room.getID());
		query.append(", ");
		query.append(room.getSpace());
		query.append(", '");
		query.append(room.getName());
		query.append("' )");
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
			return true;
		} catch (SQLException ex) {
			System.err.println("SQL exception in room addition");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
	}
	
	/*
	 * Legge til:
	 * getMeetingAnswers(Meeting) - Should return answers that belongs to the meeting
	 */
	
	/**
	 * Adds a meeting answer related to the person with the email to the database
	 * @param meetingID The ID of the meeting
	 * @param email The persons email
	 * @param c The connection to the database.
	 * @return True if the addition was successful, false if an exception is met
	 * during execution.
	 */
	public static boolean addMeetingAnswer(int meetingID, String email, Boolean answer, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO MeetingAnswer VALUES ( ");
		query.append(meetingID);
		query.append(", '");
		query.append(email);
		query.append("', ");
		if(answer == null)
		{
			query.append("NULL");
		}
		else if(answer)
		{
			query.append("1");
		}
		else
		{
			query.append("0");
		}
		query.append(" )");
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
			return true;
		} catch (SQLException ex) {
			System.err.println("SQL exception in room addition");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
	} 
	
	/**
	 * Checks if the email and password are correct and belongs to a user.
	 * @param email 
	 * @param password 
	 * @param c The connection to the database.
	 * @return True if a user exists with the given email and password, false otherwise.
	 */
	public static boolean isValidEmailAndPassword(String email, String password, Connection c)
	{
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT email, password FROM Person");
		
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery(query.toString());
			while(rs.next()) {
				if(email.equals(rs.getString(1)) && password.equals(rs.getString(2))) {
					rs.close();
					return true;
				}
			}
			rs.close();
		} catch (SQLException ex) {
			System.err.println("SQL exception in email and password validation");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
		
		return false;
	}
	
	public static Person getPerson(String email, Connection c) {
		
		//SELECT COUNT(*) FROM Person WHERE email=[email];
		
		StringBuilder query1 = new StringBuilder(); 
		query1.append("SELECT COUNT(*) FROM Person WHERE email=");
		query1.append(email);
		
		int antPersoner;
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query1.toString());
			//Les: http://docs.oracle.com/javase/1.4.2/docs/api/java/sql/ResultSet.html
			antPersoner = r.getInt(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		
		if(antPersoner!=1){
			System.err.println("Message: Noe er galt. Sjekk getPerson(...)");
		}
		
		//SELECT first_name FROM Person WHERE email=[email];
		
		StringBuilder query2 = new StringBuilder(); 
		query2.append("SELECT first_name FROM Person WHERE email=");
		query2.append(email);
		
		String firstname;
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query2.toString());
			firstname = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	
		
		//SELECT last_name FROM Person WHERE email=[email];
		
		String lastname;
		
		StringBuilder query3 = new StringBuilder(); 
		query3.append("SELECT last_name FROM Person WHERE email=");
		query3.append(email);	
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query3.toString());
			lastname = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	
		
		Person person = new Person(email, lastname, firstname);
		
		return person;
		
	}
	
	public static Appointment getAppointment(int id, Connection c) {
				
		//SELECT start FROM Appointment WHERE id=[id];
		
		Timestamp start;
		
		StringBuilder query1 = new StringBuilder(); 
		query1.append("SELECT start FROM Appointment WHERE id=");
		query1.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query1.toString());
			start = r.getTimestamp(1);
				
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		
		//SELECT end_time FROM Appointment WHERE id=[id];
		
		Timestamp end;
		
		StringBuilder query2 = new StringBuilder(); 
		query2.append("SELECT end_time FROM Appointment WHERE id=");
		query2.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query2.toString());
			end = r.getTimestamp(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}		
		
		
		
		//SELECT name FROM Appointment WHERE id=[id];
		
		String name;
		
		StringBuilder query3 = new StringBuilder(); 
		query3.append("SELECT name FROM Appointment WHERE id=");
		query3.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query3.toString());
			name = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}			
		
		
		//SELECT descr FROM Appointment WHERE id=[id];
		
		String descr;
		
		StringBuilder query4 = new StringBuilder(); 
		query4.append("SELECT name FROM Appointment WHERE id=");
		query4.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query4.toString());
			descr = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}			
		
		
		String created_by;
		
		StringBuilder query5 = new StringBuilder(); 
		query5.append("SELECT name FROM Appointment WHERE id=");
		query5.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query5.toString());
			created_by = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}		
		
		Person registeredBy = getPerson(created_by, c);
		
		return new Appointment(id, start, end, name, descr, registeredBy);
		
	}
	
	public static Meeting getMeeting(int id, Connection c){
		
		
		//*****KOPIERT FRA getAppointment*****
		
		Timestamp start;
		
		StringBuilder query1 = new StringBuilder(); 
		query1.append("SELECT start FROM Appointment WHERE id=");
		query1.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query1.toString());
			start = r.getTimestamp(1);
				
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		
		//SELECT end_time FROM Appointment WHERE id=[id];
		
		Timestamp end;
		
		StringBuilder query2 = new StringBuilder(); 
		query2.append("SELECT end_time FROM Appointment WHERE id=");
		query2.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query2.toString());
			end = r.getTimestamp(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}		
		
		
		
		//SELECT name FROM Appointment WHERE id=[id];
		
		String name;
		
		StringBuilder query3 = new StringBuilder(); 
		query3.append("SELECT name FROM Appointment WHERE id=");
		query3.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query3.toString());
			name = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}			
		
		
		//SELECT descr FROM Appointment WHERE id=[id];
		
		String descr;
		
		StringBuilder query4 = new StringBuilder(); 
		query4.append("SELECT name FROM Appointment WHERE id=");
		query4.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query4.toString());
			descr = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}			
		
		
		String created_by;
		
		StringBuilder query5 = new StringBuilder(); 
		query5.append("SELECT name FROM Appointment WHERE id=");
		query5.append(id);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query5.toString());
			created_by = r.getString(1);
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}		
		
		Person registeredBy = getPerson(created_by, c);
		
		//*****SLUTT P� KOPIERING FRA getAppointment*****
		
		//SELECT email FROM PersonAppointment WHERE app_id=[id]
		
		StringBuilder query6 = new StringBuilder(); 
		query6.append("SELECT email FROM PersonAppointment WHERE app_id=");
		query6.append(id);
		
		ResultSet personer;
		
		try {
			Statement s = c.createStatement();
			personer = s.executeQuery(query6.toString());
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}				
		
		ArrayList<Person> initialAttendees = new ArrayList<Person>();
		
		for(int i = 1; true; i++){
			try{
				Person person = getPerson(personer.getString(i), c);
				if(person == null){
					break;
				}
				initialAttendees.add(person);
			} catch (SQLException e){
					break;
			}
		}
		
		//SELECT DISTINCT added_by FROM PersonAppointment WHERE app_id=[id]
		
		StringBuilder query7 = new StringBuilder(); 
		query7.append("SELECT DISTINCT added_by FROM PersonAppointment WHERE app_id=");
		query7.append(id);
		
		ResultSet grupper;
		
		try {
			Statement s = c.createStatement();
			grupper = s.executeQuery(query7.toString());
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	
		
		ArrayList<Group> initialGroups = new ArrayList<Group>();
		
		for(int i = 1; true; i++){
			try{
				Group group = getGroup(grupper.getInt(i), c);
				if(group == null){
					break;
				}
				initialGroups.add(group);
			} catch (SQLException e){
					break;
			}
		}		
		
		return new Meeting(id, start, end, name, descr, registeredBy, initialAttendees, initialGroups);
		
	}
	
	public static Group getGroup(int id, Connection c){
		return null;
	}
	
	public static boolean hjelpeMetodeGetMeeting1(ResultSet r, int i){
		String s;
		try{
			s = r.getString(i);
		} catch (Exception e){
			return false;
		}
		
		if(s != null){
			return true;
		}
		return false;
	}
	
	/*
	 * Ikke enda implementerte metoder:
	 * 
	 * addGroup
	 * getMeeting(id)
	 * 
	 */
}
