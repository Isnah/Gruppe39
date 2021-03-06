/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.Converters;
import client.Group;
import client.Meeting;
import client.Person;
import client.Room;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Time;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Laxcor
 */
public class AppointmentEditor extends javax.swing.JFrame {

    private MainWindow frame;
    private Meeting model;
    
    private DefaultListModel<ListUnit> notInvited;
    private DefaultListModel<ListUnit> invited;
    private DefaultTableModel roomTableModel;
    private ListSelectionModel roomTableSelectionModel;
    
    
    private Room selectedRoom = null;
    private boolean newAppointment = false;
    private boolean roomReserved = false;
    private Time oldStart, oldEnd;
    private NumberFormat f;
    /**
     * Creates new form AppointmentEditor
     */
    public AppointmentEditor(MainWindow frame) {
        newAppointment = true;
        model = new Meeting(0, new Time(new Date().getTime()), new Time(new Date().getTime()), 
                                "", "", frame.getPersonByEmail(frame.getEmail()), null, null);
        model.setRoomDescr("Type in a description...");
        init(frame);
        update();
    }
    
    /**
     * Constructor with an already existing model
     * @param model 
     */
    public AppointmentEditor(MainWindow frame, Meeting model) {
        this.model = model;
        init(frame);
        update();
    }
    
    /**
     * General init
     */
    private void init(MainWindow frame) {
        this.frame = frame;
        
        f = NumberFormat.getInstance();
        f.setMaximumIntegerDigits(2);
        f.setMinimumIntegerDigits(1);
        
        initComponents();
        setLocationRelativeTo(null);
        dateChooser.setSelectableDateRange(new Date(), null);
        roomTable.setVisible(false);
        
        oldStart = new Time(model.getStart());
        oldEnd = new Time(model.getEnd());
        
        invited = new DefaultListModel<>();
        for (Person person : model.getAttendees()) {
            invited.addElement(new ListUnit(person.getEmail(), person.getFirstName() + " " + person.getLastName()));
        }
        for (Group group : model.getGroupAttendees()) {
            invited.addElement(new ListUnit(group.getId(), group.getName()));
        }
        invitedList.setModel(invited);
        
        setRoomTableModel();
        roomTable.setColumnSelectionAllowed(false);
        // TODO Fix editable
        
        notInvited = new DefaultListModel<>();
        
        for (ListUnit lu : frame.getAllInvitable()) {
            if (!invited.contains(lu)) {
                notInvited.addElement(lu);
            }
        }
        
        notInvitedList.setModel(notInvited);
        
        roomTableSelectionModel = new DefaultListSelectionModel();
        roomTableSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.setSelectionModel(roomTableSelectionModel);
        roomTableSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                setRoom();
            }
        });
        dateChooser.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateRoomTableModel();
            }
        });
    }
    /**
     * Updates all the fields
     */
    private void update() {
        titleField.setText(model.getName());
        descField.setText(model.getDescr());
        dateChooser.setDate(new Date(model.getStart()));
        whereField.setText(getRoom());
        timeFromHH.setValue(Integer.parseInt(model.getTimeFormat().subSequence(0, 2).toString()));
        timeFromMM.setValue(Integer.parseInt(model.getTimeFormat().subSequence(3, 5).toString()));
        timeToHH.setValue(Integer.parseInt(model.getTimeFormat().subSequence(6, 8).toString()));
        timeToMM.setValue(Integer.parseInt(model.getTimeFormat().subSequence(9, 11).toString()));
        if (model.getRoom() != null) {
            roomReserved = true;
            selectedRoom = model.getRoom();
            setReservation(true);
            removeRoomButton.setEnabled(true);
            whereField.setEnabled(false);
        }
    }
    private void setRoomTableModel() {
        roomTableModel = new DefaultTableModel(getTableDataNoOccupied(), new Object[] {"Room","Capacity","Occupied"});
        roomTable.setModel(roomTableModel);
    }
    private void updateRoomTableModel() {
        roomTableModel = new DefaultTableModel(getTableData(), new Object[] {"Room","Capacity","Occupied"});
        roomTable.setModel(roomTableModel);
    }
    private void setRoom() {
        if (roomTable.getValueAt(roomTable.getSelectedRow(), 2).equals("No")) {
            whereField.setText((String)roomTable.getValueAt(roomTable.getSelectedRow(), 0));
            ArrayList<Room> allRooms = frame.getAllRooms();
            for (Room room : allRooms) {
                if (room.getName().equals(roomTable.getValueAt(roomTable.getSelectedRow(), 0))) {
                    selectedRoom = room;
                }
            }
            roomReserved = true;
    }   }
    /**
     * 
     */
    private void saveChanges() {
         
        model.setName(titleField.getText());
        model.setDescr(descField.getText());
        
        GregorianCalendar date = (GregorianCalendar)dateChooser.getCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        
        model.setStart(date.getTimeInMillis() + Converters.HHMMToMilliseconds(getHHFrom(), getMMFrom()));
        model.setEnd(date.getTimeInMillis() + Converters.HHMMToMilliseconds(getHHTo(), getMMTo()));
        
        for (int i=0;i<invited.getSize();i++) {
            if (invited.elementAt(i).isGroup()) {
                model.addGroupAttendee(frame.getGroupById(invited.getElementAt(i).getId()));
            }
            else {
                model.addAttendee(frame.getPersonByEmail(invited.getElementAt(i).getEmail()));
            }
        }
        if (roomReserved) {
            model.setRoom(selectedRoom);
        }
        else {
            model.setRoomDescr(whereField.getText());
        }
        
        if (newAppointment) {
            frame.newAppointment(model);
            frame.showInformationPanel(model);
        }
        else {
            if (oldStart.getTime() != model.getStart() || oldEnd.getTime() != model.getEnd()) {
                frame.deleteAppointment(model);
                frame.newAppointment(model);
            }
            else {
                frame.editAppointment(model);
            }
        }
        dispose();
    }
    
    private void setReservation(boolean enabled) {
        roomTable.setVisible(enabled);
        removeRoomButton.setEnabled(enabled);
        whereField.setEnabled(!enabled);
    }
    private int getHHFrom() {
        return (int)timeFromHH.getValue();
    }
    private int getMMFrom() {
        return (int)timeFromMM.getValue();
    }
    private int getHHTo() {
        return (int)timeToHH.getValue();
    }
    private int getMMTo() {
        return (int)timeToMM.getValue();
    }
    private String getRoom() {
        if (model.getRoom() != null) {
            roomReserved = true;
            return model.getRoom().getName();
        }
        else {
            return model.getRoomDescr();
        }
    }
    private Object[][] getTableDataNoOccupied() {
        Object[][] data = new Object[frame.getAllRooms().size()][];
        ArrayList<Room> roomList = frame.getAllRooms();
        for (int i=0;i<roomList.size();i++) {
        	Object[] info = {roomList.get(i).getName(), roomList.get(i).getSpace(), "Set the time"};
            data[i] = info;
        }
            return data;
    }    
    private Object[][] getTableData() {
        Object[][] data = new Object[frame.getAllRooms().size()][];
        ArrayList<Room> roomList = frame.getAllRooms();
        for (int i=0;i<roomList.size();i++) {
            data[i] = getRoomList(roomList.get(i));
        }
            return data;
    }
    private Object[] getRoomList(Room room) {
    	Object[] roomInfo = {room.getName(), room.getSpace(), getRoomStatus(room)};
    	return roomInfo;
    }
    
    private String getRoomStatus(Room room){

    	GregorianCalendar date = (GregorianCalendar)dateChooser.getCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    	
    	long start = date.getTimeInMillis() + Converters.HHMMToMilliseconds(getHHFrom(), getMMFrom());
        long end = date.getTimeInMillis() + Converters.HHMMToMilliseconds(getHHTo(), getMMTo());
    	
    	if (room.isAvailable(start, end)) return "No";
    	else return "Yes";
    	
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        leftPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        descLabel = new javax.swing.JLabel();
        dateLabel = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        whereLabel = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        dateChooser = new com.toedter.calendar.JDateChooser();
        timePanel = new javax.swing.JPanel();
        timeFromLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        timeToLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        timeToMM = new javax.swing.JSpinner();
        timeFromHH = new javax.swing.JSpinner();
        timeFromMM = new javax.swing.JSpinner();
        timeToHH = new javax.swing.JSpinner();
        whereField = new javax.swing.JTextField();
        removeRoomButton = new javax.swing.JButton();
        reserveRoomButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        descField = new javax.swing.JTextArea();
        rightPanel = new javax.swing.JPanel();
        innerRightPanel = new javax.swing.JPanel();
        participantsLabel = new javax.swing.JLabel();
        invitePanel = new javax.swing.JPanel();
        invitedListArea = new javax.swing.JScrollPane();
        invitedList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        notInvitedListArea = new javax.swing.JScrollPane();
        notInvitedList = new javax.swing.JList();
        roomListScrollPane = new javax.swing.JScrollPane();
        roomTable = new javax.swing.JTable();
        appointmentEditorLabel = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(200, 230, 230));

        leftPanel.setBackground(new java.awt.Color(200, 230, 230));

        titleLabel.setText("Title");

        descLabel.setText("Description");

        dateLabel.setText("Date");

        timeLabel.setText("When");

        whereLabel.setText("Where");

        timePanel.setOpaque(false);

        timeFromLabel.setText("From");

        jLabel9.setText(":");

        timeToLabel.setText("To");

        jLabel12.setText(":");

        timeToMM.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        timeToMM.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeToMMStateChanged(evt);
            }
        });

        timeFromHH.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        timeFromHH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeFromHHStateChanged(evt);
            }
        });

        timeFromMM.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));
        timeFromMM.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeFromMMStateChanged(evt);
            }
        });

        timeToHH.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));
        timeToHH.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                timeToHHStateChanged(evt);
            }
        });

        javax.swing.GroupLayout timePanelLayout = new javax.swing.GroupLayout(timePanel);
        timePanel.setLayout(timePanelLayout);
        timePanelLayout.setHorizontalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createSequentialGroup()
                .addComponent(timeFromLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeFromHH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeFromMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(timeToLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeToHH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(timeToMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        timePanelLayout.setVerticalGroup(
            timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(timeFromLabel)
                .addComponent(jLabel9)
                .addComponent(timeToLabel)
                .addComponent(jLabel12)
                .addComponent(timeToMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(timeFromHH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(timeFromMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(timeToHH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        whereField.setText("Type in a description...");

        removeRoomButton.setText("X");
        removeRoomButton.setEnabled(false);
        removeRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeRoomButtonActionPerformed(evt);
            }
        });

        reserveRoomButton.setText("... or reserve a room");
        reserveRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reserveRoomButtonActionPerformed(evt);
            }
        });

        descField.setColumns(20);
        descField.setRows(5);
        descField.setMinimumSize(new java.awt.Dimension(3, 18));
        jScrollPane4.setViewportView(descField);

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleLabel)
                            .addComponent(dateLabel)
                            .addComponent(descLabel)
                            .addComponent(timeLabel))
                        .addGap(55, 55, 55)
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(timePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane4)
                            .addComponent(titleField)
                            .addComponent(dateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, leftPanelLayout.createSequentialGroup()
                        .addComponent(whereLabel)
                        .addGap(76, 76, 76)
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(whereField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeRoomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(reserveRoomButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(titleLabel))
                .addGap(9, 9, 9)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descLabel)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(whereLabel)
                    .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(removeRoomButton)
                        .addComponent(whereField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reserveRoomButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rightPanel.setBackground(new java.awt.Color(200, 230, 230));

        innerRightPanel.setOpaque(false);

        participantsLabel.setText("Participants");

        invitePanel.setOpaque(false);

        invitedList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        invitedListArea.setViewportView(invitedList);

        addButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        addButton.setText("Add --->");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
        removeButton.setText("<--- Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        notInvitedList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        notInvitedListArea.setViewportView(notInvitedList);

        javax.swing.GroupLayout invitePanelLayout = new javax.swing.GroupLayout(invitePanel);
        invitePanel.setLayout(invitePanelLayout);
        invitePanelLayout.setHorizontalGroup(
            invitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(invitePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(notInvitedListArea, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(invitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invitedListArea, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        invitePanelLayout.setVerticalGroup(
            invitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(invitePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(invitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(invitedListArea, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(notInvitedListArea, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(invitePanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton)
                .addGap(0, 44, Short.MAX_VALUE))
        );

        roomTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Room", "Capacity", "Occupied"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        roomListScrollPane.setViewportView(roomTable);

        javax.swing.GroupLayout innerRightPanelLayout = new javax.swing.GroupLayout(innerRightPanel);
        innerRightPanel.setLayout(innerRightPanelLayout);
        innerRightPanelLayout.setHorizontalGroup(
            innerRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, innerRightPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(innerRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(participantsLabel)
                    .addGroup(innerRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(roomListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(invitePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        innerRightPanelLayout.setVerticalGroup(
            innerRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(innerRightPanelLayout.createSequentialGroup()
                .addComponent(participantsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invitePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roomListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(innerRightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(innerRightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        appointmentEditorLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        appointmentEditorLabel.setText("Appointment Editor");

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(rightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(appointmentEditorLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appointmentEditorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        saveChanges();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void reserveRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reserveRoomButtonActionPerformed
        updateRoomTableModel();
        setReservation(true);
    }//GEN-LAST:event_reserveRoomButtonActionPerformed

    private void timeToHHStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_timeToHHStateChanged
        if ((int)timeToHH.getValue() < (int)timeFromHH.getValue()) {
            timeFromHH.setValue(timeToHH.getValue());
        }
        timeToMMStateChanged(null);
    }//GEN-LAST:event_timeToHHStateChanged

    private void timeFromHHStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_timeFromHHStateChanged
        if ((int)timeToHH.getValue() < (int)timeFromHH.getValue()) {
            timeToHH.setValue(timeFromHH.getValue());
        }
        timeFromMMStateChanged(null);
    }//GEN-LAST:event_timeFromHHStateChanged

    private void timeFromMMStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_timeFromMMStateChanged
        if ((int)timeToHH.getValue() == (int)timeFromHH.getValue()) {
            if ((int)timeToMM.getValue() < (int)timeFromMM.getValue()) {
                timeToMM.setValue(timeFromMM.getValue());
            }
        }
        roomReserved = false;
        setReservation(false);
        updateRoomTableModel();
    }//GEN-LAST:event_timeFromMMStateChanged

    private void timeToMMStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_timeToMMStateChanged
        if ((int)timeToHH.getValue() == (int)timeFromHH.getValue()) {
            if ((int)timeToMM.getValue() < (int)timeFromMM.getValue()) {
                timeFromMM.setValue(timeToMM.getValue());
            }
        }
        roomReserved = false;
        setReservation(false);
        updateRoomTableModel();
    }//GEN-LAST:event_timeToMMStateChanged

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        while (notInvitedList.getSelectedIndex() != -1) {
            invited.addElement(notInvited.getElementAt(notInvitedList.getSelectedIndex()));
            notInvited.removeElementAt(notInvitedList.getSelectedIndex());
        }
        
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        while (invitedList.getSelectedIndex() != -1){
        notInvited.addElement(invited.getElementAt(invitedList.getSelectedIndex()));
        invited.removeElementAt(invitedList.getSelectedIndex());
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void removeRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeRoomButtonActionPerformed
        setReservation(false);
        roomReserved = false;
    }//GEN-LAST:event_removeRoomButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel appointmentEditorLabel;
    private javax.swing.JButton cancelButton;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JTextArea descField;
    private javax.swing.JLabel descLabel;
    private javax.swing.JPanel innerRightPanel;
    private javax.swing.JPanel invitePanel;
    private javax.swing.JList invitedList;
    private javax.swing.JScrollPane invitedListArea;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JList notInvitedList;
    private javax.swing.JScrollPane notInvitedListArea;
    private javax.swing.JLabel participantsLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton removeRoomButton;
    private javax.swing.JButton reserveRoomButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane roomListScrollPane;
    private javax.swing.JTable roomTable;
    private javax.swing.JButton saveButton;
    private javax.swing.JSpinner timeFromHH;
    private javax.swing.JLabel timeFromLabel;
    private javax.swing.JSpinner timeFromMM;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JPanel timePanel;
    private javax.swing.JSpinner timeToHH;
    private javax.swing.JLabel timeToLabel;
    private javax.swing.JSpinner timeToMM;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField whereField;
    private javax.swing.JLabel whereLabel;
    // End of variables declaration//GEN-END:variables
}
