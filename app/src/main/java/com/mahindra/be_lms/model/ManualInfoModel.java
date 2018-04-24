package com.mahindra.be_lms.model;

/**
 * Created by Dell on 9/26/2017.
 */

public class ManualInfoModel {

    public  String id;
    public  String fileurl;
    public  String name;
    public String type;
    public String filename;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilePath() {
        return fileurl;
    }

    public void setFilePath(String filePath) {
        this.fileurl = filePath;
    }

    public String getManualName() {
        return name;
    }

    public void setManualName(String manualName) {
        this.name = manualName;
    }

    public String getFileType() {
        return type;
    }

    public void setFileType(String fileType) {
        this.type = fileType;
    }

    public String getOriginalFileName() {
        return filename;
    }

    public void setOriginalFileName(String originalFileName) {
        this.filename = originalFileName;
    }
}
