package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dell on 9/27/2017.
 */

public class QuizResultModel implements Parcelable {

    public static final Parcelable.Creator<QuizResultModel> CREATOR = new Parcelable.Creator<QuizResultModel>() {
        @Override
        public QuizResultModel createFromParcel(Parcel source) {
            return new QuizResultModel(source);
        }

        @Override
        public QuizResultModel[] newArray(int size) {
            return new QuizResultModel[size];
        }
    };
    @SerializedName("none")
    private Long id;
    @SerializedName("id")
    private String ID;
    @SerializedName("quiz")
    private String quizId;
    @SerializedName("userid")
    private String userId;
    @SerializedName("attempt")
    private String quizAttempt;
    @SerializedName("uniqueid")
    private String quizUniqeId;
    @SerializedName("layout")
    private String quizLayout;
    @SerializedName("currentpage")
    private String currentPage;
    @SerializedName("preview")
    private String quizPreview;
    @SerializedName("state")
    private String quizState;
    @SerializedName("timestart")
    private String quiztimeStart;
    @SerializedName("timefinish")
    private String quizTimeFinish;
    @SerializedName("timemodified")
    private String quizTimeModified;
    @SerializedName("timecheckstate")
    private String quizTimeCheckState;
    @SerializedName("sumgrades")
    private String quizSumGrades;

    private long technicalDocumentReferenceID;

    public QuizResultModel() {
    }

    protected QuizResultModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.currentPage = in.readString();
        this.quizAttempt = in.readString();
        this.quizId = in.readString();
        this.quizLayout = in.readString();
        this.quizPreview = in.readString();
        this.quizState = in.readString();
        this.quizSumGrades = in.readString();
        this.quizTimeCheckState = in.readString();
        this.quizTimeFinish = in.readString();
        this.quizTimeModified = in.readString();
        this.quiztimeStart = in.readString();
        this.quizUniqeId = in.readString();
        this.ID = in.readString();
        this.technicalDocumentReferenceID = in.readLong();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuizAttempt() {
        return quizAttempt;
    }

    public void setQuizAttempt(String quizAttempt) {
        this.quizAttempt = quizAttempt;
    }

    public String getQuizUniqeId() {
        return quizUniqeId;
    }

    public void setQuizUniqeId(String quizUniqeId) {
        this.quizUniqeId = quizUniqeId;
    }

    public String getQuizLayout() {
        return quizLayout;
    }

    public void setQuizLayout(String quizLayout) {
        this.quizLayout = quizLayout;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getQuizPreview() {
        return quizPreview;
    }

    public void setQuizPreview(String quizPreview) {
        this.quizPreview = quizPreview;
    }

    public String getQuizState() {
        return quizState;
    }

    public void setQuizState(String quizState) {
        this.quizState = quizState;
    }

    public String getQuiztimeStart() {
        return quiztimeStart;
    }

    public void setQuiztimeStart(String quiztimeStart) {
        this.quiztimeStart = quiztimeStart;
    }

    public String getQuizTimeFinish() {
        return quizTimeFinish;
    }

    public void setQuizTimeFinish(String quizTimeFinish) {
        this.quizTimeFinish = quizTimeFinish;
    }

    public String getQuizTimeModified() {
        return quizTimeModified;
    }

    public void setQuizTimeModified(String quizTimeModified) {
        this.quizTimeModified = quizTimeModified;
    }

    public String getQuizTimeCheckState() {
        return quizTimeCheckState;
    }

    public void setQuizTimeCheckState(String quizTimeCheckState) {
        this.quizTimeCheckState = quizTimeCheckState;
    }

    public String getQuizSumGrades() {
        return quizSumGrades;
    }

    public void setQuizSumGrades(String quizSumGrades) {
        this.quizSumGrades = quizSumGrades;
    }

    public long getTechnicalDocumentReferenceID() {
        return technicalDocumentReferenceID;
    }

    public void setTechnicalDocumentReferenceID(long technicalDocumentReferenceID) {
        this.technicalDocumentReferenceID = technicalDocumentReferenceID;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.currentPage);
        dest.writeValue(this.quizAttempt);
        dest.writeValue(this.quizId);
        dest.writeValue(this.quizLayout);
        dest.writeValue(this.quizPreview);
        dest.writeValue(this.quizState);
        dest.writeValue(this.quizSumGrades);
        dest.writeValue(this.quizTimeCheckState);
        dest.writeValue(this.quizTimeFinish);
        dest.writeValue(this.quizTimeModified);
        dest.writeValue(this.quiztimeStart);
        dest.writeValue(this.quizUniqeId);
        dest.writeValue(this.ID);
        dest.writeLong(this.technicalDocumentReferenceID);
    }
}
