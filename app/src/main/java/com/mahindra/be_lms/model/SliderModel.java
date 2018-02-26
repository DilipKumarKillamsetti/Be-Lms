package com.mahindra.be_lms.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 1/23/2017.
 */

public class SliderModel {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String desc;
    @SerializedName("image_url")
    private String img_url;
    @SerializedName("seq")
    private int seq;
    @SerializedName("weblink")
    private String webLink;
    @SerializedName("pdfdoc")
    private String pdfdoc;
    private long downloadRef;

    public SliderModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getPdfdoc() {
        return pdfdoc;
    }

    public void setPdfdoc(String pdfdoc) {
        this.pdfdoc = pdfdoc;
    }

    public long getDownloadRef() {
        return downloadRef;
    }

    public void setDownloadRef(long downloadRef) {
        this.downloadRef = downloadRef;
    }

    @Override
    public String toString() {
        return "SliderModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", img_url='" + img_url + '\'' +
                ", seq=" + seq +
                ", webLink='" + webLink + '\'' +
                ", pdfdoc='" + pdfdoc + '\'' +
                ", downloadRef=" + downloadRef +
                '}';
    }
}
