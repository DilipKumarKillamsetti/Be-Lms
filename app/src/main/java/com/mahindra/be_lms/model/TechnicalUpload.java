package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pravin on 10/8/16.
 */

public class TechnicalUpload implements Parcelable {
    public static final Parcelable.Creator<TechnicalUpload> CREATOR = new Parcelable.Creator<TechnicalUpload>() {
        @Override
        public TechnicalUpload createFromParcel(Parcel source) {
            return new TechnicalUpload(source);
        }

        @Override
        public TechnicalUpload[] newArray(int size) {
            return new TechnicalUpload[size];
        }
    };
    @SerializedName("none")
    private Long id;
    @SerializedName("id")
    private String technicalUploadID;
    @SerializedName("category")
    private String technicalUploadCategory;
    @SerializedName("subcategory")
    private String technicalUploadSubcategory;
    @SerializedName("filename")
    private String technicalUploadTitle;
    @SerializedName("description")
    private String technicalUploadDesc;
    private String technicalUploadAttachments;
    @SerializedName("filepath")
    private String attachmentList;
    @SerializedName("entry_date")
    private String entry_date;
    @SerializedName("user")
    private String user;
    @SerializedName("posted by")
    private String posted_by;
    private long technicalDocumentReferenceID;

    public TechnicalUpload() {
    }

    protected TechnicalUpload(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.technicalUploadID = in.readString();
        this.technicalUploadCategory = in.readString();
        this.technicalUploadSubcategory = in.readString();
        this.technicalUploadTitle = in.readString();
        this.technicalUploadDesc = in.readString();
        this.technicalUploadAttachments = in.readString();
        this.attachmentList = in.readString();
        this.entry_date = in.readString();
        this.user = in.readString();
        this.posted_by = in.readString();
        this.technicalDocumentReferenceID = in.readLong();
    }

    public String getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(String attachmentList) {
        this.attachmentList = attachmentList;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public long getTechnicalDocumentReferenceID() {
        return technicalDocumentReferenceID;
    }

    public void setTechnicalDocumentReferenceID(long technicalDocumentReferenceID) {
        this.technicalDocumentReferenceID = technicalDocumentReferenceID;
    }

    public String getTechnicalUploadAttachments() {
        return technicalUploadAttachments;
    }

    public void setTechnicalUploadAttachments(String technicalUploadAttachments) {
        this.technicalUploadAttachments = technicalUploadAttachments;
    }

    public String getTechnicalUploadCategory() {
        return technicalUploadCategory;
    }

    public void setTechnicalUploadCategory(String technicalUploadCategory) {
        this.technicalUploadCategory = technicalUploadCategory;
    }

    public String getTechnicalUploadDesc() {
        return technicalUploadDesc;
    }

    public void setTechnicalUploadDesc(String technicalUploadDesc) {
        this.technicalUploadDesc = technicalUploadDesc;
    }

    public String getTechnicalUploadID() {
        return technicalUploadID;
    }

    public void setTechnicalUploadID(String technicalUploadID) {
        this.technicalUploadID = technicalUploadID;
    }

    public String getTechnicalUploadSubcategory() {
        return technicalUploadSubcategory;
    }

    public void setTechnicalUploadSubcategory(String technicalUploadSubcategory) {
        this.technicalUploadSubcategory = technicalUploadSubcategory;
    }

    public String getTechnicalUploadTitle() {
        return technicalUploadTitle;
    }

    public void setTechnicalUploadTitle(String technicalUploadTitle) {
        this.technicalUploadTitle = technicalUploadTitle;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.technicalUploadID);
        dest.writeString(this.technicalUploadCategory);
        dest.writeString(this.technicalUploadSubcategory);
        dest.writeString(this.technicalUploadTitle);
        dest.writeString(this.technicalUploadDesc);
        dest.writeString(this.technicalUploadAttachments);
        dest.writeString(this.attachmentList);
        dest.writeString(this.entry_date);
        dest.writeString(this.user);
        dest.writeString(this.posted_by);
        dest.writeLong(this.technicalDocumentReferenceID);
    }
}
