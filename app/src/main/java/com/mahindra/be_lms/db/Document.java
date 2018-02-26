package com.mahindra.be_lms.db;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Entity mapped to table "DOCUMENT".
 */
public class Document implements Parcelable {

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel source) {
            return new Document(source);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
    private Long id;
    private String documentTreeID;
    private String documentName;
    private Long documentReferencedID;
    private Date documentHitDate;
    private Integer documentHitCount;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END
    private String documentRole;

    public Document() {
    }

    public Document(Long id) {
        this.id = id;
    }

    public Document(Long id, String documentTreeID, String documentName, Long documentReferencedID, Date documentHitDate, Integer documentHitCount, String documentRole) {
        this.id = id;
        this.documentTreeID = documentTreeID;
        this.documentName = documentName;
        this.documentReferencedID = documentReferencedID;
        this.documentHitDate = documentHitDate;
        this.documentHitCount = documentHitCount;
        this.documentRole = documentRole;
    }

    // KEEP METHODS - put your custom methods here
    public Document(JSONObject jsonObject) {
        try {
            this.documentTreeID = jsonObject.getString("document_tree_id");
            this.documentName = jsonObject.getString("document_name");
            this.documentHitCount = Integer.valueOf(jsonObject.getString("document_hit"));
            this.documentReferencedID = 0L;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    protected Document(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.documentTreeID = in.readString();
        this.documentName = in.readString();
        this.documentReferencedID = (Long) in.readValue(Long.class.getClassLoader());
        long tmpDocumentHitDate = in.readLong();
        this.documentHitDate = tmpDocumentHitDate == -1 ? null : new Date(tmpDocumentHitDate);
        this.documentHitCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.documentRole = in.readString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentTreeID() {
        return documentTreeID;
    }

    public void setDocumentTreeID(String documentTreeID) {
        this.documentTreeID = documentTreeID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Long getDocumentReferencedID() {
        return documentReferencedID;
    }

    public void setDocumentReferencedID(Long documentReferencedID) {
        this.documentReferencedID = documentReferencedID;
    }

    public Date getDocumentHitDate() {
        return documentHitDate;
    }

    public void setDocumentHitDate(Date documentHitDate) {
        this.documentHitDate = documentHitDate;
    }

    public Integer getDocumentHitCount() {
        return documentHitCount;
    }

    public void setDocumentHitCount(Integer documentHitCount) {
        this.documentHitCount = documentHitCount;
    }

    public String getDocumentRole() {
        return documentRole;
    }

    // KEEP METHODS END

    public void setDocumentRole(String documentRole) {
        this.documentRole = documentRole;
    }

    @Override
    public String toString() {
        return "Document{" +
                "documentHitCount=" + documentHitCount +
                ", id=" + id +
                ", documentTreeID='" + documentTreeID + '\'' +
                ", documentName='" + documentName + '\'' +
                ", documentReferencedID=" + documentReferencedID +
                ", documentHitDate=" + documentHitDate +
                ", documentRole='" + documentRole + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.documentTreeID);
        dest.writeString(this.documentName);
        dest.writeValue(this.documentReferencedID);
        dest.writeLong(this.documentHitDate != null ? this.documentHitDate.getTime() : -1);
        dest.writeValue(this.documentHitCount);
        dest.writeString(this.documentRole);
    }
}
