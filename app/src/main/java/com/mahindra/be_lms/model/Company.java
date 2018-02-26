package com.mahindra.be_lms.model;

/**
 * Created by Pravin on 10/1/16.
 */

public class Company {
    private String id;
    private String company_name;

    public Company() {
    }

    public Company(String id, String company_name) {
        this.id = id;
        this.company_name = company_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    @Override
    public String toString() {
        return company_name;
    }
}
