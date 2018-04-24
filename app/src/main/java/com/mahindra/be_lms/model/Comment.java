package com.mahindra.be_lms.model;

/**
 * Created by Dell on 4/23/2018.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("totalscore")
    @Expose
    private String totalscore;
    @SerializedName("parent1")
    @Expose
    private String parent1;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("postedby")
    @Expose
    private String postedby;
    @SerializedName("postType")
    @Expose
    private String postType;
    @SerializedName("createdon")
    @Expose
    private String createdon;
    @SerializedName("attachment_comment")
    @Expose
    private String attachmentComment;
    @SerializedName("userpic")
    @Expose
    private String userpic;
    @SerializedName("postData")
    @Expose
    private String postData;
    public final static Parcelable.Creator<Comment> CREATOR = new Creator<Comment>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return (new Comment[size]);
        }

    }
            ;

    protected Comment(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.subject = ((String) in.readValue((String.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.totalscore = ((String) in.readValue((String.class.getClassLoader())));
        this.parent1 = ((String) in.readValue((String.class.getClassLoader())));
        this.userid = ((String) in.readValue((String.class.getClassLoader())));
        this.username = ((String) in.readValue((String.class.getClassLoader())));
        this.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.postedby = ((String) in.readValue((String.class.getClassLoader())));
        this.postType = ((String) in.readValue((String.class.getClassLoader())));
        this.createdon = ((String) in.readValue((String.class.getClassLoader())));
        this.attachmentComment = ((String) in.readValue((String.class.getClassLoader())));
        this.userpic = ((String) in.readValue((String.class.getClassLoader())));
        this.postData = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(String totalscore) {
        this.totalscore = totalscore;
    }

    public String getParent1() {
        return parent1;
    }

    public void setParent1(String parent1) {
        this.parent1 = parent1;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getAttachmentComment() {
        return attachmentComment;
    }

    public void setAttachmentComment(String attachmentComment) {
        this.attachmentComment = attachmentComment;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(subject);
        dest.writeValue(message);
        dest.writeValue(totalscore);
        dest.writeValue(parent1);
        dest.writeValue(userid);
        dest.writeValue(username);
        dest.writeValue(imageUrl);
        dest.writeValue(postedby);
        dest.writeValue(postType);
        dest.writeValue(createdon);
        dest.writeValue(attachmentComment);
        dest.writeValue(userpic);
        dest.writeValue(postData);
    }

    public int describeContents() {
        return 0;
    }

}