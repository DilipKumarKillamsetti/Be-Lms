package com.mahindra.be_lms.model;

/**
 * Created by Dell on 4/23/2018.
 */

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllPostResponse implements Parcelable
{

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Statuscode")
    @Expose
    private Integer statuscode;
    @SerializedName("custommessage")
    @Expose
    private String custommessage;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    public final static Parcelable.Creator<AllPostResponse> CREATOR = new Creator<AllPostResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public AllPostResponse createFromParcel(Parcel in) {
            return new AllPostResponse(in);
        }

        public AllPostResponse[] newArray(int size) {
            return (new AllPostResponse[size]);
        }

    }
            ;

    protected AllPostResponse(Parcel in) {
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.statuscode = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.custommessage = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.data, (Datum.class.getClassLoader()));
    }

    public AllPostResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(Integer statuscode) {
        this.statuscode = statuscode;
    }

    public String getCustommessage() {
        return custommessage;
    }

    public void setCustommessage(String custommessage) {
        this.custommessage = custommessage;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(status);
        dest.writeValue(statuscode);
        dest.writeValue(custommessage);
        dest.writeList(data);
    }

    public int describeContents() {
        return 0;
    }

}