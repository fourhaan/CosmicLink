package com.example.volunteerapp;

public class VolunteerDetails {
    public String fullname, gender, dob, mobile,usertype,state,city,joining_date,bio,image_url;

    // Constructor to initialize values
    public VolunteerDetails(String TextfullName, String Textgender, String Textdob, String TextmobileNo, String userType, String selectedState, String selectedCity, String joiningDate,String bio,String imgUrl) {
        this.fullname = TextfullName;
        this.gender = Textgender;
        this.dob = Textdob;
        this.mobile = TextmobileNo;
        this.usertype = userType;
        this.state = selectedState;
        this.city = selectedCity;
        this.joining_date = joiningDate;
        this.bio = bio;
        this.image_url = imgUrl;
    }

}
