package com.mahindra.be_lms.model;

/**
 * Created by Dell on 3/15/2018.
 */

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NominatedEvent implements Serializable, Parcelable
{

    @SerializedName("Event_name")
    @Expose
    private String eventName;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("eventdate")
    @Expose
    private String eventdate;
    public final static Parcelable.Creator<NominatedEvent> CREATOR = new Creator<NominatedEvent>() {


        @SuppressWarnings({
                "unchecked"
        })
        public NominatedEvent createFromParcel(Parcel in) {
            return new NominatedEvent(in);
        }

        public NominatedEvent[] newArray(int size) {
            return (new NominatedEvent[size]);
        }

    }
            ;
    private final static long serialVersionUID = 5619581934921057029L;

    protected NominatedEvent(Parcel in) {
        this.eventName = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.eventdate = ((String) in.readValue((String.class.getClassLoader())));
    }

    public NominatedEvent() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(eventName);
        dest.writeValue(status);
        dest.writeValue(eventdate);
    }

    public int describeContents() {
        return 0;
    }

}