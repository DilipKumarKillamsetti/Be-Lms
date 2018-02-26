package com.mahindra.be_lms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pravin on 11/18/16.
 */

public class QuizResultDetails {
    @SerializedName("id")
    private String id;
    @SerializedName("courseid")
    private String courseid;
    @SerializedName("institution")
    private String institution;
    @SerializedName("course")
    private String course;
    @SerializedName("attempt")
    private String attempt;
    @SerializedName("grade")
    private String grade;
    @SerializedName("percentage")
    private String percentage;
    @SerializedName("sumgrades")
    private String sumgrades;
    @SerializedName("quizid")
    private String quizid;
    @SerializedName("quiz")
    private String quiz;

    public QuizResultDetails() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getAttempt() {
        return attempt;
    }

    public void setAttempt(String attempt) {
        this.attempt = attempt;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getSumgrades() {
        return sumgrades;
    }

    public void setSumgrades(String sumgrades) {
        this.sumgrades = sumgrades;
    }

    public String getQuizid() {
        return quizid;
    }

    public void setQuizid(String quizid) {
        this.quizid = quizid;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    @Override
    public String toString() {
        return "QuizResultDetails{" +
                "id='" + id + '\'' +
                ", courseid='" + courseid + '\'' +
                ", institution='" + institution + '\'' +
                ", course='" + course + '\'' +
                ", attempt='" + attempt + '\'' +
                ", grade='" + grade + '\'' +
                ", percentage='" + percentage + '\'' +
                ", sumgrades='" + sumgrades + '\'' +
                ", quizid='" + quizid + '\'' +
                ", quiz='" + quiz + '\'' +
                '}';
    }
}
