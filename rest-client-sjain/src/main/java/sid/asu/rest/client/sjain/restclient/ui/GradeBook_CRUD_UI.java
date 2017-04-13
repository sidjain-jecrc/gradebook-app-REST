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
    private static boolean getGradeSelected = false;

    /**
     * Creates new form GradeBook_CRUD_UI
     */
    public GradeBook_CRUD_UI() {
        initComponents();
        gradeBook_CRUD_client = new GradeBook_CRUD_Client();
        jLabelError.setVisible(false);
        jLabelEmptyError.setVisible(false);
        jLabelStudentIdMissing.setVisible(false);
        jLabelScoreError.setVisible(false);
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
            String itemMaxScore = jTextFieldItemMaxScore.getText().trim();
            String studentId = jTextFieldStudentId.getText().trim();
            String studentScore = jTextFieldStudentScore.getText().trim();
            String studentFeedback = jTextFieldFeedback.getText().trim();

            List<Student> students = new ArrayList<>();
            Student student = new Student();

            if (jRadioButtonCreate.isSelected() || jRadioButtonUpdate.isSelected()) {
                gradeBookItem.setItemMax(Integer.parseInt(itemMaxScore));
                student.setId(Integer.parseInt(studentId));
                student.setScore(Integer.parseInt(studentScore));
                student.setFeedback(studentFeedback);
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

    private void populateInstructorForm(ClientResponse clientResponse) {
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
                        jLabelStudentAppeal.setText(String.valueOf(student.getAppealStatus()));
                    }
                }
            } else {
                jTextFieldItemMaxScore.setText("");
                jTextFieldStudentScore.setText("");
                jTextFieldFeedback.setText("");
                jLabelStudentAppeal.setText("");
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

    private void populateStudentForm(ClientResponse clientResponse) {
        LOG.info("Populating the Student form UI with the Gradebook info");

        String entity = clientResponse.getEntity(String.class);
        LOG.debug("The Client Response entity is {}", entity);

        try {
            if ((clientResponse.getStatus() == Response.Status.OK.getStatusCode())) {
                if (getGradeSelected) {
                    GradeBookItem gradeBookItem = (GradeBookItem) Converter.convertFromXmlToObject(entity, GradeBookItem.class);
                    LOG.debug("The Client Response student gradebook object is {}", gradeBookItem);

                    // Populate gradebook info
                    Student resStudent = gradeBookItem.getStudents().get(0);
                    LOG.debug("The response student is {}", resStudent);

                    jLabelStudentScore.setText(Integer.toString(resStudent.getScore()));
                    jLabelStudentFeedback.setText(resStudent.getFeedback());
                    jLabelStudentAppeal1.setText(String.valueOf(resStudent.getAppealStatus()));

                    getGradeSelected = false;
                } else {
                    jLabelStudentScore.setText("");
                    jLabelStudentFeedback.setText("");
                    jLabelStudentAppeal1.setText("");
                }

            } else {
                jLabelStudentScore.setText("");
                jLabelStudentFeedback.setText("");
                jLabelStudentAppeal1.setText("");
            }

            // Populate HTTP Header Information
            jTextFieldHttpStatusCode.setText(Integer.toString(clientResponse.getStatus()));
            jTextFieldMediaType.setText(clientResponse.getType().toString());

        } catch (JAXBException e) {
            LOG.error("JAXB Exception: " + e.getMessage());
        } catch (Exception e) {
            LOG.error("General Exception:" + e.getMessage());
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
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldStudentId1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jComboBoxGradeItems1 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabelStudentScore = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabelStudentFeedback = new javax.swing.JLabel();
        jButtonGetGrade = new javax.swing.JButton();
        jButtonAppealGrade = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jLabelStudentAppeal = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabelStudentAppeal1 = new javax.swing.JLabel();
        jLabelStudentIdMissing = new javax.swing.JLabel();
        jLabelScoreError = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setResizable(false);
        setSize(new java.awt.Dimension(840, 510));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("Action");

        buttonGroupCrud.add(jRadioButtonCreate);
        jRadioButtonCreate.setText("Create");
        jRadioButtonCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCreateActionPerformed(evt);
            }
        });

        buttonGroupCrud.add(jRadioButtonRead);
        jRadioButtonRead.setText("Read");
        jRadioButtonRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonReadActionPerformed(evt);
            }
        });

        buttonGroupCrud.add(jRadioButtonUpdate);
        jRadioButtonUpdate.setText("Update");
        jRadioButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonUpdateActionPerformed(evt);
            }
        });

        buttonGroupCrud.add(jRadioButtonDelete);
        jRadioButtonDelete.setText("Delete");
        jRadioButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDeleteActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Grade Book Entry (Instructor)");

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

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel12.setText("Grade Book (Student)");

        jLabel13.setText("Student ID :");

        jTextFieldStudentId1.setToolTipText("Student ID");

        jLabel14.setText("Grading Item :");

        jComboBoxGradeItems1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Assignment (30%)", "Mid Term (30%)", "Final Exam (40%)" }));

        jLabel15.setText("Student Score :");

        jLabelStudentScore.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        jLabel16.setText("Feedback :");

        jLabelStudentFeedback.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        jButtonGetGrade.setText("Get Grade");
        jButtonGetGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGetGradeActionPerformed(evt);
            }
        });

        jButtonAppealGrade.setText("Appeal Grade");
        jButtonAppealGrade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAppealGradeActionPerformed(evt);
            }
        });

        jLabel17.setText("Appeal Status :");

        jLabelStudentAppeal.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        jLabel18.setText("Appeal Status :");

        jLabelStudentAppeal1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N

        jLabelStudentIdMissing.setForeground(new java.awt.Color(255, 51, 51));
        jLabelStudentIdMissing.setText("Please enter student id");

        jLabelScoreError.setForeground(new java.awt.Color(255, 51, 51));
        jLabelScoreError.setText("Student score can't be greater than max score");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addGap(113, 113, 113)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(18, 18, 18)
                                    .addComponent(jComboBoxGradeItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextFieldStudentScore, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextFieldItemMaxScore, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextFieldStudentId, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addGap(18, 18, 18)
                                    .addComponent(jTextFieldFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelStudentAppeal)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(50, 50, 50))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jButtonGetGrade)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonAppealGrade))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(14, 14, 14)
                                            .addComponent(jLabel13)
                                            .addGap(18, 18, 18)
                                            .addComponent(jTextFieldStudentId1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel14)
                                            .addGap(18, 18, 18)
                                            .addComponent(jComboBoxGradeItems1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabelStudentAppeal1))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabelStudentScore))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(25, 25, 25)
                                        .addComponent(jLabel16)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabelStudentFeedback))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(59, 59, 59)
                                        .addComponent(jLabelStudentIdMissing)))
                                .addGap(50, 50, 50)))
                        .addGap(65, 65, 65))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldResourceLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jTextFieldHttpStatusCode, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextFieldMediaType, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(83, 83, 83)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButtonCrudSubmit)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButtonClear, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel8)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelEmptyError)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelScoreError)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldStudentScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldItemMaxScore, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabelStudentAppeal)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButtonRead)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButtonUpdate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelError)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldStudentId1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(108, 108, 108)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(48, 48, 48)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jButtonGetGrade)
                                            .addComponent(jButtonAppealGrade)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(jLabelStudentIdMissing))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBoxGradeItems1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabelStudentScore))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabelStudentFeedback))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabelStudentAppeal1))
                                .addGap(72, 72, 72)))))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelEmptyError)
                    .addComponent(jLabelScoreError))
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonClear)
                    .addComponent(jButtonCrudSubmit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldHttpStatusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMediaType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextFieldResourceLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCrudSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCrudSubmitActionPerformed
        LOG.info("Invoking REST Client based on selection");

        if (jRadioButtonCreate.isSelected()) { // create gradebook item
            LOG.debug("Invoking {} action", jRadioButtonCreate.getText());
            jLabelError.setVisible(false);
            jLabelEmptyError.setVisible(false);

            String itemMaxScore = jTextFieldItemMaxScore.getText().trim();
            String studentId = jTextFieldStudentId.getText().trim();
            String studentScore = jTextFieldStudentScore.getText().trim();
            String studentFeedback = jTextFieldFeedback.getText().trim();

            if (!itemMaxScore.equals("") && !studentId.equals("") && !studentScore.equals("") && !studentFeedback.equals("")) {

                if (Integer.parseInt(studentScore) > Integer.parseInt(itemMaxScore)) {
                    jLabelScoreError.setVisible(true);

                } else {
                    jLabelScoreError.setVisible(false);
                    ClientResponse clientResponse = gradeBook_CRUD_client.createGradeBookItem(this.convertFormToXMLString());
                    resourceURI = clientResponse.getLocation();
                    LOG.debug("Retrieved location {}", resourceURI);
                    this.populateInstructorForm(clientResponse);
                }

            } else {
                jLabelEmptyError.setVisible(true);
            }

        } else if (jRadioButtonRead.isSelected()) { // read gradebook item
            LOG.debug("Invoking {} action", jRadioButtonRead.getText());
            jLabelError.setVisible(false);
            jLabelEmptyError.setVisible(false);

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

            String studentId = jTextFieldStudentId.getText().trim();
            if (!studentId.equals("")) {
                ClientResponse clientResponse = gradeBook_CRUD_client.retrieveGradeBookItem(ClientResponse.class, gradeItemId, studentId);
                this.populateInstructorForm(clientResponse);
            } else {
                jLabelEmptyError.setVisible(true);
            }

        } else if (jRadioButtonUpdate.isSelected()) { // update gradebook item
            LOG.debug("Invoking {} action", jRadioButtonUpdate.getText());
            jLabelError.setVisible(false);
            jLabelEmptyError.setVisible(false);

            String itemMaxScore = jTextFieldItemMaxScore.getText().trim();
            String studentId = jTextFieldStudentId.getText().trim();
            String studentScore = jTextFieldStudentScore.getText().trim();
            String studentFeedback = jTextFieldFeedback.getText().trim();

            if (!itemMaxScore.equals("") && !studentId.equals("") && !studentScore.equals("") && !studentFeedback.equals("")) {
                if (Integer.parseInt(studentScore) > Integer.parseInt(itemMaxScore)) {
                    jLabelScoreError.setVisible(true);

                } else {
                    jLabelScoreError.setVisible(false);
                    ClientResponse clientResponse = gradeBook_CRUD_client.updateGradeBookItem(this.convertFormToXMLString(), studentId);
                    this.populateInstructorForm(clientResponse);
                }
            } else {
                jLabelEmptyError.setVisible(true);
            }

        } else if (jRadioButtonDelete.isSelected()) { // delete gradebook item
            LOG.debug("Invoking {} action", jRadioButtonUpdate.getText());
            jLabelError.setVisible(false);
            jLabelEmptyError.setVisible(false);

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

            String studentId = jTextFieldStudentId.getText().trim();
            if (!studentId.equals("")) {
                ClientResponse clientResponse = gradeBook_CRUD_client.deleteGradeBookItem(gradeItemId, studentId);
                jTextFieldHttpStatusCode.setText(Integer.toString(clientResponse.getStatus()));
            } else {
                jLabelEmptyError.setVisible(true);
            }

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
        jTextFieldItemMaxScore.setText("");
        jTextFieldFeedback.setText("");
        jTextFieldHttpStatusCode.setText("");
        jTextFieldMediaType.setText("");
        jTextFieldResourceLocation.setText("");
        jTextFieldStudentScore.setText("");

    }//GEN-LAST:event_jRadioButtonUpdateActionPerformed

    private void jRadioButtonCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCreateActionPerformed
        // TODO add your handling code here:
        jTextFieldItemMaxScore.setText("");
        jTextFieldFeedback.setText("");
        jTextFieldHttpStatusCode.setText("");
        jTextFieldMediaType.setText("");
        jTextFieldResourceLocation.setText("");
        jTextFieldStudentScore.setText("");

    }//GEN-LAST:event_jRadioButtonCreateActionPerformed

    private void jRadioButtonReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonReadActionPerformed
        // TODO add your handling code here:
        jTextFieldItemMaxScore.setText("");
        jTextFieldFeedback.setText("");
        jTextFieldHttpStatusCode.setText("");
        jTextFieldMediaType.setText("");
        jTextFieldResourceLocation.setText("");
        jTextFieldStudentScore.setText("");
    }//GEN-LAST:event_jRadioButtonReadActionPerformed

    private void jRadioButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDeleteActionPerformed
        // TODO add your handling code here:
        jTextFieldItemMaxScore.setText("");
        jTextFieldFeedback.setText("");
        jTextFieldHttpStatusCode.setText("");
        jTextFieldMediaType.setText("");
        jTextFieldResourceLocation.setText("");
        jTextFieldStudentScore.setText("");

    }//GEN-LAST:event_jRadioButtonDeleteActionPerformed

    private void jButtonGetGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetGradeActionPerformed
        LOG.debug("Invoking {} action", jButtonGetGrade.getText());
        jLabelStudentIdMissing.setVisible(false);

        String gradeItem = jComboBoxGradeItems1.getSelectedItem().toString();
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

        String studentId = jTextFieldStudentId1.getText().trim();
        if (!studentId.equals("")) {
            getGradeSelected = true;
            ClientResponse clientResponse = gradeBook_CRUD_client.getStudentGradeBookItem(ClientResponse.class, gradeItemId, studentId);
            this.populateStudentForm(clientResponse);
        } else {
            jLabelEmptyError.setVisible(true);
        }

    }//GEN-LAST:event_jButtonGetGradeActionPerformed

    private void jButtonAppealGradeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAppealGradeActionPerformed
        // TODO add your handling code here:
        LOG.debug("Invoking {} action", jButtonAppealGrade.getText());
        jLabelStudentIdMissing.setVisible(false);

        String gradeItem = jComboBoxGradeItems1.getSelectedItem().toString();
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

        String studentId = jTextFieldStudentId1.getText();
        if (!studentId.equals("")) {
            ClientResponse clientResponse = gradeBook_CRUD_client.updateStudentAppealItem(gradeItemId, studentId);
            this.populateStudentForm(clientResponse);
        } else {
            jLabelEmptyError.setVisible(true);
        }


    }//GEN-LAST:event_jButtonAppealGradeActionPerformed

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
    private javax.swing.JButton jButtonAppealGrade;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonCrudSubmit;
    private javax.swing.JButton jButtonGetGrade;
    private javax.swing.JComboBox<String> jComboBoxGradeItems;
    private javax.swing.JComboBox<String> jComboBoxGradeItems1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JLabel jLabelScoreError;
    private javax.swing.JLabel jLabelStudentAppeal;
    private javax.swing.JLabel jLabelStudentAppeal1;
    private javax.swing.JLabel jLabelStudentFeedback;
    private javax.swing.JLabel jLabelStudentIdMissing;
    private javax.swing.JLabel jLabelStudentScore;
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
    private javax.swing.JTextField jTextFieldStudentId1;
    private javax.swing.JTextField jTextFieldStudentScore;
    // End of variables declaration//GEN-END:variables
}
