package com.mahindra.be_lms.model;

/**
 * Created by Dell on 9/21/2017.
 */

public class CourseModel {

    public  String id;
    public  String courseName;
    public  String status;
    public  String courseDescription;
    public  String shortname;
    public  String fullname;
    public  String summary;
    public  String format;
    public  String showgrades;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }
}
