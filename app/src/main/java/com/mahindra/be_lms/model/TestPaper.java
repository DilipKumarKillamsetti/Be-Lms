package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chaitali on 10/8/16.
 */

public class TestPaper implements Parcelable {
    public static final Creator<TestPaper> CREATOR = new Creator<TestPaper>() {
        @Override
        public TestPaper createFromParcel(Parcel source) {
            return new TestPaper(source);
        }

        @Override
        public TestPaper[] newArray(int size) {
            return new TestPaper[size];
        }
    };
    private String id;
    private String testpaper_title;
    private String testpaper_type;
    private String testpaper_desc;
    private String testpaper_url;
    private long referenceId;

    public TestPaper() {
    }

    public TestPaper(String id, String testpaper_title, String testpaper_type, String testpaper_desc, String testpaper_url) {
        this.id = id;
        this.testpaper_title = testpaper_title;
        this.testpaper_type = testpaper_type;
        this.testpaper_desc = testpaper_desc;
        this.testpaper_url = testpaper_url;
    }

    protected TestPaper(Parcel in) {
        this.id = in.readString();
        this.testpaper_title = in.readString();
        this.testpaper_type = in.readString();
        this.testpaper_desc = in.readString();
        this.testpaper_url = in.readString();
        this.referenceId = in.readLong();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestpaper_title() {
        return testpaper_title;
    }

    public void setTestpaper_title(String testpaper_title) {
        this.testpaper_title = testpaper_title;
    }

    public String getTestpaper_type() {
        return testpaper_type;
    }

    public void setTestpaper_type(String testpaper_type) {
        this.testpaper_type = testpaper_type;
    }

    public String getTestpaper_desc() {
        return testpaper_desc;
    }

    public void setTestpaper_desc(String testpaper_desc) {
        this.testpaper_desc = testpaper_desc;
    }

    public String getTestpaper_url() {
        return testpaper_url;
    }

    public void setTestpaper_url(String testpaper_url) {
        this.testpaper_url = testpaper_url;
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.testpaper_title);
        dest.writeString(this.testpaper_type);
        dest.writeString(this.testpaper_desc);
        dest.writeString(this.testpaper_url);
        dest.writeLong(this.referenceId);
    }
}
