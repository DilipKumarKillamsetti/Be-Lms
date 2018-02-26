package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pravin on 10/5/16.
 */
public class Course implements Parcelable {
    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };
    private String id;
    private String course_title;
    private String course_desc;

    public Course() {
    }

    public Course(String id, String course_name, String course_desc) {
    }

    public int supress(){
        int result=0;
        return result;
    }

    protected Course(Parcel in) {
        this.id = in.readString();
        this.course_title = in.readString();
        this.course_desc = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourse_title() {
        return course_title;
    }

    public void setCourse_title(String course_title) {
        this.course_title = course_title;
    }

    public String getCourse_desc() {
        return course_desc;
    }

    public void setCourse_desc(String course_desc) {
        this.course_desc = course_desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.course_title);
        dest.writeString(this.course_desc);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", course_title='" + course_title + '\'' +
                ", course_desc='" + course_desc + '\'' +
                '}';
    }
}
