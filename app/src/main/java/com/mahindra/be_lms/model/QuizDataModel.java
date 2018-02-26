package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dell on 10/26/2017.
 */

public class QuizDataModel implements Parcelable {

    public static final Parcelable.Creator<QuizDataModel> CREATOR = new Parcelable.Creator<QuizDataModel>() {
        @Override
        public QuizDataModel createFromParcel(Parcel source) {
            return new QuizDataModel(source);
        }

        @Override
        public QuizDataModel[] newArray(int size) {
            return new QuizDataModel[size];
        }
    };

    @SerializedName("none")
    private Long id;
    @SerializedName("id")
    private String quizID;
    @SerializedName("course")
    private String course;
    @SerializedName("coursemodule")
    private String courseModule;
    @SerializedName("name")
    private String quizTitle;
    @SerializedName("intro")
    private String quizDesc;
    @SerializedName("introformat")
    private String introFormat;
    @SerializedName("timeopen")
    private String timeOpen;
    @SerializedName("timeclose")
    private String timeClose;
    @SerializedName("timelimit")
    private String timeLimit;
    @SerializedName("overduehandling")
    private String overdueHandling;
    @SerializedName("graceperiod")
    private String gracePeriod;
    @SerializedName("preferredbehaviour")
    private String preferredBehaviour;
    @SerializedName("canredoquestions")
    private String canredoQuestions;
    @SerializedName("attempts")
    private String attempts;
    @SerializedName("attemptonlast")
    private String attemptonLast;
    @SerializedName("grademethod")
    private String gradeMethod;
    @SerializedName("decimalpoints")
    private String decimalPoints;
    @SerializedName("questiondecimalpoints")
    private String questionDecimalPoints;
    @SerializedName("reviewattempt")
    private String reviewAttempt;
    @SerializedName("reviewcorrectness")
    private String reviewCorrectness;
    @SerializedName("reviewmarks")
    private String reviewMarks;
    @SerializedName("reviewspecificfeedback")
    private String reviewSpecificFeedback;
    @SerializedName("reviewgeneralfeedback")
    private String reviewGeneralFeedback;
    @SerializedName("reviewrightanswer")
    private String revieWrightAnswer;
    @SerializedName("reviewoverallfeedback")
    private String reviewOverallFeedback;
    @SerializedName("questionsperpage")
    private String questionsPerPage;
    @SerializedName("sumgrades")
    private String sumGrades;
    @SerializedName("grade")
    private String grade;



    private long technicalDocumentReferenceID;

    public QuizDataModel() {
    }

    protected QuizDataModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.attemptonLast = in.readString();
        this.attempts = in.readString();
        this.canredoQuestions= in.readString();
        this.course = in.readString();
        this.courseModule = in.readString();
        this.decimalPoints= in.readString();
        this.gracePeriod = in.readString();
        this.grade = in.readString();
        this.introFormat = in.readString();
        this.overdueHandling = in.readString();
        this.preferredBehaviour = in.readString();
        this.questionDecimalPoints = in.readString();
        this.gracePeriod = in.readString();
        this.questionsPerPage = in.readString();
        this.gradeMethod = in.readString();
        this.quizDesc = in.readString();
        this.introFormat = in.readString();
        this.overdueHandling = in.readString();
        this.preferredBehaviour = in.readString();
        this.questionDecimalPoints = in.readString();
        this.quizID = in.readString();
        this.quizTitle = in.readString();
        this.reviewAttempt = in.readString();
        this.reviewCorrectness = in.readString();
        this.reviewGeneralFeedback = in.readString();
        this.reviewMarks = in.readString();
        this.revieWrightAnswer = in.readString();
        this.reviewOverallFeedback = in.readString();



    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourseModule() {
        return courseModule;
    }

    public void setCourseModule(String courseModule) {
        this.courseModule = courseModule;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizDesc() {
        return quizDesc;
    }

    public void setQuizDesc(String quizDesc) {
        this.quizDesc = quizDesc;
    }

    public String getIntroFormat() {
        return introFormat;
    }

    public void setIntroFormat(String introFormat) {
        this.introFormat = introFormat;
    }

    public String getTimeOpen() {
        return timeOpen;
    }

    public void setTimeOpen(String timeOpen) {
        this.timeOpen = timeOpen;
    }

    public String getTimeClose() {
        return timeClose;
    }

    public void setTimeClose(String timeClose) {
        this.timeClose = timeClose;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getOverdueHandling() {
        return overdueHandling;
    }

    public void setOverdueHandling(String overdueHandling) {
        this.overdueHandling = overdueHandling;
    }

    public String getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getPreferredBehaviour() {
        return preferredBehaviour;
    }

    public void setPreferredBehaviour(String preferredBehaviour) {
        this.preferredBehaviour = preferredBehaviour;
    }

    public String getCanredoQuestions() {
        return canredoQuestions;
    }

    public void setCanredoQuestions(String canredoQuestions) {
        this.canredoQuestions = canredoQuestions;
    }

    public String getAttempts() {
        return attempts;
    }

    public void setAttempts(String attempts) {
        this.attempts = attempts;
    }

    public String getAttemptonLast() {
        return attemptonLast;
    }

    public void setAttemptonLast(String attemptonLast) {
        this.attemptonLast = attemptonLast;
    }

    public String getGradeMethod() {
        return gradeMethod;
    }

    public void setGradeMethod(String gradeMethod) {
        this.gradeMethod = gradeMethod;
    }

    public String getDecimalPoints() {
        return decimalPoints;
    }

    public void setDecimalPoints(String decimalPoints) {
        this.decimalPoints = decimalPoints;
    }

    public String getQuestionDecimalPoints() {
        return questionDecimalPoints;
    }

    public void setQuestionDecimalPoints(String questionDecimalPoints) {
        this.questionDecimalPoints = questionDecimalPoints;
    }

    public String getReviewAttempt() {
        return reviewAttempt;
    }

    public void setReviewAttempt(String reviewAttempt) {
        this.reviewAttempt = reviewAttempt;
    }

    public String getReviewCorrectness() {
        return reviewCorrectness;
    }

    public void setReviewCorrectness(String reviewCorrectness) {
        this.reviewCorrectness = reviewCorrectness;
    }

    public String getReviewMarks() {
        return reviewMarks;
    }

    public void setReviewMarks(String reviewMarks) {
        this.reviewMarks = reviewMarks;
    }

    public String getReviewSpecificFeedback() {
        return reviewSpecificFeedback;
    }

    public void setReviewSpecificFeedback(String reviewSpecificFeedback) {
        this.reviewSpecificFeedback = reviewSpecificFeedback;
    }

    public String getReviewGeneralFeedback() {
        return reviewGeneralFeedback;
    }

    public void setReviewGeneralFeedback(String reviewGeneralFeedback) {
        this.reviewGeneralFeedback = reviewGeneralFeedback;
    }

    public String getRevieWrightAnswer() {
        return revieWrightAnswer;
    }

    public void setRevieWrightAnswer(String revieWrightAnswer) {
        this.revieWrightAnswer = revieWrightAnswer;
    }

    public String getReviewOverallFeedback() {
        return reviewOverallFeedback;
    }

    public void setReviewOverallFeedback(String reviewOverallFeedback) {
        this.reviewOverallFeedback = reviewOverallFeedback;
    }

    public String getQuestionsPerPage() {
        return questionsPerPage;
    }

    public void setQuestionsPerPage(String questionsPerPage) {
        this.questionsPerPage = questionsPerPage;
    }

    public String getSumGrades() {
        return sumGrades;
    }

    public void setSumGrades(String sumGrades) {
        this.sumGrades = sumGrades;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public long getTechnicalDocumentReferenceID() {
        return technicalDocumentReferenceID;
    }

    public void setTechnicalDocumentReferenceID(long technicalDocumentReferenceID) {
        this.technicalDocumentReferenceID = technicalDocumentReferenceID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeLong(this.technicalDocumentReferenceID);
        dest.writeString(this.attemptonLast);
        dest.writeString(this.attempts);
        dest.writeString(this.canredoQuestions);
        dest.writeString(this.course);
        dest.writeString(this.courseModule);
        dest.writeString(this.decimalPoints);
        dest.writeString(this.gracePeriod);
        dest.writeString(this.grade);
        dest.writeString(this.introFormat);
        dest.writeString(this.overdueHandling);
        dest.writeString(this.preferredBehaviour);
        dest.writeString(this.questionDecimalPoints);
        dest.writeString(this.gracePeriod);
        dest.writeString(this.questionsPerPage);
        dest.writeString(this.gradeMethod);
        dest.writeString(this.quizDesc);
        dest.writeString(this.introFormat);
        dest.writeString(this.overdueHandling);
        dest.writeString(this.preferredBehaviour);
        dest.writeString(this.questionDecimalPoints);
        dest.writeString(this.quizID);
        dest.writeString(this.quizTitle);
        dest.writeString(this.reviewAttempt);
        dest.writeString(this.reviewCorrectness);
        dest.writeString(this.reviewGeneralFeedback);
        dest.writeString(this.reviewMarks);
        dest.writeString(this.revieWrightAnswer);
        dest.writeString(this.reviewOverallFeedback);
    }
}
