package com.example.volunteerapp;

public class ReadWriteOrganisationDetails {
    public String fullname,mobile,usertype,state,city,joining_date,org_type;

    // Constructor to initialize values
    public ReadWriteOrganisationDetails(String TextfullName,String TextmobileNo,String userType,String selectedState,String selectedCity,String joiningDate,String selectedOrgType ) {
        this.fullname = TextfullName;
        this.mobile = TextmobileNo;
        this.usertype = userType;
        this.state = selectedState;
        this.city = selectedCity;
        this.joining_date = joiningDate;
        this.org_type = selectedOrgType;
    }

}
