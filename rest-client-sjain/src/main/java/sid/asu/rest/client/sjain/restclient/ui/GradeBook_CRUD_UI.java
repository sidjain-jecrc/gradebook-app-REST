/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.client.sjain.restclient.ui;

import com.sun.jersey.api.client.ClientResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sid.asu.rest.client.sjain.jaxb.model.GradeBookItem;
import sid.asu.rest.client.sjain.jaxb.model.Student;
import sid.asu.rest.client.sjain.jaxb.utils.Converter;
import sid.asu.rest.client.sjain.restclient.GradeBook_CRUD_Client;

/**
 *
 * @author Siddharth
 */
public class GradeBook_CRUD_UI extends javax.swing.JFrame {

    private static final Logger LOG = LoggerFactory.getLogger(GradeBook_CRUD_UI.class);
    private GradeBook_CRUD_Client gradeBook_CRUD_client;
    private URI resourceURI;
    private static final int ASSIGNMENTS = 500;
    private static final int MID_TERM = 501;
    private static final int FINAL_EXAM = 502;

    /**
     * Creates new form GradeBook_CRUD_UI
     */
    public GradeBook_CRUD_UI() {
        initComponents();
        gradeBook_CRUD_client = new GradeBook_CRUD_Client();
        jLabelError.setVisible(false);
        jLabelEmptyError.setVisible(false);
    }

    private String convertFormToXMLString() {

        GradeBookItem gradeBookItem = new GradeBookItem();
        Object selectedItem = jComboBoxGradeItems.getSelectedItem();
        if (selectedItem != null) {
            String selectedItemStr = selectedItem.toString();
            switch (selectedItemStr) {
                case "Assignment (30%)":
                    LOG.debug("<----Assignments selected---->");

                    gradeBookItem.setItemId(ASSIGNMENTS);
                    gradeBookItem.setItemName("Assignments");
                    break;
                case "Mid Term (30%)":
                    LOG.debug("<----Midterm selected---->");

                    gradeBookItem.setItemId(MID_TERM);
                    gradeBookItem.setItemName("Midterm");
                    break;
                case "Final Exam (40%)":
                    LOG.debug("<----Final exam selected---->");

                    gradeBookItem.setItemId(FINAL_EXAM);
                    gradeBookItem.setItemName("Final Exam");
                    break;
                default:
                    LOG.error("Something went wrong while fetching data from combox box");
                    break;
            }
        }
        try {
            String itemMaxScore = jTextFieldItemMaxScore.getText();
            String studentId = jTextFieldStudentId.getText();
            String studentScore = jTextFieldStudentScore.getText();
            String studentFeedback = jTextFieldFeedback.getText();

            List<Student> students = new ArrayList<>();
            Student student = new Student();

            if (jRadioButtonCreate.isSelected() || jRadioButtonUpdate.isSelected()) {
                if (!itemMaxScore.equals("") && !studentId.equals("") && !studentScore.equals("") && !studentFeedback.equals("")) {
                    gradeBookItem.setItemMax(Integer.parseInt(itemMaxScore));
                    student.setId(Integer.parseInt(studentId));
                    student.setScore(Integer.parseInt(studentScore));
                    student.setFeedback(studentFeedback);
                } else {
                    jLabelEmptyError.setVisible(true);
                }
            } else {
                if (!itemMaxScore.equals("")) {
                    gradeBookItem.setItemMax(Integer.parseInt(itemMaxScore));
                }
                if (!studentId.equals("")) {
                    student.setId(Integer.parseInt(studentScore));
                }
                if (!studentScore.equals("")) {
                    student.setScore(Integer.parseInt(studentScore));
                }
                if (!studentFeedback.equals("")) {
                    student.setFeedback(studentFeedback);
                }
            }

            students.add(student);
            gradeBookItem.setStudents(students);

        } catch (NumberFormatException e) {
            LOG.error("Error: " + e);
        }

        String xmlString = Converter.convertFromObjectToXml(gradeBookItem, GradeBookItem.class);
        return xmlString;
    }

    private void populateForm(ClientResponse clientResponse) {
        LOG.info("Populating the UI with the Gradebook info");

        String entity = clientResponse.getEntity(String.class);

        LOG.debug("The Client Response entity is {}", entity);

        try {
            if ((clientResponse.getStatus() == Response.Status.OK.getStatusCode())) {
                GradeBookItem gradeBookItem = (GradeBookItem) Converter.convertFromXmlToObject(entity, GradeBookItem.class);
                LOG.debug("The Client Response gradebook object is {}", gradeBookItem);

                // Populate gradebook info
                jTextFieldItemMaxScore.setText(Integer.toString(gradeBookItem.getItemMax()));
                List<Student> students = gradeBookItem.getStudents();
                for (Student student : students) {
                    if (student.getId() == Integer.parseInt(jTextFieldStudentId.getText())) {
                        jTextFieldStudentId.setText(Integer.toString(student.getId()));
                        jTextFieldStudentScore.setText(Integer.toString(student.getScore()));
                        jTextFieldFeedback.setText(student.getFeedback());
                    }
                }
            } else {
                jTextFieldItemMaxScore.setText("");
                jTextFieldStudentScore.setText("");
                jTextFieldFeedback.setText("");
            }

            // Populate HTTP Header Information
            jTextFieldHttpStatusCode.setText(Integer.toString(clientResponse.getStatus()));
            jTextFieldMediaType.setText(clientResponse.getType().toString());

            // The location field is only populated when a Resource is created
            if (clientResponse.getStatus() == Response.Status.CREATED.getStatusCode()) {
                jTextFieldResourceLocation.setText(clientResponse.getLocation().toString());
            } else {
                jTextFieldResourceLocation.setText("");
            }

        } catch (JAXBException e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupCrud = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jRadioButtonCreate = new javax.swing.JRadioButton();
        jRadioButtonRead = new javax.swing.JRadioButton();
        jRadioButtonUpdate = new javax.swing.JRadioButton();
        jRadioButtonDelete = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxGradeItems = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldItemMaxScore = new javax.swing.JTextField();
        jTextFieldStudentId = new javax.swing.JTextField();
        jTextFieldStudentScore = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldFeedback = new javax.swing.JTextField();
        jButtonCrudSubmit = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldHttpStatusCode = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldMediaType = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldResourceLocation = new javax.swing.JTextField();
        jButtonClear = new javax.swing.JButton();
        jLabelError = new javax.swing.JLabel();
        jLabelEmptyError = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("Action");

        buttonGroupCrud.add(jRadioButtonCreate);
        jRadioButtonCreate.setText("Create");

        buttonGroupCrud.add(jRadioButtonRead);
        jRadioButtonRead.setText("Read");

        buttonGroupCrud.add(jRadioButtonUpdate);
        jRadioButtonUpdate.setText("Update");
        jRadioButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonUpdateActionPerformed(evt);
            }
        });

        buttonGroupCrud.add(jRadioButtonDelete);
        jRadioButtonDelete.setText("Delete");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Grade Book Values");

        jLabel3.setText("Student ID :");

        jLabel4.setText("Student Score :");

        jComboBoxGradeItems.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Assignment (30%)", "Mid Term (30%)", "Final Exam (40%)" }));

        jLabel5.setText("Grading Item :");

        jLabel6.setText(" Max Score :");

        jTextFieldItemMaxScore.setToolTipText("Grading Item's Max Score");

        jTextFieldStudentId.setToolTipText("Student ID");

        jTextFieldStudentScore.setToolTipText("Student Score");

        jLabel7.setText("Feedback :");

        jTextFieldFeedback.setToolTipText("Feedback for Student");

        jButtonCrudSubmit.setText("Submit");
        jButtonCrudSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCrudSubmitActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel8.setText("HTTP Header Information");

        jLabel9.setText("HTTP Status Code :");

        jTextFieldHttpStatusCode.setToolTipText("HTTP Status Code");

        jLabel10.setText("Media Type :");

        jTextFieldMediaType.setToolTipText("Media Type");

        jLabel11.setText("Location :");

        jTextFieldResourceLocation.setToolTipText("Resouce Location");

        jButtonClear.setText("Clear");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        jLabelError.setForeground(new java.awt.Color(255, 0, 51));
        jLabelError.setText("Choose Action");

        jLabelEmptyError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelEmptyError.setText("Fill required fields");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(120, 120, 120)
                        .addComponent(jLabel8))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldHttpStatusCode, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                                .addComponent(jTextFieldMediaType))
                            .addComponent(jTextFieldResourceLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(50, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButtonRead)
                            .addComponent(jRadioButtonCreate)
                            .addComponent(jRadioButtonDelete)
                            .addComponent(jRadioButtonUpdate)
                            .addComponent(jLabelError)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldStudentId, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBoxGradeItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldStudentScore, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(jTextFieldItemMaxScore, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonCrudSubmit)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonClear))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelEmptyError)
                                    .addComponent(jTextFieldFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(88, 88, 88))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(154, 154, 154))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldStudentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(jRadioButtonCreate, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxGradeItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldStudentScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldItemMaxScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButtonRead)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelError)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelEmptyError)
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear)
                    .addComponent(jButtonCrudSubmit))
                .addGap(22, 22, 22)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldHttpStatusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldMediaType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextFieldResourceLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCrudSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCrudSubmitActionPerformed
        LOG.info("Invoking REST Client based on selection");

        if (jRadioButtonCreate.isSelected()) { // create gradebook item
            LOG.debug("Invoking {} action", jRadioButtonCreate.getText());
            jLabelError.setVisible(false);

            ClientResponse clientResponse = gradeBook_CRUD_client.createGradeBookItem(this.convertFormToXMLString());

            resourceURI = clientResponse.getLocation();
            LOG.debug("Retrieved location {}", resourceURI);

            this.populateForm(clientResponse);

        } else if (jRadioButtonRead.isSelected()) { // read gradebook item
            LOG.debug("Invoking {} action", jRadioButtonRead.getText());
            jLabelError.setVisible(false);

            String gradeItem = jComboBoxGradeItems.getSelectedItem().toString();
            String gradeItemId = null;
            switch (gradeItem) {
                case "Assignment (30%)":
                    gradeItemId = Integer.toString(ASSIGNMENTS);
                    break;
                case "Mid Term (30%)":
                    gradeItemId = Integer.toString(MID_TERM);
                    break;
                case "Final Exam (40%)":
                    gradeItemId = Integer.toString(FINAL_EXAM);
                    break;
                default:
                    LOG.error("Something went wrong while fetching data from combox box");
                    break;
            }

            String studentId = jTextFieldStudentId.getText();

            ClientResponse clientResponse = gradeBook_CRUD_client.retrieveGradeBookItem(ClientResponse.class, gradeItemId, studentId);
            this.populateForm(clientResponse);

        } else if (jRadioButtonUpdate.isSelected()) { // update gradebook item
            LOG.debug("Invoking {} action", jRadioButtonUpdate.getText());
            jLabelError.setVisible(false);

            String studentId = jTextFieldStudentId.getText();

            ClientResponse clientResponse = gradeBook_CRUD_client.updateGradeBookItem(ClientResponse.class, studentId);
            this.populateForm(clientResponse);

        } else if (jRadioButtonDelete.isSelected()) { // delete gradebook item
            LOG.debug("Invoking {} action", jRadioButtonUpdate.getText());
            jLabelError.setVisible(false);

            String studentId = jTextFieldStudentId.getText();

            ClientResponse clientResponse = gradeBook_CRUD_client.deleteGradeBookItem(studentId);
            jTextFieldHttpStatusCode.setText(Integer.toString(clientResponse.getStatus()));

        } else {
            jLabelError.setVisible(true);
        }
    }//GEN-LAST:event_jButtonCrudSubmitActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        // TODO add your handling code here:
        jTextFieldItemMaxScore.setText("");
        jTextFieldFeedback.setText("");
        jTextFieldHttpStatusCode.setText("");
        jTextFieldMediaType.setText("");
        jTextFieldResourceLocation.setText("");
        jTextFieldStudentId.setText("");
        jTextFieldStudentScore.setText("");
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jRadioButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonUpdateActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GradeBook_CRUD_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GradeBook_CRUD_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GradeBook_CRUD_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GradeBook_CRUD_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GradeBook_CRUD_UI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupCrud;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonCrudSubmit;
    private javax.swing.JComboBox<String> jComboBoxGradeItems;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelEmptyError;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JRadioButton jRadioButtonCreate;
    private javax.swing.JRadioButton jRadioButtonDelete;
    private javax.swing.JRadioButton jRadioButtonRead;
    private javax.swing.JRadioButton jRadioButtonUpdate;
    private javax.swing.JTextField jTextFieldFeedback;
    private javax.swing.JTextField jTextFieldHttpStatusCode;
    private javax.swing.JTextField jTextFieldItemMaxScore;
    private javax.swing.JTextField jTextFieldMediaType;
    private javax.swing.JTextField jTextFieldResourceLocation;
    private javax.swing.JTextField jTextFieldStudentId;
    private javax.swing.JTextField jTextFieldStudentScore;
    // End of variables declaration//GEN-END:variables
}
