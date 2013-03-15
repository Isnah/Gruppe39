package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import model.Group;
import model.Person;
import model.Room;
import model.appointment.Appointment;
import model.notifications.Alarm;

/*
 * Ikke enda implementerte metoder:
 * 
 * addGroup
 * 
 */

//TODO: addAlarm

public class SQLTranslator2 {
	
	public static ArrayList<Room> getAllRooms(Connection c){
		
		ArrayList<Room> allerom = new ArrayList<Room>();
		
		//SELECT id FROM Room;
		
		StringBuilder query = new StringBuilder(); 
		query.append("SELECT id FROM Room;");
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			while(r.next()){
				allerom.add(getRoom(r.getInt(1), c));
			}

		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		
		return allerom;
		
	}
	
	public static ArrayList<Alarm> getAlarm(String personId, long startTime, long endTime, Connection c){
		
		ArrayList<Alarm> alarmliste = new ArrayList<Alarm>();
		
		String startTimeDBFormat = "'" + longTimeToDatetime(startTime) + "'";
		
		//SELECT id FROM Alarm WHERE time>[startTimeDBFormat] AND time<[startTimeDBFormat];
		
		StringBuilder query = new StringBuilder(); 
		query.append("SELECT id FROM Alarm WHERE time>");
		query.append(startTimeDBFormat);
		query.append(" AND time<");
		query.append(startTimeDBFormat);
		query.append(";");
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			while(r.next()){
				alarmliste.add(getAlarmById(r.getInt(1), c));
			}

		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		
		return alarmliste;
		
	}
	
	public static Alarm getAlarmById(int id, Connection c){
		
		//SELECT msg, email, app_id, time FROM Alarm WHERE id=[id];
		
		String msg;
		String email;
		int appId;
		Timestamp startAlarm;
		
		StringBuilder query = new StringBuilder(); 
		query.append("SELECT msg, email, app_id, time FROM Alarm WHERE id=");
		query.append(id+";");
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			r.next();
			msg = r.getString(1);
			email = r.getString(2);
			appId = r.getInt(3);
			startAlarm = new Timestamp(datetimeToLongTime(r.getString(4)));
			} 
		catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		
		return new Alarm(msg, email, appId, startAlarm);
		
	}
	
	public static boolean addAlarm(Alarm alarm, Connection c) throws Exception{
		
		int appId = alarm.getAppId();
		String msg = alarm.getMsg();
		String email = alarm.getEmail();
		Timestamp startAlarm = alarm.getStartAlarm();
		
		if(msg.length()>255){
			throw new Exception("Beskjed er for lang.");
		}
		if(email.length()>50){
			throw new Exception("emailadresse er for lang.");
		}
		
		//INSERT INTO Alarm (msg, email, app_id, time)
		//VALUES ('[msg]', '[email]', [appId], [startAlarm]);
		
		StringBuilder query = new StringBuilder(); 
		query.append("INSERT INTO Alarm (msg, email, app_id, time) VALUES ('");
		query.append(msg+"', '"+email+"', ");
		query.append(appId);
		query.append(", '" + longTimeToDatetime(startAlarm.getTime()) + "');");
		
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query.toString());
			return true;

		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return false;
		}
	}

	public static ArrayList<Person> getGroupMembers(int groupId, Connection c){
		ArrayList<String> ids = getIdsGroupMembers(groupId, c);
		ArrayList<Person> personer = new ArrayList<Person>();
		for(String x : ids){
			personer.add(getPerson(x, c));
		}

		return personer;
	}
	
	public static ArrayList<Person> getAllPeople(Connection c){
		
		//SELECT email FROM Person;
		
		ArrayList<Person> personer = new ArrayList<Person>();
		
		StringBuilder query = new StringBuilder(); 
		query.append("SELECT email FROM Person;");
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			while(r.next()){
				personer.add(getPerson(r.getString(1), c));
			}

		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		return personer;
	}
	
	public static ArrayList<Group> getAllGroups(Connection c){
		
		//SELECT id FROM Group_i;
		
		ArrayList<Group> grupper = new ArrayList<Group>();
		
		StringBuilder query = new StringBuilder(); 
		query.append("SELECT id FROM Group_i;");
		
		try {
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery(query.toString());
			while(r.next()){
				grupper.add(getGroup(r.getInt(1), c));
			}

		} catch (SQLException ex) {
			System.err.println("SQLException while adding personappointment");
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
		return grupper;
		
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//KUN FOR AA UNNGAA FEILMELDING!:
	
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
	
	public static Group getGroup(int id, Connection c){
		
		//SELECT name, email WHERE id=[id]
		
		StringBuilder query = new StringBuilder();
		query.append("SELECT name, email WHERE id=");
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
	
	private static String longTimeToDatetime(long time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(time);
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
