/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.server.sjain.jaxb.model;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Siddharth
 */
@XmlType(propOrder = { "id", "score", "feedback", "graded", "appealStatus"})
public class Student {

    public enum GradeAppeal {
        APPEALED, NOT_APPEALED, ACCEPTED, REJECTED
    }

    private int id;
    private int score;
    private String feedback;
    private boolean graded;
    private GradeAppeal appealStatus;

    public Student() {
    }

    public Student(int id, int score, String feedback, boolean graded, GradeAppeal status) {
        this.id = id;
        this.score = score;
        this.feedback = feedback;
        this.graded = graded;
        this.appealStatus = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    public GradeAppeal getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(GradeAppeal appealStatus) {
        this.appealStatus = appealStatus;
    }

}
