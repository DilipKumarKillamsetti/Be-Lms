package com.mahindra.be_lms.db;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Entity mapped to table "TECHNICAL_UPLOAD".
 */
public class TechnicalUpload implements Parcelable {
    public static final Creator<TechnicalUpload> CREATOR = new Creator<TechnicalUpload>() {
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
    @SerializedName("news_title")
    private String technicalUploadTitle;
    @SerializedName("message")
    private String technicalUploadDesc;
    private String technicalUploadAttachments;
    @SerializedName("attachments")
    private String attachmentList;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END
    private long technicalDocumentReferenceID;

    public TechnicalUpload() {
    }

    public TechnicalUpload(Long id) {
        this.id = id;
    }

    public TechnicalUpload(Long id, String technicalUploadID, String technicalUploadCategory, String technicalUploadSubcategory, String technicalUploadTitle, String technicalUploadDesc, String technicalUploadAttachments) {
        this.id = id;
        this.technicalUploadID = technicalUploadID;
        this.technicalUploadCategory = technicalUploadCategory;
        this.technicalUploadSubcategory = technicalUploadSubcategory;
        this.technicalUploadTitle = technicalUploadTitle;
        this.technicalUploadDesc = technicalUploadDesc;
        this.technicalUploadAttachments = technicalUploadAttachments;
    }

    protected TechnicalUpload(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.technicalUploadID = in.readString();
        this.technicalUploadCategory = in.readString();
        this.technicalUploadSubcategory = in.readString();
        this.technicalUploadTitle = in.readString();
        this.technicalUploadDesc = in.readString();
        this.technicalUploadAttachments = in.readString();
    }

    public static Creator<TechnicalUpload> getCREATOR() {
        return CREATOR;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTechnicalUploadID() {
        return technicalUploadID;
    }

    public void setTechnicalUploadID(String technicalUploadID) {
        this.technicalUploadID = technicalUploadID;
    }

    public String getTechnicalUploadCategory() {
        return technicalUploadCategory;
    }

    public void setTechnicalUploadCategory(String technicalUploadCategory) {
        this.technicalUploadCategory = technicalUploadCategory;
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

    public String getTechnicalUploadDesc() {
        return technicalUploadDesc;
    }

    public void setTechnicalUploadDesc(String technicalUploadDesc) {
        this.technicalUploadDesc = technicalUploadDesc;
    }

    public String getTechnicalUploadAttachments() {
        return technicalUploadAttachments;
    }

    public void setTechnicalUploadAttachments(String technicalUploadAttachments) {
        this.technicalUploadAttachments = technicalUploadAttachments;
    }
// KEEP METHODS - put your custom methods here

    public long getTechnicalDocumentReferenceID() {
        return technicalDocumentReferenceID;
    }


    // KEEP METHODS END

    public void setTechnicalDocumentReferenceID(long technicalDocumentReferenceID) {
        this.technicalDocumentReferenceID = technicalDocumentReferenceID;
    }

    @Override
    public String toString() {
        return "TechnicalUpload{" +
                "id=" + id +
                ", technicalUploadID='" + technicalUploadID + '\'' +
                ", technicalUploadCategory='" + technicalUploadCategory + '\'' +
                ", technicalUploadSubcategory='" + technicalUploadSubcategory + '\'' +
                ", technicalUploadTitle='" + technicalUploadTitle + '\'' +
                ", technicalUploadDesc='" + technicalUploadDesc + '\'' +
                ", technicalUploadAttachments='" + technicalUploadAttachments + '\'' +
                ", attachmentList=" + attachmentList +
                '}';
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
    }

    public String getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(String attachmentList) {
        this.attachmentList = attachmentList;
    }
}
