package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dell on 11/8/2017.
 */

public class MyTrainningPassportModel implements Parcelable  {

    public static final Parcelable.Creator<MyTrainningPassportModel> CREATOR = new Parcelable.Creator<MyTrainningPassportModel>() {
        @Override
        public MyTrainningPassportModel createFromParcel(Parcel source) {
            return new MyTrainningPassportModel(source);
        }

        @Override
        public MyTrainningPassportModel[] newArray(int size) {
            return new MyTrainningPassportModel[size];
        }
    };
    private String quizid;
    private String quizname;
    private String coursename;
    private String grade;
    private String result;
    private String date;

    public MyTrainningPassportModel(String quizid, String quizname, String coursename, String grade,String result,String date) {
        this.quizid = quizid;
        this.quizname = quizname;
        this.coursename = coursename;
        this.grade = grade;
        this.result = result;
        this.date = date;

    }

    public String getQuizid() {
        return quizid;
    }

    public void setQuizid(String quizid) {
        this.quizid = quizid;
    }

    public String getQuizname() {
        return quizname;
    }

    public void setQuizname(String quizname) {
        this.quizname = quizname;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getGrade() {
        return grade;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGrade(String grade) {

        this.grade = grade;
    }




    protected MyTrainningPassportModel(Parcel in) {
        this.quizid = in.readString();
        this.quizname= in.readString();
        this.coursename = in.readString();
        this.grade = in.readString();
        this.result = in.readString();
        this.date = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.quizid);
        dest.writeString(this.quizname);
        dest.writeString(this.coursename);
        dest.writeString(this.grade);
        dest.writeString(this.result);
        dest.writeString(this.date);
    }

}
