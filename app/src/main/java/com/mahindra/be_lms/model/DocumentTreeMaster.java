package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pravin on 11/4/16.
 * Modified by Chaitali Chavan 11/11/16.
 */

public class DocumentTreeMaster implements Parcelable {
    public static final Creator<DocumentTreeMaster> CREATOR = new Creator<DocumentTreeMaster>() {
        @Override
        public DocumentTreeMaster createFromParcel(Parcel source) {
            return new DocumentTreeMaster(source);
        }

        @Override
        public DocumentTreeMaster[] newArray(int size) {
            return new DocumentTreeMaster[size];
        }
    };
    private String docID;
    private String name;
    private String type;
    private Long docReferencedID;

    public DocumentTreeMaster(String docID, String name, String type, Long docReferencedID) {
        this.docID = docID;
        this.docReferencedID = docReferencedID;
        this.name = name;
        this.type = type;
    }

    protected DocumentTreeMaster(Parcel in) {
        this.docID = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.docReferencedID = (Long) in.readValue(Long.class.getClassLoader());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public Long getDocReferencedID() {
        return docReferencedID;
    }

    public void setDocReferencedID(Long docReferencedID) {
        this.docReferencedID = docReferencedID;
    }

    @Override
    public String toString() {
        return "DocumentTreeMaster{" +
                "docID='" + docID + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", docReferencedID=" + docReferencedID +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.docID);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeValue(this.docReferencedID);
    }
}
