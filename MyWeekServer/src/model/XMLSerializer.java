package model;

import java.sql.Time;
import java.util.Iterator;

import model.appointment.Appointment;
import model.appointment.Meeting;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

public class XMLSerializer {
	
	/**
	 * 
	 * @param person
	 * @return
	 */
	/*public static Document modelToXml(Person person) {
		Element root = new Element("user");
		
		Iterator<Appointment> i = person.getAppointmentIterator();
		
	}*/
	
	/**
	 * This is used when you only want the basic information about a person sent.
	 * Fist name, last name and email.
	 * @param person The person you wish to parse.
	 */
	public static Element simplePersonToXml(Person person) {
		Element thePerson = new Element("person");
		
		Element firstName = new Element("first_name");
		firstName.appendChild(person.getFirstName());
		
		Element lastName = new Element("last_name");
		lastName.appendChild(person.getLastName());
		
		Element email = new Element("email");
		email.appendChild(person.getEmail());
		
		thePerson.appendChild(firstName);
		thePerson.appendChild(lastName);
		thePerson.appendChild(email);
		
		return thePerson;
	}
	
	/**
	 * 
	 * @param anAppointment
	 * @return
	 */
	public static Element appointmentToXml(Appointment anAppointment) {
		Element app = new Element("appointment");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(anAppointment.getID()));
		
		Element start = new Element("start");
		start.appendChild(Long.toString(anAppointment.getStart()));
		
		Element end = new Element("end");
		end.appendChild(Long.toString(anAppointment.getEnd()));
		
		Element name = new Element("name");
		name.appendChild(anAppointment.getName());
		
		Element descr = new Element("description");
		descr.appendChild(anAppointment.getDescr());
		
		Element registeredBy = new Element("registered_by");
		registeredBy.appendChild(simplePersonToXml(anAppointment.getRegisteredBy()));
		
		app.appendChild(id);
		app.appendChild(start);
		app.appendChild(end);
		app.appendChild(name);
		app.appendChild(descr);
		app.appendChild(registeredBy);
		
		if(anAppointment.getRoomDescr() != null) {
			Element roomDescr = new Element("room_description");
			roomDescr.appendChild(anAppointment.getRoomDescr());
			app.appendChild(roomDescr);
		}
		
		if(anAppointment.getRoom() != null) {
			app.appendChild(roomToXml(anAppointment.getRoom()));
		}
		
		return app;
	}
	
	/**
	 * Returns a XOR xml element for a room. Contains room_id, room_name and
	 * room_space
	 * @param aRoom
	 * @return
	 */
	public static Element roomToXml(Room aRoom) {
		Element room = new Element("room");
		
		Element roomID = new Element("room_id");
		roomID.appendChild(Integer.toString(aRoom.getID()));
		
		Element roomName = new Element("room_name");
		roomName.appendChild(aRoom.getName());
		
		Element roomSpace = new Element("room_space");
		roomSpace.appendChild(Integer.toString(aRoom.getSpace()));
		
		room.appendChild(roomID);
		room.appendChild(roomName);
		room.appendChild(roomSpace);
		
		return room;
	}

	
	public static Element roomToXmlWithAppointmentID(Room aRoom) {
		Element room = roomToXml(aRoom);
		
		Element appointments = new Element("appointments");
		
		Iterator<Appointment> it = aRoom.getAppointmentIterator();
		
		while(it.hasNext()) {
			Appointment app = it.next();
			Element appElem = new Element("appointment_id");
			appElem.appendChild(Integer.toString(app.getID()));
			appointments.appendChild(appElem);
		}
		
		room.appendChild(appointments);
		
		return room;
	}
	
	public static Element meetingToXml(Meeting aMeeting) {
		Element meeting = new Element("meeting");
				
		Element id = new Element("id");
		id.appendChild(Integer.toString(aMeeting.getID()));
		
		Element start = new Element("start");
		start.appendChild(Long.toString(aMeeting.getStart()));
		
		Element end = new Element("end");
		end.appendChild(Long.toString(aMeeting.getEnd()));
		
		Element name = new Element("name");
		name.appendChild(aMeeting.getName());
		
		Element descr = new Element("description");
		descr.appendChild(aMeeting.getDescr());
		
		Element registeredBy = new Element("registered_by");
		registeredBy.appendChild(simplePersonToXml(aMeeting.getRegisteredBy()));
		
		meeting.appendChild(id);
		meeting.appendChild(start);
		meeting.appendChild(end);
		meeting.appendChild(name);
		meeting.appendChild(descr);
		meeting.appendChild(registeredBy);
		
		if(aMeeting.getRoomDescr() != null) {
			Element roomDescr = new Element("room_description");
			roomDescr.appendChild(aMeeting.getRoomDescr());
			meeting.appendChild(roomDescr);
		}
		
		if(aMeeting.getRoom() != null) {
			Element room = new Element("room");
			room.appendChild(roomToXml(aMeeting.getRoom()));
			meeting.appendChild(room);
		}
		
		Element attendees = new Element("attendees");
		
		Element personAttendees = new Element("people_attending");
		
		Iterator<Person> it = aMeeting.getAttendeeIterator();
		while(it.hasNext()) {
			Person person = it.next();
			personAttendees.appendChild(simplePersonToXml(person));
		}
		
		attendees.appendChild(personAttendees);
		
		Element groupAttendees = new Element("groups_attending");
		
		Iterator<Group> itG = aMeeting.getGroupAttendeeIterator();		
		while(itG.hasNext()) {
			Group group = itG.next();
			attendees.appendChild(simpleGroupToXml(group));
		}
		
		attendees.appendChild(groupAttendees);
		
		meeting.appendChild(attendees);
		
		return meeting;
	}
	
	/**
	 * Creates an xml element with no recursion and only the basic info of the group
	 * @param aGroup
	 */
	public static Element simpleGroupToXml(Group aGroup) {
		Element group = new Element("group");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(aGroup.getID()));
		
		Element name = new Element("name");
		name.appendChild(aGroup.getName());
		
		group.appendChild(id);
		group.appendChild(name);
		
		return group;
	}
	
	
	public static Room assembleRoom(Element xmlRoomElement) {
		int id, space;
		String name;
		
		Element element = xmlRoomElement.getFirstChildElement("room_id");
		
		if(element == null) {
			System.err.println("Malformed xml element. No room_id while assembling room.");
			return null;
		}
		id = Integer.parseInt(element.getValue());
		
		element = xmlRoomElement.getFirstChildElement("room_name");
		
		if(element == null) {
			System.err.println("Malformed xml element. No room_name while assembling room.");
			return null;
		}
		name = element.getValue();
		
		element = xmlRoomElement.getFirstChildElement("room_space");
		
		if(element == null) {
			System.err.println("Malformed xml element. No room_space while assembling room");
			return null;
		}
		space = Integer.parseInt(element.getValue());
		
		/*
		 * TODO: Needs to check for appointments. Might need to ask the database for appointments through app_id's
		 */
		
		return new Room(id, space, name);
	}
	
	/*
	public static void main(String[] args) {
		Person person = new Person("ola@nordmann.no", "nordmann", "ola");
		Appointment app1 = new Appointment(1, new Time(1), new Time(2), "test1", "tester xml parsing", person);
		Room room = new Room(1, 5, "testrom");
		room.addAppointment(app1);
		person.addAppointment(app1);
		
		Document doc = testXml(person);
		
		try {
			Serializer serializer = new Serializer(System.out, "ISO-8859-1");
			serializer.setIndent(4);
			serializer.setMaxLength(64);
			serializer.write(doc);
		} catch (Exception ex) {
			// do nothing
		}
	}
	
	public static Document testXml(Person person) {
		Element root = new Element("user");
		
		root.appendChild(simplePersonToXml(person));
		root.appendChild(appointmentToXml(person.getAppointmentIterator().next()));
		
		return new Document(root);
	}*/
}
