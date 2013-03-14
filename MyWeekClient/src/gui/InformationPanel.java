/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import client.Meeting;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author Laxcor
 */
public class InformationPanel extends javax.swing.JPanel implements PropertyChangeListener {

    private MainWindow frame;
    private Meeting model;
    /**
     * Creates new form InformationPanel
     */
    public InformationPanel() {
    }
    
    public InformationPanel(MainWindow frame) {
        super();
        this.frame = frame;
        initComponents();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                confirmRemovePanel.setVisible(false);
            }
        });
    }

    public void setModel(Meeting model) {
        this.model = model;
        model.addPropertyChangeListener(this);
        update();
    }
    private void update() {
        name.setText(model.getName());
        desc.setText(model.getDescr());
        time.setText(model.getTimeFormat());
        date.setText(model.getDateFormat());
        location.setText(getRoom());
        alarmList.setModel(model.getAlarmList());
    }
    private String getRoom() {
        if (model.getRoom() != null) {
            return model.getRoom().getName();
        }
        else {
            return model.getRoomDescr();
        }
    }
    public void hideConfirmation() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                confirmRemovePanel.setVisible(false);
            }
        });
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Meeting.NAME:
                name.setText(model.getName());
                break;
            case Meeting.DESCR:
                desc.setText(model.getDescr());
                break;
            case Meeting.START:
                time.setText(model.getTimeFormat());
                break;
            case Meeting.END:
                time.setText(model.getTimeFormat());
                break;
            case Meeting.ROOM:
                location.setText(getRoom());
                break;
            default:
        }
        repaint();
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        name = new javax.swing.JLabel();
        desc = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        location = new javax.swing.JLabel();
        infoSeparator1 = new javax.swing.JSeparator();
        infoSeparator2 = new javax.swing.JSeparator();
        removeAppointmentButton = new javax.swing.JButton();
        editAppointmentButton = new javax.swing.JButton();
        confirmRemovePanel = new javax.swing.JPanel();
        confirmationLabel = new javax.swing.JLabel();
        confirmYes = new javax.swing.JButton();
        confirmNo = new javax.swing.JButton();
        alarmLabel = new javax.swing.JLabel();
        participantsLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        addAlarmButton = new javax.swing.JButton();
        alarmListScrollPane = new javax.swing.JScrollPane();
        alarmList = new javax.swing.JList();
        participantsScrollPane = new javax.swing.JScrollPane();
        oarticipantList = new javax.swing.JList();
        removeAlarmButton = new javax.swing.JButton();
        jSpinner1 = new javax.swing.JSpinner();
        jSpinner2 = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(200, 230, 230));

        name.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        name.setText("<name>");

        desc.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        desc.setText("<description>");

        time.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        time.setText("<time from-to>");

        date.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        date.setText("<date>");

        location.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        location.setText("<location>");

        infoSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        infoSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        removeAppointmentButton.setText("Remove Appointment");
        removeAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAppointmentButtonActionPerformed(evt);
            }
        });

        editAppointmentButton.setText("Edit Appointment");
        editAppointmentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editAppointmentButtonActionPerformed(evt);
            }
        });

        confirmRemovePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));

        confirmationLabel.setText("<html>Are you sure you want to <br> remove this appointment?<html>");

        confirmYes.setText("Yes");
        confirmYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmYesActionPerformed(evt);
            }
        });

        confirmNo.setText("No");
        confirmNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmNoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout confirmRemovePanelLayout = new javax.swing.GroupLayout(confirmRemovePanel);
        confirmRemovePanel.setLayout(confirmRemovePanelLayout);
        confirmRemovePanelLayout.setHorizontalGroup(
            confirmRemovePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmRemovePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(confirmRemovePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(confirmRemovePanelLayout.createSequentialGroup()
                        .addComponent(confirmYes)
                        .addGap(26, 26, 26)
                        .addComponent(confirmNo, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(confirmationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        confirmRemovePanelLayout.setVerticalGroup(
            confirmRemovePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmRemovePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(confirmationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(confirmRemovePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(confirmYes)
                    .addComponent(confirmNo))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        alarmLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        alarmLabel.setText("Alarms");

        participantsLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        participantsLabel.setText("Participants");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        addAlarmButton.setText("Add");

        alarmList.setBackground(new java.awt.Color(200, 230, 230));
        alarmListScrollPane.setViewportView(alarmList);

        oarticipantList.setBackground(new java.awt.Color(200, 230, 230));
        participantsScrollPane.setViewportView(oarticipantList);

        removeAlarmButton.setText("Remove");

        jLabel1.setText("h");

        jLabel2.setText("min");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(name, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(desc, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(location, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(time))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(participantsLabel)
                    .addComponent(participantsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(infoSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(removeAlarmButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(alarmListScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(addAlarmButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(alarmLabel)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(confirmRemovePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeAppointmentButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editAppointmentButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(editAppointmentButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeAppointmentButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmRemovePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(infoSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(participantsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(participantsScrollPane))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(name)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(desc, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(time)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(date)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(location))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(alarmLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addAlarmButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(alarmListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeAlarmButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAppointmentButtonActionPerformed
        confirmRemovePanel.setVisible(true);
    }//GEN-LAST:event_removeAppointmentButtonActionPerformed

    private void confirmYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmYesActionPerformed
        hideConfirmation();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.hideInformationPanel();
            }
        });
    }//GEN-LAST:event_confirmYesActionPerformed

    private void confirmNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmNoActionPerformed
        hideConfirmation();
    }//GEN-LAST:event_confirmNoActionPerformed

    private void editAppointmentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAppointmentButtonActionPerformed
        new AppointmentEditor(frame, model).setVisible(true);
    }//GEN-LAST:event_editAppointmentButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAlarmButton;
    private javax.swing.JLabel alarmLabel;
    private javax.swing.JList alarmList;
    private javax.swing.JScrollPane alarmListScrollPane;
    private javax.swing.JButton confirmNo;
    private javax.swing.JPanel confirmRemovePanel;
    private javax.swing.JButton confirmYes;
    private javax.swing.JLabel confirmationLabel;
    private javax.swing.JLabel date;
    private javax.swing.JLabel desc;
    private javax.swing.JButton editAppointmentButton;
    private javax.swing.JSeparator infoSeparator1;
    private javax.swing.JSeparator infoSeparator2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JLabel location;
    private javax.swing.JLabel name;
    private javax.swing.JList oarticipantList;
    private javax.swing.JLabel participantsLabel;
    private javax.swing.JScrollPane participantsScrollPane;
    private javax.swing.JButton removeAlarmButton;
    private javax.swing.JButton removeAppointmentButton;
    private javax.swing.JLabel time;
    // End of variables declaration//GEN-END:variables
}
