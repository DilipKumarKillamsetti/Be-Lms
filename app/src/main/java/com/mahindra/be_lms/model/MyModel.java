package com.mahindra.be_lms.model;

import android.graphics.Bitmap;

/**
 * Created by krupenghetiya on 03/02/17.
 */

public class MyModel {
    private final String image_url;
    private String video_url,type,message,postedBy,pName,postedOn,profilePic,comments;
    private final String name;
    public  String  likesCount,commentCount,id,location,sharedById;
    private boolean likeStatus;
    private Bitmap bitmap;

    public MyModel(String video_url, String image_url, String name,String type,String postedBy,String pName,String postedOn,String likesCount,String commentCount,String id, String location,boolean likeStatus,String sharedById,String profilePic,String comments,Bitmap bitmap) {
        this.video_url = video_url;
        this.image_url = image_url;
        this.name = name;
        this.type = type;
        this.postedBy = postedBy;
        this.pName = pName;
        this.postedOn = postedOn;
        this.likesCount = likesCount;
        this.commentCount = commentCount;
        this.id = id;
        this.location = location;
        this.likeStatus = likeStatus;
        this.sharedById = sharedById;
        this.profilePic = profilePic;
        this.comments = comments;
        this.bitmap = bitmap;
    }

    public MyModel(String image_url, String name,String type,String postedBy,String pName,String postedOn) {
        this.image_url = image_url;
        this.name = name;
        this.type = type;
        this.pName = pName;
        this.postedBy = postedBy;
        this.postedOn = postedOn;
    }

    public MyModel(String name,String type,String message,String postedBy,String postedOn) {
        this.image_url=null;
        this.name = name;
        this.type = type;
        this.message = message;
        this.postedBy = postedBy;
        this.postedOn = postedOn;
     }

    public String getImage_url() {
        return image_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getpName() {
        return pName;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(boolean likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
