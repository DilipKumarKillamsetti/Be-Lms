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

public class Datum implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("course")
    @Expose
    private Integer course;
    @SerializedName("forum")
    @Expose
    private Integer forum;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("sharedByName")
    @Expose
    private String sharedByName;
    @SerializedName("sharedById")
    @Expose
    private String sharedById;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("likesCount")
    @Expose
    private String likesCount;
    @SerializedName("commentsCounts")
    @Expose
    private String commentsCounts;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("thumb_url")
    @Expose
    private String thumbUrl;
    @SerializedName("postedby")
    @Expose
    private String postedby;
    @SerializedName("createdon")
    @Expose
    private String createdon;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("userpic")
    @Expose
    private String userpic;
    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;
    @SerializedName("postType")
    @Expose
    private String postType;
    @SerializedName("postData")
    @Expose
    private String postData;
    @SerializedName("tag")
    @Expose
    private String tag;
    @SerializedName("likedStatus")
    @Expose
    private boolean likedStatus;
    public final static Parcelable.Creator<Datum> CREATOR = new Creator<Datum>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Datum createFromParcel(Parcel in) {
            return new Datum(in);
        }

        public Datum[] newArray(int size) {
            return (new Datum[size]);
        }

    }
            ;

    protected Datum(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.course = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.forum = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.description = ((String) in.readValue((String.class.getClassLoader())));
        this.sharedByName = ((String) in.readValue((String.class.getClassLoader())));
        this.sharedById = ((String) in.readValue((String.class.getClassLoader())));
        this.location = ((String) in.readValue((String.class.getClassLoader())));
        this.likesCount = ((String) in.readValue((String.class.getClassLoader())));
        this.commentsCounts = ((String) in.readValue((String.class.getClassLoader())));
        this.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.thumbUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.postedby = ((String) in.readValue((String.class.getClassLoader())));
        this.createdon = ((String) in.readValue((String.class.getClassLoader())));
        this.attachment = ((String) in.readValue((String.class.getClassLoader())));
        this.userpic = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.comments, (Comment.class.getClassLoader()));
        this.postType = ((String) in.readValue((String.class.getClassLoader())));
        this.postData = ((String) in.readValue((String.class.getClassLoader())));
        this.tag = ((String) in.readValue((String.class.getClassLoader())));
        this.likedStatus = ((boolean) in.readValue((String.class.getClassLoader())));
    }

    public Datum() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public Integer getForum() {
        return forum;
    }

    public void setForum(Integer forum) {
        this.forum = forum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSharedByName() {
        return sharedByName;
    }

    public void setSharedByName(String sharedByName) {
        this.sharedByName = sharedByName;
    }

    public String getSharedById() {
        return sharedById;
    }

    public void setSharedById(String sharedById) {
        this.sharedById = sharedById;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getCommentsCounts() {
        return commentsCounts;
    }

    public void setCommentsCounts(String commentsCounts) {
        this.commentsCounts = commentsCounts;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getUserpic() {
        return userpic;
    }

    public void setUserpic(String userpic) {
        this.userpic = userpic;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getLikedStatus() {
        return likedStatus;
    }

    public void setLikedStatus(boolean likedStatus) {
        this.likedStatus = likedStatus;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(course);
        dest.writeValue(forum);
        dest.writeValue(description);
        dest.writeValue(sharedByName);
        dest.writeValue(sharedById);
        dest.writeValue(location);
        dest.writeValue(likesCount);
        dest.writeValue(commentsCounts);
        dest.writeValue(imageUrl);
        dest.writeValue(thumbUrl);
        dest.writeValue(postedby);
        dest.writeValue(createdon);
        dest.writeValue(attachment);
        dest.writeValue(userpic);
        dest.writeList(comments);
        dest.writeValue(postType);
        dest.writeValue(postData);
        dest.writeValue(tag);
        dest.writeValue(likedStatus);
    }

    public int describeContents() {
        return 0;
    }

}



