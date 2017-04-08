/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.client.sjain.jaxb.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Siddharth
 */
@XmlRootElement(name = "Student")
public class Student {

    public enum GradeAppeal {
        APPEALED, NOT_APPEALED, ACCEPTED, REJECTED
    }

    private int id;
    private String name;
    private int score;
    private String feedback;
    private boolean graded;
    private GradeAppeal appealStatus;

    public Student() {
    }

    public Student(int id, String name, int score, String feedback, boolean graded, GradeAppeal status) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.feedback = feedback;
        this.graded = graded;
        this.appealStatus = status;
    }

    @XmlElement(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "score")
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @XmlElement(name = "feedback")
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    @XmlElement(name = "isGraded")
    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    @XmlElement(name = "appealStatus")
    public GradeAppeal getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(GradeAppeal appealStatus) {
        this.appealStatus = appealStatus;
    }

}
