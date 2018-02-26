package com.mahindra.be_lms.model;

/**
 * Created by android7 on 10/1/16.
 */

public class Location {
    private String id;
    private String location_name; //city name

    public Location() {
    }

    public Location(String id, String location_name) {
        this.id = id;
        this.location_name = location_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    @Override
    public String toString() {
        return location_name;
    }
}
