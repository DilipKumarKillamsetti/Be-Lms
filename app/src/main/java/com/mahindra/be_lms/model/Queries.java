package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pravin on 10/18/16.
 */

public class Queries implements Parcelable {
    public static final Creator<Queries> CREATOR = new Creator<Queries>() {
        @Override
        public Queries createFromParcel(Parcel source) {
            return new Queries(source);
        }

        @Override
        public Queries[] newArray(int size) {
            return new Queries[size];
        }
    };
    private String queryid;
    private String subject;
    private String message;
    private String Status;
    private String query_response;
    private String filepath;
    private String chathistory;

    public Queries(String id, String query_subject, String query_body, String status, String query_response,String filepath,String chathistory) {
        this.queryid = id;
        this.subject = query_subject;
        this.message = query_body;
        this.Status = status;
        this.query_response = query_response;
        this.filepath = filepath;
        this.chathistory = chathistory;
    }

    protected Queries(Parcel in) {
        this.queryid = in.readString();
        this.subject = in.readString();
        this.message = in.readString();
        this.Status = in.readString();
        this.query_response = in.readString();
        this.filepath = in.readString();
        this.chathistory = in.readString();
    }

    public String getId() {
        return queryid;
    }

    public void setId(String id) {
        this.queryid = id;
    }

    public String getQuery_subject() {
        return subject;
    }

    public void setQuery_subject(String query_subject) {
        this.subject = query_subject;
    }

    public String getQuery_body() {
        return message;
    }

    public void setQuery_body(String query_body) {
        this.message = query_body;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getQuery_response() {
        return query_response;
    }

    public static Creator<Queries> getCREATOR() {
        return CREATOR;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getChathistory() {
        return chathistory;
    }

    public void setChathistory(String chathistory) {
        this.chathistory = chathistory;
    }

    public void setQuery_response(String query_response) {
        this.query_response = query_response;
    }



    @Override
    public String toString() {
        return "Queries{" +
                "id='" + queryid + '\'' +
                ", query_subject='" + subject + '\'' +
                ", query_body='" + message + '\'' +
                ", query_response='" + query_response + '\'' +
                ", filepath='" + filepath + '\'' +
                ", chathistory='" + chathistory + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.queryid);
        dest.writeString(this.subject);
        dest.writeString(this.message);
        dest.writeString(this.Status);
        dest.writeString(this.query_response);
        dest.writeString(this.filepath);
        dest.writeString(this.chathistory);
    }
}
