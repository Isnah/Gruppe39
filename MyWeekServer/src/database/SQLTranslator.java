package database;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import model.*;
import model.appointment.Appointment;
import model.appointment.Meeting;
import model.notifications.Notification;

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
	 * @return The appointment_id if the appointment and personAppointment were successfully added
	 * to the database. -1 if any of these queries fail.
	 */
	
	public static int addAppointment(Appointment app, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO Appointment (name, start, end_time, descr, ");
		if(app.getRoomDescr() != null) query.append("room_descr, ");
		else if(app.getRoom() != null) query.append("room_id, ");
		query.append("created_by ) VALUES ( \"");
		query.append(app.getName());
		query.append("\", \"");
		query.append(longTimeToDatetime(app.getStart()));
		query.append("\", \"");
		query.append(longTimeToDatetime(app.getEnd()));
		query.append("\", \"");
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
			return -1;
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
			return -1;
		}
		
		//to get the appointment_id
		query = new StringBuilder();
		query.append("SELECT id FROM Appointment WHERE id=");
		query.append("LAST_INSERT_ID()");
		int app_id = -1;
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			if(r.next()) {
				app_id = r.getInt(1);
				r.close();
			}
		} catch (SQLException ex) {
			System.err.println("SQL exception in getMeetingAnswer()");
			System.err.println("Message: " + ex.getMessage());
		}
		
		return app_id;
	}
	
	/**
	 * Adds a meeting to the database
	 * @param mtn
	 * @param c
	 * @return meeting_id if successful, -1 otherwise
	 */
	public static int addMeeting(Meeting mtn, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("INSERT INTO Appointment VALUES ( name, start, end_time, descr, ");
		if(mtn.getRoom() != null) query.append("room_id, ");
		query.append("created_by ) VALUES ( \"");
		query.append(mtn.getName() + "\", \"");
		query.append(longTimeToDatetime(mtn.getStart()) + "\", \"");
		query.append(longTimeToDatetime(mtn.getEnd()) + "\", \"");
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
			return -1;
		}
		
		//list of the emails to the participants that should be added to meetingAnswer
		ArrayList<String> participants = new ArrayList<String>();
		
		for(int i = 0; i < mtn.getAttendees().size(); ++i) {
			query = new StringBuilder();
			/* TODO: Very unsure about next command. LAST_INSERT_ID() will work in
			 * this case.
			 */
			query.append("INSERT INTO PersonAppointment ( app_id, email ) VALUES ( LAST_INSERT_ID(), \"");
			query.append(mtn.getAttendee(i).getEmail() + "\")");
			
			try {
				Statement s = c.createStatement();
				s.executeUpdate(query.toString());
			} catch (SQLException ex) {
				System.err.println("SQLExeption in adding meeting while adding personAppointments");
				System.err.println("Current index: " + i);
				System.err.println("Message: " + ex.getMessage());
				return -1;
			}
			
			if(!participants.contains(mtn.getAttendee(i).getEmail()))
			{
				participants.add(mtn.getAttendee(i).getEmail());
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
					return -1;
				}
				
				if(!participants.contains(mtn.getAttendee(i).getEmail()))
				{
					participants.add(mtn.getAttendee(i).getEmail());
				}
			}
		}
		
		//adds a meetingAnswer to every participant and sets it to null/"not answered"
		for(String pEmail : participants)
		{
			addMeetingAnswer(mtn.getID(), pEmail, null, c);
		}
		
		//to get the meeting_id
		query = new StringBuilder();
		query.append("SELECT id FROM Appointment WHERE id=");
		query.append("LAST_INSERT_ID()");
		int mtn_id = -1;
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			if(r.next()) {
				mtn_id = r.getInt(1);
				r.close();
			}
		} catch (SQLException ex) {
			System.err.println("SQL exception in getMeetingAnswer()");
			System.err.println("Message: " + ex.getMessage());
		}
		
		return mtn_id;
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
		
		query.append("INSERT INTO Room capacity, name VALUES ( ");
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
	
	/**
	 * Returns the room with id roomID from the database
	 * @param roomID The ID of the room
	 * @param c The connection to the database.
	 * @return the room
	 */
	public static Room getRoom(int roomID, Connection c) {
		ArrayList<Appointment> appointments = new ArrayList<Appointment>();
		
		//SELECT id FROM Appointment WHERE room_id=[roomID];
		
		StringBuilder query1 = new StringBuilder();
		query1.append("SELECT id FROM Appointment WHERE room_id=");
		query1.append(roomID);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query1.toString());
			int id;
			while(r.next()) {
				id = r.getInt(1);
				appointments.add(getAppointment(id, c));
			}
		} catch (SQLException ex) {
			System.err.println("SQL exception in getMeetingAnswer()");
			System.err.println("Message: " + ex.getMessage());
		}	
		
		int space;
		String name;
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT id, capacity, name FROM Room WHERE id=");
		query.append(roomID);
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			if(r.next()) {
				space = r.getInt(2);
				name = r.getString(3);
				r.close();
				return new Room(roomID, space, name, appointments);
			}
		} catch (SQLException ex) {
			System.err.println("SQL exception in getMeetingAnswer()");
			System.err.println("Message: " + ex.getMessage());
		}
		
		return null;
	}
	
	/*
	 * TODO
	 * Legge til: (lagt til/dekket av annen funksjon: *)
	 * *getMeetingAnswers(Meeting) - Should return answers that belongs to the meeting
	 * 							  - dekket av attendee-lister i meeting
	 * *getMeetingAnswers(String email) - dekket av getNotificationsForPerson()
	 * *getPersonsAppointments(Person) //dekket av getPersonWithAppointments()
	 * *UpdateAppointmentOrMeeting()
	 * getCancelNotification() ?
	 * deleteAppointmentOrMeeting() //1: holder med aa slette bare moetet/avtalen - databasen tar seg av resten
	 * 								//2: opprette CancelNotifications her
	 * updateMeetingAnswer()
	 * getNotificationsForPerson() //gjelder baade cancelNotifications til gjeldende person og meetingAnswers
	 * 							   //til deltakere i moeter som personen har laget (dersom de har declinet).
	 * 							   //I gjelder i tillegg meetingAnswers til gjeldende person dersom personen
	 * 							   //staar som "pending".
	 * 							   //Skal i denne metoden også slette alle cancelNotifications - slik at det
	 * 							   //ikke blir sendt flere ganger.
	 * LEGGE TIL TRE ATTENDEE-LISTER I GET MEETING (og i Meeting-klassen)
	 * 
	 * addCancelNotification()
	 * deleteCancelNotification()
	 * 
	 * *removeAttendeeFromMeeting(int mtnID, String email) - gjores i UpdateAppointmentOrMeeting()
	 * *removeGroupFromMeeting(int mtnID, Group group) - gjores i UpdateAppointmentOrMeeting()
	 */
	
	/**
	 * Adds a meeting answer related to the person with the email to the database
	 * @param meetingID The ID of the meeting
	 * @param email The persons email
	 * @param answer The persons answer to the meeting
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
	 * Deletes a meeting answer related to the person with the email to the database
	 * @param meetingID The ID of the meeting
	 * @param email The persons email
	 * @param c The connection to the database.
	 * @return True if the deletion was successful, false if an exception is met
	 * during execution.
	 */
	public static boolean deleteMeetingAnswer(int meetingID, String email, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM MeetingAnswer WHERE app_id=");
		query.append(meetingID);
		query.append(" AND email='");
		query.append(email);
		query.append("'");
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
			return true;
		} catch (SQLException ex) {
			System.err.println("SQL exception in deleteMeetingAnswer()");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
	}
	
	/**
	 * Get a persons answer to a meeting from the database
	 * @param meetingID The ID of the meeting
	 * @param email The persons email
	 * @param c The connection to the database.
	 * @return True if the addition was successful, false if an exception is met
	 * during execution.
	 */
	public static Boolean getMeetingAnswer(int meetingID, String email, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("SELECT answer FROM MeetingAnswer WHERE app_id=");
		query.append(meetingID);
		query.append(" AND email='");
		query.append(email);
		query.append("' ");
		
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery(query.toString());
			Boolean answer = null;
			String answerStr = null;
			if(rs.next()) {
				answerStr = rs.getString(1);
				if(answerStr.equals("1"))
				{
					answer = true;
				}
				else if(answerStr.equals("0"))
				{
					answer = false;
				}
			}
			rs.close();
			return answer;
		} catch (SQLException ex) {
			System.err.println("SQL exception in getMeetingAnswer()");
			System.err.println("Message: " + ex.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Adds a cancel notification related to the person with the email to the database
	 * @param mtn The meeting
	 * @param email The persons email
	 * @param cancelled True if meeting is cancelled - False if the person is removed from the meeting
	 * @param c The connection to the database.
	 * @return True if the addition was successful, false if an exception is met
	 * during execution.
	 */
	private static boolean addCancelNotification(Meeting mtn, String email, boolean cancelled, Connection c) {
		StringBuilder query = new StringBuilder();
		//TODO //add app_id 
		query.append("INSERT INTO CancelNotification email, msg, cancelled VALUES ( '");
		query.append(email);
		query.append("', '");
		query.append(mtn.getName());
		query.append("', ");
		if(cancelled)
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
			System.err.println("SQL exception in room addCancelNotification()");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
	}
	
	/**
	 * Get the cancelNotifications for a person
	 * @param email The persons email
	 * @param c The connection to the database.
	 * @return The cancel notification  if the addition was successful, false if an exception is met
	 * during execution.
	 */
	private static Boolean getPersonsCancelNotifications(String email, Connection c) {
		ArrayList<Notification> notifications = new ArrayList<Notification>();
		
		StringBuilder query = new StringBuilder();
		//TODO //get app_id
		query.append("SELECT msg, cancelled FROM CancelNotification WHERE email='");
		query.append(email);
		query.append("' ");
		
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery(query.toString());
			String msg = null;
			Boolean cancelled = null;
			while(rs.next()) {
				msg = rs.getString(1);
				cancelled = rs.getString(1);
				if(cancelled.equals("1"))
				{
					answer = true;
				}
				else if(cancelled.equals("0"))
				{
					answer = false;
				}
				notifications.add(new Notification(-1, invitation, cancelled, msg));
			}
			rs.close();
		} catch (SQLException ex) {
			System.err.println("SQL exception in getMeetingAnswer()");
			System.err.println("Message: " + ex.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Updates an appointment or a meeting
	 * @param app The appointment or meeting
	 * @param c
	 * @return true if successful, false otherwise
	 */
	public static boolean updateAppointmentOrMeeting(Appointment app, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("UPDATE Appointment SET name='");
		query.append(app.getName() + "', start='");
		query.append(longTimeToDatetime(app.getStart()) + "', end_time='");
		query.append(longTimeToDatetime(app.getEnd()) + "', descr='");
		query.append(app.getDescr() + "', ");
		if(app.getRoomDescr() != null) {
			query.append("room_descr='");
			query.append(app.getRoomDescr());
			query.append("', ");
		}
		else
		{
			query.append("room_descr=NULL, ");
		}
		if(app.getRoom() != null) query.append("room_id=" + app.getRoom().getID());
		else query.append("room_id=NULL");
		query.append(" WHERE ");
		query.append("id=");
		query.append(app.getID());
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
		} catch (SQLException ex) {
			System.err.println("SQLException while updating appointment or meeting");
			System.err.println("Message: " + ex.getMessage() );
			return false;
		}
		
		if(app instanceof Meeting)
		{
			Meeting mtn = (Meeting)app;
			
			//list of the emails to the former participants
			ArrayList<String> formerParticipants = new ArrayList<String>();
			query = new StringBuilder();
			query.append("SELECT DISTINCT email FROM PersonAppointment WHERE app_id=");
			query.append(mtn.getID());
			try {
				Statement s = c.createStatement();
				ResultSet rs = s.executeQuery(query.toString());
				while(rs.next()) {
					formerParticipants.add(rs.getString(1));
				}
				rs.close();
			} catch (SQLException ex) {
				System.err.println("SQL exception while updating appointment or meeting");
				System.err.println("Message: " + ex.getMessage());
				return false;
			}
			
			//first delete all former attendees from the meeting and then add the present attendees
			query = new StringBuilder();
			
			query.append("DELETE FROM PersonAppointment WHERE app_id=");
			query.append(mtn.getID());
			
			try {
				Statement s = c.createStatement();
				s.executeUpdate(query.toString());
			} catch (SQLException ex) {
				System.err.println("SQL exception while updating appointment or meeting");
				System.err.println("Message: " + ex.getMessage());
				return false;
			}			
			
			//list of the emails to the present participants
			ArrayList<String> participants = new ArrayList<String>();
			//adding present attendees
			for(int i = 0; i < mtn.getAttendees().size(); ++i) {
				query = new StringBuilder();
				query.append("INSERT INTO PersonAppointment ( app_id, email ) VALUES ( ");
				query.append(mtn.getID() + ", \"");
				query.append(mtn.getAttendee(i).getEmail() + "\")");
				
				try {
					Statement s = c.createStatement();
					s.executeUpdate(query.toString());
				} catch (SQLException ex) {
					System.err.println("SQLExeption in adding meeting while adding personAppointments");
					System.err.println("Current index: " + i);
					System.err.println("Message: " + ex.getMessage());
					return false;
				}
				
				if(!participants.contains(mtn.getAttendee(i).getEmail()))
				{
					participants.add(mtn.getAttendee(i).getEmail());
				}
			}
			
			for(int i = 0; i < mtn.getGroupAttendees().size(); ++i) {
				Group grp = mtn.getGroupAttendee(i);
				for(int j = 0; j < grp.getMembers().size(); ++j) {
					query = new StringBuilder();
					
					query.append("INSERT INTO PersonAppointment ( app_id, email, added_by ) VALUES ( ");
					query.append(mtn.getID() + ", \"");
					query.append(grp.getMembers().get(j).getEmail() + "\", ");
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
					
					if(!participants.contains(mtn.getAttendee(i).getEmail()))
					{
						participants.add(mtn.getAttendee(i).getEmail());
					}
				}
			}
			
			//delete the meetingAnswers belonging to the removed participants
			//also add cancelNotification with cancelled=false (that is, attendee removed)
			for(String pEmail : formerParticipants)
			{
				if(!participants.contains(pEmail))
				{//participant is removed
					//delete meetingAnswer
					deleteMeetingAnswer(mtn.getID(), pEmail, c);
					//add cancelNotification
					
				}
			}			
			
			//adds a meetingAnswer to every new participant and sets it to null/"not answered"
			for(String pEmail : participants)
			{
				if(!formerParticipants.contains(pEmail))
				{//participant is new, add meetingAnswer
					addMeetingAnswer(mtn.getID(), pEmail, null, c);
				}
			}
		}
		
		return true;
	}
	
	
	/**
	 * Deletes an appointment or meeting
	 * @param app_id The ID of the appointment or meeting
	 * @param c The connection to the database.
	 * @return True if the deletion was successful, false if an exception is met
	 * during execution.
	 */
	public static boolean deleteAppointmentOrMeeting(Appointment app, Connection c) {
		StringBuilder query = new StringBuilder();
		
		query.append("DELETE FROM Appointment WHERE app_id=");
		query.append(app.getID());
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
			return true;
		} catch (SQLException ex) {
			System.err.println("SQL exception in deleteAppointmentOrMeeting()");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
		
		//creating cancelNotifications
		
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
	
	/**
	 * Helper method to convert from long time(used in jdbc) to datetime to use in database.
	 */
	private static String longTimeToDatetime(long time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(time);
	}
	
	/**
	 * Helper method to convert from datetime(used in database) to long time(used in jdbc).
	 */
	private static long datetimeToLongTime(String datetime) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dateFormat.parse(datetime).getTime();
		} catch (ParseException e) {
			System.err.println("ParseException in datetimeToLongTime()");
			System.err.println("Message: " + e.getMessage());
		}
		return 0;
	}
	
	/**
	 * Method that communicates with database in order to return an object representing
	 * a person based on the e-mail key.
	 * The person is returned with all the appointments that the person is attending.
	 * @param email: The key to identify a specific person.
	 * @param c: Connection to database.
	 * @return
	 */
	public static Person getPersonWithAppointments(String email, Connection c) {
		
		//getting info about the person from the database
		
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
			System.err.println("SQLException while getting a person with appointments");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	
		
		//fetching the person's appointments
		//NB: The appointments(meetings) should contain simple persons without appointments
		ArrayList<Appointment> appointments = new ArrayList<Appointment>();
		
		query = new StringBuilder(); 
		query.append("SELECT DISTINCT app_id FROM PersonAppointment WHERE email='");
		query.append(email);
		query.append("'");
		
		int app_id;
		
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery(query.toString());
			while(rs.next())
			{
				app_id = rs.getInt(1);
				Statement stmnt = c.createStatement();
				ResultSet r = stmnt.executeQuery("SELECT DISTINCT email" +
						"FROM PersonAppointment WHERE app_id=" + app_id);
				r.next();
				if(r.next())
				{//the appointment has attendees and is therefore a meeting
					appointments.add(getMeeting(app_id, c));
				}
				else
				{//the appointment is not a meeting (just a regular appointment)
					appointments.add(getAppointment(app_id, c));
				}
			}
			rs.close();
		} catch (SQLException ex) {
			System.err.println("SQLException while getting a person with appointments");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	
		
		return new Person(email, lastname, firstname, appointments);
		
	}
	
	/**
	 * Method that communicates with database in order to return an object representing
	 * a person based on the e-mail key.
	 * @param email: The key to identify a specific person.
	 * @param c: Connection to database.
	 * @return
	 */
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

	/**
	 * Method to make a group with a specific id in the database into an object.
	 * @param id: id of the group
	 * @param c: Connection to the database.
	 * @return
	 */
	
	public static Group getGroup(int id, Connection c){
		
		//SELECT name, email WHERE id=[id]
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT name, email FROM Group WHERE id=");
		query.append(id);
		
		String name;
		String email;
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			r.next();
			name = r.getString(1);
			email = r.getString(2);
				
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		
		return new Group(id, name, email);
	}
	
	public static Appointment getAppointment(int id, Connection c) {
				
		//SELECT start FROM Appointment WHERE id=[id];
		
		StringBuilder query1 = new StringBuilder();
		query1.append("SELECT start, end_time, name, descr, room_descr, room_id, created_by" +
				"FROM Appointment WHERE id=");
		query1.append(id);
		
		Timestamp start;
		Timestamp end;
		String name;
		String descr;
		String room_descr;
		Integer room_id;
		String created_by;
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query1.toString());
			r.next();
			start = new Timestamp(datetimeToLongTime(r.getString(1)));
			end = new Timestamp(datetimeToLongTime(r.getString(2)));
			name = r.getString(3);
			descr = r.getString(4);
			room_descr = r.getString(5);
			if(r.wasNull()) room_descr = null;
			room_id = r.getInt(6);
			if(r.wasNull()) room_id = null;
			created_by = r.getString(7);
				
		} catch (SQLException ex) {
			System.err.println("SQLException while getting appointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
			
		Person registeredBy = getPerson(created_by, c);
		
		Appointment app = new Appointment(id, start, end, name, descr, registeredBy);
		if(room_descr != null)
		{
			app.setRoomDescr(room_descr);
		}
		if(room_id != null)
		{
			app.setRoom(getRoom(room_id, c));
		}
		return app;
	}
	
	public static Meeting getMeeting(int id, Connection c){
		
		//SELECT start, end_time, name, descr, created_by FROM Appointment WHERE id=[id];
		
		StringBuilder query1 = new StringBuilder();
		query1.append("SELECT start, end_time, name, descr, room_descr, room_id, created_by" +
				"FROM Appointment WHERE id=");
		query1.append(id);
		
		Timestamp start;
		Timestamp end;
		String name;
		String descr;
		String room_descr;
		Integer room_id;
		String created_by;
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query1.toString());
			r.next();
			start = new Timestamp(datetimeToLongTime(r.getString(1)));
			end = new Timestamp(datetimeToLongTime(r.getString(2)));
			name = r.getString(3);
			descr = r.getString(4);
			room_descr = r.getString(5);
			if(r.wasNull()) room_descr = null;
			room_id = r.getInt(6);
			if(r.wasNull()) room_id = null;
			created_by = r.getString(7);
				
		} catch (SQLException ex) {
			System.err.println("SQLException while getting meeting");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
			
		Person registeredBy = getPerson(created_by, c);
		
		//*****SLUTT PAA KOPIERING FRA getAppointment*****
		
		//SELECT DISTINCT email FROM PersonAppointment WHERE app_id=[id]
		
		StringBuilder query2 = new StringBuilder(); 
		query2.append("SELECT DISTINCT email FROM PersonAppointment WHERE app_id=");
		query2.append(id);
		
		ArrayList<Person> initialAttendees = new ArrayList<Person>();
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query2.toString());
			while(r.next()){
				initialAttendees.add(getPerson(r.getString(1), c));
			}
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}				
		
		
		//SELECT DISTINCT added_by FROM PersonAppointment WHERE app_id=[id]
		
		StringBuilder query3 = new StringBuilder(); 
		query3.append("SELECT DISTINCT added_by FROM PersonAppointment WHERE app_id=");
		query3.append(id);
		
		ArrayList<Group> initialGroups = new ArrayList<Group>();
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query2.toString());
			while(r.next()){
				initialGroups.add(getGroup(r.getInt(1), c));
			}
			
		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}	
		
		Meeting meeting = new Meeting(id, start, end, name, descr, registeredBy,
				initialAttendees, initialGroups);
		if(room_descr != null)
		{
			meeting.setRoomDescr(room_descr);
		}
		if(room_id != null)
		{
			meeting.setRoom(getRoom(room_id, c));
		}
		
		//partition the attendees in three lists
		ArrayList<Person> allAttendees = new ArrayList<Person>(initialAttendees);
		for(int i = 0; i < meeting.getGroupAttendees().size(); ++i)
		{
			Group grp = meeting.getGroupAttendee(i);
			for(int j = 0; j < grp.getMembers().size(); ++j)
			{
				if(!allAttendees.contains(grp.getMembers().get(j)))
				{
					allAttendees.add(grp.getMembers().get(j));
				}
			}
		}
		ArrayList<Person> accepted = new ArrayList<Person>();
		ArrayList<Person> pending = new ArrayList<Person>();
		ArrayList<Person> declined = new ArrayList<Person>();
		for(Person p : allAttendees)
		{
			Boolean answer = getMeetingAnswer(meeting.getID(), p.getEmail(), c);
			if(answer == null)
			{
				pending.add(p);
			}
			else if(answer)
			{
				accepted.add(p);
			}
			else
			{
				declined.add(p);
			}
		}
		meeting.setAccepted(accepted);
		meeting.setPending(pending);
		meeting.setDeclined(declined);
		
		return meeting;
	}
	
	
	

	/*
	 * Ikke enda implementerte metoder:
	 * 
	 * addGroup
	 * getNotification(id)
	 * 
	 */

}
