package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Chaitali Chavan on 11/22/16.
 * Modified By Chaitali Chavan on 11/22/16,
 */

public class ApprovedProfile implements Parcelable {
    public static final Creator<ApprovedProfile> CREATOR = new Creator<ApprovedProfile>() {
        @Override
        public ApprovedProfile createFromParcel(Parcel source) {
            return new ApprovedProfile(source);
        }

        @Override
        public ApprovedProfile[] newArray(int size) {
            return new ApprovedProfile[size];
        }
    };
    private String profileID;
    private String profileName;
    private String profile_json;

    public ApprovedProfile(String profileID, String profileName, String profile_json) {
        this.profileID = profileID;
        this.profileName = profileName;
        this.profile_json = profile_json;
    }

    public ApprovedProfile() {
    }

    protected ApprovedProfile(Parcel in) {
        this.profileID = in.readString();
        this.profileName = in.readString();
        this.profile_json = in.readString();
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfile_json() {
        return profile_json;
    }

    public void setProfile_json(String profile_json) {
        this.profile_json = profile_json;
    }

    @Override
    public String toString() {
        return "ApprovedProfile{" +
                "profile_json='" + profile_json + '\'' +
                ", profileID='" + profileID + '\'' +
                ", profileName='" + profileName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.profileID);
        dest.writeString(this.profileName);
        dest.writeString(this.profile_json);
    }
}
