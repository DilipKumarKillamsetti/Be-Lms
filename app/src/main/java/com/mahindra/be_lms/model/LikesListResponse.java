package com.mahindra.be_lms.model;

/**
 * Created by Dell on 4/17/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikesListResponse implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("postedby")
    @Expose
    private String postedby;
    @SerializedName("userpic")
    @Expose
    private String userpic;
    public final static Parcelable.Creator<LikesListResponse> CREATOR = new Creator<LikesListResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public LikesListResponse createFromParcel(Parcel in) {
            return new LikesListResponse(in);
        }

        public LikesListResponse[] newArray(int size) {
            return (new LikesListResponse[size]);
        }

    }
            ;

    protected LikesListResponse(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.userid = ((String) in.readValue((String.class.getClassLoader())));
        this.modified = ((String) in.readValue((String.class.getClassLoader())));
        this.username = ((String) in.readValue((String.class.getClassLoader())));
        this.postedby = ((String) in.readValue((String.class.getClassLoader())));
        this.userpic = ((String) in.readValue((String.class.getClassLoader())));
    }

    public LikesListResponse() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(userid);
        dest.writeValue(modified);
        dest.writeValue(username);
        dest.writeValue(postedby);
        dest.writeValue(userpic);
    }

    public int describeContents() {
        return 0;
    }

}
