package com.mahindra.be_lms.model;

/**
 * Created by android7 on 10/1/16.
 */

public class Designation {
    private String id;
    private String designation_name;

    public Designation() {
    }

    public Designation(String id, String designation_name) {
        this.id = id;
        this.designation_name = designation_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignation_name() {
        return designation_name;
    }

    public void setDesignation_name(String designation_name) {
        this.designation_name = designation_name;
    }

    @Override
    public String toString() {
        return designation_name;
    }
}
