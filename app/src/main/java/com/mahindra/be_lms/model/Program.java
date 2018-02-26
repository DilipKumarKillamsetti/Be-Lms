package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android7 on 10/5/16.
 */
public class Program implements Parcelable {
    public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel source) {
            return new Program(source);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };
    private String id;
    private String program_title;
    private String program_date;
    private String program_desc;
    private boolean nomination;
    private List<Course> courseList = new ArrayList<>();

    public Program(String id, String program_name, String program_date, String program_desc, boolean nomination, List<Course> courseList) {
        this.id = id;
        this.program_title = program_name;
        this.program_date = program_date;
        this.program_desc = program_desc;
        this.nomination = nomination;
        this.courseList = courseList;
    }

    protected Program(Parcel in) {
        this.id = in.readString();
        this.program_title = in.readString();
        this.program_date = in.readString();
        this.program_desc = in.readString();
        this.courseList = new ArrayList<>();
        in.readList(this.courseList, Course.class.getClassLoader());
    }

    public static Creator<Program> getCREATOR() {
        return CREATOR;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgram_title() {
        return program_title;
    }

    public void setProgram_title(String program_title) {
        this.program_title = program_title;
    }

    public String getProgram_date() {
        return program_date;
    }

    public void setProgram_date(String program_date) {
        this.program_date = program_date;
    }

    public String getProgram_desc() {
        return program_desc;
    }

    public void setProgram_desc(String program_desc) {
        this.program_desc = program_desc;
    }

    public boolean isNomination() {
        return nomination;
    }

    public void setNomination(boolean nomination) {
        this.nomination = nomination;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.program_title);
        dest.writeString(this.program_date);
        dest.writeString(this.program_desc);
        dest.writeList(this.courseList);
    }
}
