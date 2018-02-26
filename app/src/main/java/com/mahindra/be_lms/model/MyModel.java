package com.mahindra.be_lms.model;

/**
 * Created by krupenghetiya on 03/02/17.
 */

public class MyModel {
    private final String image_url;
    private String video_url,type,message,postedBy,pName,postedOn;
    private final String name;

    public MyModel(String video_url, String image_url, String name,String type,String postedBy,String pName,String postedOn) {
        this.video_url = video_url;
        this.image_url = image_url;
        this.name = name;
        this.type = type;
        this.postedBy = postedBy;
        this.pName = pName;
        this.postedOn = postedOn;
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
}
