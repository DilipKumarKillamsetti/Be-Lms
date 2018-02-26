package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pravin on 11/7/16.
 */

public class SearchModel implements Parcelable {
    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel source) {
            return new SearchModel(source);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };
    private String id;
    private String fullname;
    private String shortname;
    private String categoryid;
    private String categoryname;
    private String summary;

    public SearchModel(String id, String fullname, String shortname,String categoryid,String categoryname,String summary) {
        this.id = id;
        this.fullname = fullname;
        this.shortname  = shortname;
        this.categoryid = categoryid;
        this.categoryname = categoryname;
        this.summary = summary;
    }




    protected SearchModel(Parcel in) {
        this.id = in.readString();
        this.fullname = in.readString();
        this.shortname  = in.readString();
        this.categoryid = in.readString();
        this.categoryname = in.readString();
        this.summary = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return categoryname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.categoryname);
        dest.writeString(this.categoryid);
        dest.writeString(this.shortname);
        dest.writeString(this.fullname);
        dest.writeString(this.summary);
    }

}
