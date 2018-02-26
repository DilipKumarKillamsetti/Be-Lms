package com.mahindra.be_lms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pravin on 11/18/16.
 */

public class QuizResult {
    @SerializedName("company")
    private String company;
    @SerializedName("program")
    private String program;
    @SerializedName("cdate")
    private String cdate;
    @SerializedName("courseid")
    private String courseid;
    @SerializedName("course")
    private String course;
    @SerializedName("result")
    private String result;
    @SerializedName("feedback")
    private String feedback;
    @SerializedName("participation_certificate")
    private String participation_certificate;
    @SerializedName("post_feedback")
    private String post_feedback;
    @SerializedName("admin_approval")
    private String admin_approval;
    @SerializedName("qualification_certificate")
    private String qualification_certificate;
    @SerializedName("details")
    private QuizResultDetails quizResultDetails;

    public QuizResult() {
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getParticipation_certificate() {
        return participation_certificate;
    }

    public void setParticipation_certificate(String participation_certificate) {
        this.participation_certificate = participation_certificate;
    }

    public String getPost_feedback() {
        return post_feedback;
    }

    public void setPost_feedback(String post_feedback) {
        this.post_feedback = post_feedback;
    }

    public String getAdmin_approval() {
        return admin_approval;
    }

    public void setAdmin_approval(String admin_approval) {
        this.admin_approval = admin_approval;
    }

    public String getQualification_certificate() {
        return qualification_certificate;
    }

    public void setQualification_certificate(String qualification_certificate) {
        this.qualification_certificate = qualification_certificate;
    }

    public QuizResultDetails getQuizResultDetails() {
        return quizResultDetails;
    }

    public void setQuizResultDetails(QuizResultDetails quizResultDetails) {
        this.quizResultDetails = quizResultDetails;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "company='" + company + '\'' +
                ", program='" + program + '\'' +
                ", cdate='" + cdate + '\'' +
                ", courseid='" + courseid + '\'' +
                ", course='" + course + '\'' +
                ", result='" + result + '\'' +
                ", feedback='" + feedback + '\'' +
                ", participation_certificate='" + participation_certificate + '\'' +
                ", post_feedback='" + post_feedback + '\'' +
                ", admin_approval='" + admin_approval + '\'' +
                ", qualification_certificate='" + qualification_certificate + '\'' +
                ", quizResultDetails=" + quizResultDetails +
                '}';
    }
}
