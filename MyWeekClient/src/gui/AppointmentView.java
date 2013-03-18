/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.Meeting;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author Laxcor
 */
public class AppointmentView extends javax.swing.JPanel implements PropertyChangeListener, MouseListener {
    
    private MainWindow frame;
    private Meeting model;
    
    
    /**
     * Creates new form AppointmentView
     */
    public AppointmentView(MainWindow frame,Meeting model) {
        initComponents();
        
        this.frame = frame;
        
        addMouseListener(this);
        
        this.model = model;
        model.addPropertyChangeListener(this);
        
        appointmentName.setText(model.getName());
        appointmentTime.setText(model.getTimeFormat());
        appointmentRoom.setText(getRoom());
        appointmentStatus.setText("");
        alarmIcon.setVisible(false);
        
        setPosition();
    }
    public Meeting getModel() {
        return model;
    }
    private String getRoom() {
        if (model.getRoom() != null) {
            return model.getRoom().getName();
        }
        else { 
            return model.getRoomDescr();
        }
    }
    private void setPosition() {
        int x, width, durationH, startH, startM;
        float y, durationM, height;
        startH = Integer.parseInt(model.getTimeFormat().substring(0, 2));
        startM = Integer.parseInt(model.getTimeFormat().substring(3, 5));
        durationH = Integer.parseInt(model.getTimeFormat().substring(6, 8)) - startH;
        durationM = Integer.parseInt(model.getTimeFormat().substring(9, 11)) - startM;
        x = 2;
        y = 38*startH + (38f/60)*startM; //*hours and minutes from 00:00
        width = 110;
        height = getDuration(durationH, durationM);
        System.out.println(height);
        setBounds(x, (int)y, width, (int)height);
    }
    private int getDuration(int durH, float durM) {
        float actualDuration = 38*durH + (38f/60)*durM;
        if (actualDuration < 38) {
            return 38;
        }
        else {
            return (int)actualDuration;
        }
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Meeting.NAME:
                appointmentName.setText(model.getName());
                break;
            case Meeting.START:
                appointmentTime.setText(model.getTimeFormat());
                setPosition();
                break;
            case Meeting.END:
                appointmentTime.setText(model.getTimeFormat());
                setPosition();
                break;
            case Meeting.ROOM:
                appointmentRoom.setText(getRoom());
                break;
            case Meeting.ALARMS:
                if (model.getAlarmList().isEmpty()) {
                    alarmIcon.setVisible(false);
                }
                else {
                    alarmIcon.setVisible(true);
                }
                break;
            default:
        }
        repaint();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        frame.showInformationPanel(model);
                    }
                });
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        appointmentName = new javax.swing.JLabel();
        alarmIcon = new javax.swing.JLabel();
        appointmentTime = new javax.swing.JLabel();
        appointmentRoom = new javax.swing.JLabel();
        appointmentStatus = new javax.swing.JLabel();

        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        setPreferredSize(new java.awt.Dimension(112, 38));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        appointmentName.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        appointmentName.setText("<Name>");
        add(appointmentName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        alarmIcon.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        alarmIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/alarm.png"))); // NOI18N
        add(alarmIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 0, -1, -1));

        appointmentTime.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        appointmentTime.setText("<Timestamp>");
        add(appointmentTime, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 10, -1, -1));

        appointmentRoom.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        appointmentRoom.setText("<Room nr.>");
        add(appointmentRoom, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, -1, -1));

        appointmentStatus.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        add(appointmentStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 20, -1, -1));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel alarmIcon;
    private javax.swing.JLabel appointmentName;
    private javax.swing.JLabel appointmentRoom;
    private javax.swing.JLabel appointmentStatus;
    private javax.swing.JLabel appointmentTime;
    // End of variables declaration//GEN-END:variables

}
