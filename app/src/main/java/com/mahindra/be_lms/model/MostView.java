package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android7 on 10/4/16.
 */
public class MostView implements Parcelable {

    public static final Creator<MostView> CREATOR = new Creator<MostView>() {
        @Override
        public MostView createFromParcel(Parcel source) {
            return new MostView(source);
        }

        @Override
        public MostView[] newArray(int size) {
            return new MostView[size];
        }
    };

    private String id;
    private String Name;
    private String url;
    private String instance;
    private String Fileurl;
    private String viewedcount;
    private String type;
    private String filename;

    public MostView(Parcel source) {
    }

    public MostView(String id, String Name, String url,String instance,String Fileurl,String viewedcount,String type,String filename) {
        this.id = id;
        this.Name = Name;
        this.url = url;
        this.instance = instance;
        this.Fileurl =  Fileurl;
        this.viewedcount = viewedcount;
        this.type = type;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getFileurl() {
        return Fileurl;
    }

    public void setFileurl(String fileurl) {
        Fileurl = fileurl;
    }

    public String getViewedcount() {
        return viewedcount;
    }

    public void setViewedcount(String viewedcount) {
        this.viewedcount = viewedcount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Queries{" +
                "id='" + id + '\'' +
                ", Name='" + Name + '\'' +
                ", url='" + url + '\'' +
                ", instance='" + instance + '\'' +
                ", Fileurl='" + Fileurl + '\'' +
                ", viewedcount='" + viewedcount + '\'' +
                ", type='" + type + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.instance);
        dest.writeString(this.Fileurl);
        dest.writeString(this.viewedcount);
        dest.writeString(this.type);
        dest.writeString(this.Name);
        dest.writeString(this.filename);
    }
}
