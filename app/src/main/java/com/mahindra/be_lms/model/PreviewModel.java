package com.mahindra.be_lms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dell on 11/13/2017.
 */

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PreviewModel implements Parcelable
{

    @SerializedName("slot")
    @Expose
    private Integer slot;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("sequencecheck")
    @Expose
    private Integer sequencecheck;
    @SerializedName("lastactiontime")
    @Expose
    private Integer lastactiontime;
    @SerializedName("hasautosavedstep")
    @Expose
    private Boolean hasautosavedstep;
    @SerializedName("flagged")
    @Expose
    private Boolean flagged;
    @SerializedName("number")
    @Expose
    private Integer number;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("blockedbyprevious")
    @Expose
    private Boolean blockedbyprevious;
    @SerializedName("mark")
    @Expose
    private String mark;
    @SerializedName("maxmark")
    @Expose
    private Integer maxmark;
    public final static Parcelable.Creator<PreviewModel> CREATOR = new Creator<PreviewModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PreviewModel createFromParcel(Parcel in) {
            return new PreviewModel(in);
        }

        public PreviewModel[] newArray(int size) {
            return (new PreviewModel[size]);
        }

    }
            ;

    protected PreviewModel(Parcel in) {
        this.slot = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.type = ((String) in.readValue((String.class.getClassLoader())));
        this.page = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.html = ((String) in.readValue((String.class.getClassLoader())));
        this.sequencecheck = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.lastactiontime = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.hasautosavedstep = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.flagged = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.number = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.state = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.blockedbyprevious = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.mark = ((String) in.readValue((String.class.getClassLoader())));
        this.maxmark = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public PreviewModel() {
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public Integer getSequencecheck() {
        return sequencecheck;
    }

    public void setSequencecheck(Integer sequencecheck) {
        this.sequencecheck = sequencecheck;
    }

    public Integer getLastactiontime() {
        return lastactiontime;
    }

    public void setLastactiontime(Integer lastactiontime) {
        this.lastactiontime = lastactiontime;
    }

    public Boolean getHasautosavedstep() {
        return hasautosavedstep;
    }

    public void setHasautosavedstep(Boolean hasautosavedstep) {
        this.hasautosavedstep = hasautosavedstep;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getBlockedbyprevious() {
        return blockedbyprevious;
    }

    public void setBlockedbyprevious(Boolean blockedbyprevious) {
        this.blockedbyprevious = blockedbyprevious;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Integer getMaxmark() {
        return maxmark;
    }

    public void setMaxmark(Integer maxmark) {
        this.maxmark = maxmark;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(slot);
        dest.writeValue(type);
        dest.writeValue(page);
        dest.writeValue(html);
        dest.writeValue(sequencecheck);
        dest.writeValue(lastactiontime);
        dest.writeValue(hasautosavedstep);
        dest.writeValue(flagged);
        dest.writeValue(number);
        dest.writeValue(state);
        dest.writeValue(status);
        dest.writeValue(blockedbyprevious);
        dest.writeValue(mark);
        dest.writeValue(maxmark);
    }

    public int describeContents() {
        return 0;
    }
}
