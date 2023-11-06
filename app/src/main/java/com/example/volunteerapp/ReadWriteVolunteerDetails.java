package com.example.volunteerapp;

public class ReadWriteVolunteerDetails {
    public String fullname, gender, dob, mobile,usertype,state,city,joining_date;

    // Constructor to initialize values
    public ReadWriteVolunteerDetails(String TextfullName,String Textgender,String Textdob,String TextmobileNo,String userType,String selectedState,String selectedCity,String joiningDate) {
        this.fullname = TextfullName;
        this.gender = Textgender;
        this.dob = Textdob;
        this.mobile = TextmobileNo;
        this.usertype = userType;
        this.state = selectedState;
        this.city = selectedCity;
        this.joining_date = joiningDate;
    }

}
