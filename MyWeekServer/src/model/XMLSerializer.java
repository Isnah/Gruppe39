package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import server.helpers.LoginCredentials;

import model.appointment.Appointment;
import model.appointment.Meeting;
import model.notifications.Alarm;
import model.notifications.Notification;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class XMLSerializer {
	
	public static String getType(Document doc) {
		Element root = doc.getRootElement();
		return root.getLocalName();
	}
	
	public static String getType(Element el) {
		return el.getLocalName();
	}
	
	/**
	 * Builds all information about a person into an xom document.
	 * @param person
	 * @return
	 */
	public static Document modelToXml(Person person) {
		Element root = new Element("return");
		Element modelroot = new Element("model");
		
		modelroot.appendChild(completePersonToXml(person));
		
		root.appendChild(modelroot);
		
		Document doc = new Document(root);
		
		return doc;
	}
	
	public static Document loginToXml(String username, char[] password) {
		String passwordString = String.valueOf(password);
		
		Element root = new Element("login");
		
		Element user = new Element("user");
		user.appendChild(username);
		
		Element pword = new Element("password");
		pword.appendChild(passwordString);
		
		root.appendChild(user);
		root.appendChild(pword);
		
		return new Document(root);
	}
	
	public static LoginCredentials assembleLogin(Document loginDoc) {
		String user, password;
		
		Element root = loginDoc.getRootElement();
		if(root.getLocalName() != "login") {
			System.err.println("Not a login xml");
			return null;
		}
		
		Element element = root.getFirstChildElement("user");
		if(element == null) {
			System.err.println("Not a valid login xml. No user.");
			return null;
		}
		user = element.getValue();
		
		element = root.getFirstChildElement("password");
		if(element == null) {
			System.err.println("Not a valid login xml. No password.");
			return null;
		}
		password = element.getValue();
		
		return new LoginCredentials(user, password);
	}
	
	/**
	 * Same as {@link simplePersonToXml(Person person)}, exept all appointments
	 * are entered at the end with all information.
	 * @param person
	 * @return
	 */
	public static Element completePersonToXml(Person person) {
		Element personElem = simplePersonToXml(person);
		
		personElem.setLocalName("person");
		
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
		Element thePerson = new Element("person_simple");
		
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
	 * id's, start, and end.
	 * @param aRoom The room you want to parse into xml.
	 * @return A {@link nu.xom.Element} for the room.
	 */
	public static Element roomToXmlWithSimpleAppointment(Room aRoom) {
		Element room = roomToXml(aRoom);
		
		Element appointments = new Element("appointments");
		
		Iterator<Appointment> it = aRoom.getAppointmentIterator();
		
		while(it.hasNext()) {
			Appointment app = it.next();
			appointments.appendChild(simpleAppointmentToXml(app));
		}
		
		room.appendChild(appointments);
		
		return room;
	}
	
	/**
	 * Only returns id, start and end of appointments. Used by room to
	 * give information about when it is not available.
	 * @param app
	 * @return
	 */
	public static Element simpleAppointmentToXml(Appointment app) {
		Element root = new Element("appointment_simple");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(app.getID()));
		
		Element start = new Element("start");
		start.appendChild(Long.toString(app.getStart()));
		
		Element end = new Element("end");
		end.appendChild(Long.toString(app.getEnd()));
		
		root.appendChild(id);
		root.appendChild(start);
		root.appendChild(end);
		
		return root;
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
		Element group = new Element("group_simple");
		
		Element id = new Element("id");
		id.appendChild(Integer.toString(aGroup.getID()));
		
		Element name = new Element("name");
		name.appendChild(aGroup.getName());
		
		Element email = new Element("email");
		email.appendChild(aGroup.getEmail());
		
		group.appendChild(id);
		group.appendChild(name);
		
		return group;
	}
	
	public static Element groupToXml(Group aGroup) {
		Element group = simpleGroupToXml(aGroup);
		group.setLocalName("group");
		
		Element members = new Element("members");
		
		Iterator<Person> pIt = aGroup.getMembers().iterator();
		
		while(pIt.hasNext()) {
			Person person = pIt.next();
			
			members.appendChild(simplePersonToXml(person));
		}
		
		Element subgroups = new Element("subgroups");
		
		Iterator<Group> gIt = aGroup.getSubgroups().iterator();
		
		while(gIt.hasNext()) {
			Group grp = gIt.next();
			
			subgroups.appendChild(groupToXml(grp));
		}
		
		return group;
	}
	
	public Document notificationToXml(Notification notification) {
		Element not = new Element("notification");
		
		Element type = new Element("type");
		if(!notification.isCancelled() && !notification.isInvitation()) type.appendChild("removed");
		else if(notification.isCancelled() && !notification.isInvitation()) type.appendChild("declined");
		else if(!notification.isCancelled() && notification.isInvitation()) type.appendChild("invitation");
		else if(notification.isCancelled() && notification.isInvitation()) type.appendChild("cancellation");
		else type.appendChild("other");
		
		Element appID = new Element("app_id");
		appID.appendChild(Integer.toString(notification.getAppID()));
		
		Element message = new Element("message");
		message.appendChild(notification.getMessage());
		
		not.appendChild(type);
		not.appendChild(appID);
		not.appendChild(message);
		
		Document doc = new Document(not);
		
		return doc;
	}
	
	/**
	 * Very similar to assembleSimplePerson() except that it also assembles
	 * the person's appointments and places them into the person's
	 * appointments list
	 * @param xmlPersonElement
	 * @return
	 */
	public static Person assembleCompletePerson(Element xmlPersonElement) {
		Person person = assembleSimplePerson(xmlPersonElement);
		
		Element element = xmlPersonElement.getFirstChildElement("appointments");
		if(element == null) {
			System.err.println("Malformed xml element. No appointments tag in assemble complete person.");
			return null;
		}
		Elements appointments = element.getChildElements();
		
		for(int i = 0; i < appointments.size(); ++i) {
			Element app = appointments.get(i);
			if(app.getLocalName().equals("meeting")) {
				Meeting meeting = assembleMeeting(app);
				person.addAppointment(meeting);
			} else if(app.getLocalName().equals("appointment")) {
				Appointment appointment = assembleAppointment(app);
				person.addAppointment(appointment);
			}
		}
		
		return person;
	}
	
	public static Element idToXml(int id, char type) {
		Element ret = new Element("return");
		
		Element idEl;
		
		switch(type) {
		case 'a': case 'm':
			idEl = new Element("app_id");
			break;
		case 'r':
			idEl = new Element("room_id");
			break;
		default:
			System.err.println("Type not supported in idToXml");
			return null;
		}
			
		idEl.appendChild(Integer.toString(id));
		ret.appendChild(idEl);
		
		return ret;
	}
	
	public Element alarmToXml(Alarm alarm) {
		Element root = new Element("alarm");
		
		Element email = new Element("email");
		email.appendChild(alarm.getEmail());
		
		Element appId = new Element("app_id");
		appId.appendChild(Integer.toString(alarm.getAppId()));
		
		Element msg = new Element("message");
		msg.appendChild(alarm.getMsg());
		
		Element time = new Element("time");
		time.appendChild(Long.toString(alarm.getStartAlarm().getTime()));
		
		root.appendChild(email);
		root.appendChild(appId);
		root.appendChild(msg);
		
		return root;
	}
	
	public Alarm assembleAlarm(Element xmlAlarmElement) {
		if( ! xmlAlarmElement.getLocalName().equals("alarm")) {
			System.err.println("Malformed xml element. This is not an alarm");
			return null;
		}
		
		int app_id;
		String email, message;
		Timestamp start;
		
		
		Element el = xmlAlarmElement.getFirstChildElement("email");
		if(el == null) {
			System.err.println("Malformed xml element. No email while assembling alarm");
			return null;
		}
		email = el.getValue();
		
		el = xmlAlarmElement.getFirstChildElement("app_id");
		if(el == null) {
			System.err.println("Malformed xml element. No app_id while assembling alarm");
			return null;
		}
		app_id = Integer.parseInt(el.getValue());
		
		el = xmlAlarmElement.getFirstChildElement("message");
		if(el == null) {
			System.err.println("Malformed xml element. No message while assembling alarm");
			return null;
		}
		message = el.getValue();
		
		el = xmlAlarmElement.getFirstChildElement("start");
		if(el == null) {
			System.err.println("Malformed xml element. No start while assembling alarm");
			return null;
		}
		start = new Timestamp(Long.parseLong(el.getValue()));
		
		return new Alarm(message, email, app_id, start);
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
		
		Element element = xmlPersonElement.getFirstChildElement("first_name");
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
		
		element = xmlPersonElement.getFirstChildElement("email");
		if(element == null) {
			System.err.println("Malformed xml element. No email while assembling simple person");
			return null;
		}
		email = element.getValue();
		
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
		String name, email;
		
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
		
		element = xmlGroupElement.getFirstChildElement("email");
		if(element == null) {
			System.err.println("Malformed xml element. No email in assemble simple group");
			return null;
		}
		email = element.getValue();
		
		return new Group(id, name, email);
	}
	
	/**
	 * Assembles a meeting from a xom element.
	 * @param xmlMeetingElement
	 * @return
	 */
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
		if(registeredBy == null) {
			System.err.println("Malformed xml element. Assemble simple person returned null while assembling registered_by in meeting");
			return null;
		}
		
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
			if(room == null) {
				System.err.println("Malformed xml element. Room assembly returned null while assembling meeting");
				return null;
			}
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
			if(per == null) {
				System.err.println("Malformed xml element. Assemble simple person returned null while assembling meeting.");
				return null;
			}
			attendees.add(per);
		}
		
		groupAttendees = new ArrayList<Group>();
		Element grpAtt = element.getFirstChildElement("groups_attending");
		Elements groups = grpAtt.getChildElements();
		for(int i = 0; i < groups.size(); ++i) {
			grpAtt = groups.get(i);
			Group grp = assembleSimpleGroup(grpAtt);
			if(grp == null) {
				System.err.println("Malformed xml element. Assemble simple group returned null while assembling meeting");
				return null;
			}
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
		if(registeredBy == null) {
			System.err.println("Malformed xml element. Assemble simple person returned null while assembling registered_by in appointment");
			return null;
		}
		
		
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
			if(room == null) {
				System.err.println("Malformed xml element. Assemble room returned null while assembling appointment");
			}
		}
		
		Appointment app = new Appointment(id, start, end, name, descr, registeredBy);
		if(roomDescr != null) app.setRoomDescr(roomDescr);
		if(room != null) app.setRoom(room);
		
		return app;
		
	}
	
	/**
	 * Assembles a room from an xml element. Returns null if the element is
	 * malformed. If the xmlElement has appointment id's, it will add them
	 * to the assembled room, but only with id, start and end. If there are
	 * no appointments the list will be empty.
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
		
		ArrayList<Appointment> apps = new ArrayList<Appointment>();
		
		element = xmlRoomElement.getFirstChildElement("appointments");
		if(element != null) {
			Elements appElems = element.getChildElements();
			for(int i = 0; i < appElems.size(); ++i) {
				Element app = appElems.get(i);
				
				int app_id;
				long start, end;
				
				Element el = app.getFirstChildElement("app_id");
				if(el == null) {
					continue;
				}
				app_id = Integer.parseInt(el.getValue());
				
				el = app.getFirstChildElement("start");
				if(el == null) {
					continue;
				}
				start = Long.parseLong(el.getValue());
				
				el = app.getFirstChildElement("end");
				if(el == null) {
					continue;
				}
				end = Long.parseLong(el.getValue());
				
				apps.add(new Appointment(app_id, new Timestamp(start), new Timestamp(end), null, null, null));
			}
		}
		
		return new Room(id, space, name, apps);
	}
	
	/**
	 * Assembles a notification from an xml element. Returns null if it is not a
	 * valid element for a notification.
	 * @param xmlNotificationElement
	 * @return
	 */
	public static Notification assembleNotification(Element xmlNotificationElement) {
		if(xmlNotificationElement.getLocalName() != "notification") {
			System.err.println("Malformed xml element. Not a notification");
			return null;
		}
		
		int appID;
		String message;
		boolean cancelled;
		boolean invitation;
		
		Element el = xmlNotificationElement.getFirstChildElement("app_id");
		if(el == null) {
			System.err.println("Malformed xml element. No app_id while assembling notification");
			return null;
		}
		appID = Integer.parseInt(el.getValue());
		
		el = xmlNotificationElement.getFirstChildElement("type");
		if(el == null) {
			System.err.println("Malformed xml element. No type while assembling notification");
			return null;
		}
		String value = el.getValue();
		if(value.equals("cancelled")) {
			cancelled = true;
			invitation = true;
		}
		else if(value.equals("invitation")) {
			invitation = true;
			cancelled = false;
		} else if(value.equals("declined")) {
			cancelled = true;
			invitation = false;
		} else {
			cancelled = false;
			invitation = false;
		}
		
		el = xmlNotificationElement.getFirstChildElement("message");
		if(el == null) {
			System.err.println("Malformed xml element. No message while assembling notification");
			return null;
		}
		message = el.getValue();
		
		Notification notification = new Notification(appID, invitation, cancelled, message);
		
		return notification;
	}
	
	public static Element getAppsByIdXml(int[] ids) {
		Element root = new Element("get");
		
		for(int i = 0; i < ids.length; ++i) {
			Element app = new Element("app_id");
			app.appendChild(Integer.toString(ids[i]));
			
			root.appendChild(root);
		}
		
		return root;
	}
	
	public static Element getPersonByEmailXml(String[] emails) {
		Element root = new Element("get");
		for(int i = 0; i < emails.length; ++i) {
			Element person = new Element("person_email");
			person.appendChild(emails[i]);
			
			root.appendChild(person);
		}
		
		return root;
	}
	
	public static Element getGroupByIdXml(int[] ids) {
		Element root = new Element("get");
		
		for(int i = 0; i < ids.length; ++i) {
			Element grp = new Element("group_id");
			grp.appendChild(Integer.toString(ids[i]));
			
			root.appendChild(grp);
		}
		
		return root;
	}
}
