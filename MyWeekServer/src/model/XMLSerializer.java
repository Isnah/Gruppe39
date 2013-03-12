package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import model.appointment.Appointment;
import model.appointment.Meeting;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class XMLSerializer {
	
	/**
	 * Builds all information about a person into an xom document.
	 * @param person
	 * @return
	 */
	public static Document modelToXml(Person person) {
		Element root = new Element("model");
		
		root.appendChild(completePersonToXml(person));
		
		Document doc = new Document(root);
		
		return doc;
	}
	
	/**
	 * Same as {@link simplePersonToXml(Person person)}, exept all appointments
	 * are entered at the end with all information.
	 * @param person
	 * @return
	 */
	public static Element completePersonToXml(Person person) {
		Element personElem = simplePersonToXml(person);
		
		Iterator<Appointment> it = person.getAppointmentIterator();
		
		Element appointments = new Element("appointments");
		while(it.hasNext()) {
			Appointment appointment = it.next();
			if(appointment.isMeeting()) {
				Element el = meetingToXml((Meeting)appointment);
				appointments.appendChild(el);
			} else {
				Element el = appointmentToXml(appointment);
				appointments.appendChild(el);
			}
		}
		personElem.appendChild(appointments);
		
		return personElem;
	}
	
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

	/**
	 * Same as {@link roomToXml(Room)} except that it also adds the appointment
	 * id's to the end.
	 * @param aRoom The room you want to parse into xml.
	 * @return A {@link nu.xom.Element} for the room.
	 */
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
	
	/**
	 * 
	 * @param aMeeting
	 * @return
	 */
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
			Element room = roomToXml(aMeeting.getRoom());
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
	
	/**
	 * Assembles a simple person from an xml element. Returns null if the
	 * element is malformed, along with printing an error message in
	 * {@link System.err}
	 * @param xmlPersonElement
	 * @return
	 */
	public static Person assembleSimplePerson(Element xmlPersonElement) {
		String email, lastName, firstName;
		
		Element element = xmlPersonElement.getFirstChildElement("email");
		if(element == null) {
			System.err.println("Malformed xml element. No email while assembling simple person");
			return null;
		}
		email = element.getValue();
		
		element = xmlPersonElement.getFirstChildElement("first_name");
		if(element == null) {
			System.err.println("Malformed xml element. No first_name while assembling simple person");
			return null;
		}
		firstName = element.getValue();
		
		element = xmlPersonElement.getFirstChildElement("last_name");
		if(element == null) {
			System.err.println("Malformed xml element. No last_name while assembling simple person");
			return null;
		}
		lastName = element.getValue();
		
		Person person = new Person(email, lastName, firstName);
		return person;
	}
	
	/**
	 * Assembles a group from an xml element made by the simpleGroupToXml()
	 * or similar. Only returns a group with id and name. Anything else
	 * will have to be asked for from the database.
	 * @param xmlGroupElement
	 * @return
	 */
	public static Group assembleSimpleGroup(Element xmlGroupElement) {
		int id;
		String name;
		
		Element element = xmlGroupElement.getFirstChildElement("id");
		if(element == null) {
			System.err.println("Malformed xml element. No id in assemble simple group");
			return null;
		}
		id = Integer.parseInt(element.getValue());
		
		element = xmlGroupElement.getFirstChildElement("name");
		if(element == null) {
			System.err.println("Malformed xml element. No name in assemble simple group");
			return null;
		}
		name = element.getValue();
		
		return new Group(id, name);
	}
	
	public static Meeting assembleMeeting(Element xmlMeetingElement) {
		int id;
		Timestamp start, end;
		String name, descr, roomDescr;
		Person registeredBy;
		Room room;
		ArrayList<Person> attendees;
		ArrayList<Group> groupAttendees;
		
		
		Element element = xmlMeetingElement.getFirstChildElement("id");
		if(element == null) {
			System.err.println("Malformed xml element. No id while assembling meeting");
			return null;
		}
		id = Integer.parseInt(element.getValue());
		
		element = xmlMeetingElement.getFirstChildElement("start");
		if(element == null) {
			System.err.println("Malformed xml element. No start while assembling");
			return null;
		}
		start = new Timestamp(Long.parseLong(element.getValue()));
		
		element = xmlMeetingElement.getFirstChildElement("end");
		if(element == null) {
			System.err.println("Malformed xml element. No end while assembling meeting");
			return null;
		}
		end = new Timestamp(Long.parseLong(element.getValue()));
		
		element = xmlMeetingElement.getFirstChildElement("name");
		if(element == null) {
			System.err.println("Malformed xml element. No name while assembling meeting");
			return null;
		}
		name = element.getValue();
		
		element = xmlMeetingElement.getFirstChildElement("description");
		if(element == null) {
			System.err.println("Malformend xml element. No description while assembling meeting");
			return null;
		}
		descr = element.getValue();
		
		element = xmlMeetingElement.getFirstChildElement("registered_by");
		if(element == null) {
			System.err.println("Malformed xml element. No registered_by while assembling person");
			return null;
		}
		element = element.getFirstChildElement("person");
		if(element == null) {
			System.err.println("Malformed xml element. No person under registered_by while assembling meeting");
			return null;
		}
		registeredBy = assembleSimplePerson(element);
		
		boolean hasRoomDescr = true;
		element = xmlMeetingElement.getFirstChildElement("room_description");
		if(element == null) {
			hasRoomDescr = false;
			roomDescr = null;
		} else {
			roomDescr = element.getValue();
		}
		
		element = xmlMeetingElement.getFirstChildElement("room");
		if(element == null) {
			if(!hasRoomDescr) {
				System.err.println("Malformed xml element. Neither room_description nor room while assembling meeting");
				return null;
			}
			room = null;
		} else {
			room = assembleRoom(element);
		}
		
		element = xmlMeetingElement.getFirstChildElement("attendees");
		if(element == null) {
			System.err.println("Malformed xml element. No attendees tag while assembling meeting.");
			return null;
		}
		
		attendees = new ArrayList<Person>();
		Element personAtt = element.getFirstChildElement("people_attending");
		Elements people = personAtt.getChildElements();
		for(int i = 0; i < people.size(); ++i) {
			personAtt = people.get(i);
			Person per = assembleSimplePerson(personAtt);
			attendees.add(per);
		}
		
		groupAttendees = new ArrayList<Group>();
		Element grpAtt = element.getFirstChildElement("groups_attending");
		Elements groups = grpAtt.getChildElements();
		for(int i = 0; i < groups.size(); ++i) {
			grpAtt = groups.get(i);
			Group grp = assembleSimpleGroup(grpAtt);
			groupAttendees.add(grp);
		}
		
		Meeting meeting = new Meeting(id, start, end, name, descr, registeredBy, attendees, groupAttendees);
		if(room != null) meeting.setRoom(room);
		if(roomDescr != null) meeting.setRoomDescr(roomDescr);
		
		return meeting;
	}
	
	
	/**
	 * Assembles a room from a {@link nu.xom.Element}. Be aware that it will return
	 * null if an error is noticed in the element.
	 * @param xmlAppElement
	 * @return 
	 */
	public static Appointment assembleAppointment(Element xmlAppElement) {
		int id;
		Timestamp start, end;
		Room room;
		Person registeredBy;
		String name, descr, roomDescr;
		
		Element element = xmlAppElement.getFirstChildElement("id");
		if(element == null) {
			System.err.println("Malformed xml element. No id while assembling appointment.");
			return null;
		}
		id = Integer.parseInt(element.getValue());
		
		element = xmlAppElement.getFirstChildElement("start");
		if(element == null) {
			System.err.println("Malformed xml element. No start while assembling appointment.");
			return null;
		}
		start = new Timestamp(Long.parseLong(element.getValue()));
		
		element = xmlAppElement.getFirstChildElement("end");
		if(element == null) {
			System.err.println("Malformed xml element. No end while assembling appointment.");
			return null;
		}
		end = new Timestamp(Long.parseLong(element.getValue()));
		
		element = xmlAppElement.getFirstChildElement("name");
		if(element == null) {
			System.err.println("Malformed xml element. No name while assembling appointment");
			return null;
		}
		name = element.getValue();
		
		element = xmlAppElement.getFirstChildElement("description");
		if(element == null) {
			System.err.println("Malformed xml element. No description while assembling appointment");
			return null;
		}
		descr = element.getValue();
		
		element = xmlAppElement.getFirstChildElement("registered_by");
		if(element == null) {
			System.err.println("Malformed xml element. No registered_by while assembling appointment");
			return null;
		}
		element = element.getFirstChildElement("person");
		if(element == null) {
			System.err.println("Malformed xml element. No person under registered_by while assembling appointment");
			return null;
		}
		registeredBy = assembleSimplePerson(element);
		
		
		boolean hasRoomDescr = true;
		element = xmlAppElement.getFirstChildElement("room_description");
		if(element == null) {
			hasRoomDescr = false;
			roomDescr = null;
		} else {
			roomDescr = element.getValue();
		}
		
		element = xmlAppElement.getFirstChildElement("room");
		if(element == null) {
			if(!hasRoomDescr) {
				System.err.println("Malformed xml element. No room or room_descr while assembling appointment");
				return null;
			}
			room = null;
		} else {
			room = assembleRoom(element);
		}
		
		Appointment app = new Appointment(id, start, end, name, descr, registeredBy);
		if(roomDescr != null) app.setRoomDescr(roomDescr);
		if(room != null) app.setRoom(room);
		
		return app;
		
	}
	
	/**
	 * Assembles a room from an xml element. Returns null if the element is
	 * malformed.
	 * @param xmlRoomElement
	 * @return
	 */
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
		Person user = new Person("kari@nordmann.no", "nordmann", "kari");
		Person person = new Person("ola@nordmann.no", "nordmann", "ola");
		Appointment app1 = new Appointment(1, new Time(1), new Time(2), "test1", "tester xml parsing", person);
		Room room = new Room(1, 5, "testrom");
		Meeting mtn = new Meeting(2, new Time(3), new Time(4), "meeting test", "a meeting between kari and ola", user, new ArrayList<Person>(), new ArrayList<Group>());
		mtn.addAttendee(person);
		room.addAppointment(mtn);
		room.addAppointment(app1);
		user.addAppointment(mtn);
		person.addAppointment(app1);
		person.addAppointment(mtn);
		
		Document doc = modelToXml(user);
		
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
