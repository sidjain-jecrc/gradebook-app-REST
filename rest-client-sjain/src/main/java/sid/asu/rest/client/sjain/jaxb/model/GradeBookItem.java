/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sid.asu.rest.client.sjain.jaxb.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Siddharth
 */
@XmlRootElement(name = "GradeItem")
@XmlType(propOrder = {"itemId", "itemName", "itemMax", "students"})
public class GradeBookItem {

    private int itemId;
    private String itemName;
    private int itemMax;
    private List<Student> students;

    public GradeBookItem() {
    }

    public GradeBookItem(int itemId, String itemName, int itemMax, List<Student> students) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemMax = itemMax;
        this.students = students;
    }

    @XmlElement(name = "itemId")
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @XmlElement(name = "itemName")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @XmlElement(name = "itemMax")
    public int getItemMax() {
        return itemMax;
    }

    public void setItemMax(int itemMax) {
        this.itemMax = itemMax;
    }

    @XmlElement(name = "students")
    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

}
