package com.mahindra.be_lms.model;

/**
 * Created by MEDHVI-CONT on 9/29/2017.
 */

public class SurveyFeedback {

    public  String id;
    public  String name;
    public  String status;
    public  String intro;
    public String attemptdate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurveyName() {
        return name;
    }

    public void setSurveyName(String surveyName) {
        this.name = surveyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSurveyDescription() {
        return intro;
    }

    public void setSurveyDescription(String surveyDescription) {
        this.intro = surveyDescription;
    }

    public String getAttemptdate() {
        return attemptdate;
    }

    public void setAttemptdate(String attemptdate) {
        this.attemptdate = attemptdate;
    }
}
