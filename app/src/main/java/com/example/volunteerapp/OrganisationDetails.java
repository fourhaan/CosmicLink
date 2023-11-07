package com.example.volunteerapp;

public class OrganisationDetails {
    public String fullname,mobile,usertype,state,city,joining_date,org_type,bio,image_url;

    // Constructor to initialize values
    public OrganisationDetails(String TextfullName, String TextmobileNo, String userType, String selectedState, String selectedCity, String joiningDate, String selectedOrgType, String bio,String imgUrl ) {
        this.fullname = TextfullName;
        this.mobile = TextmobileNo;
        this.usertype = userType;
        this.state = selectedState;
        this.city = selectedCity;
        this.joining_date = joiningDate;
        this.org_type = selectedOrgType;
        this.bio = bio;
        this.image_url = imgUrl;
    }

}
