package com.example.volunteerapp.Models;

public class volunteerDetails {
    private String fullname, username,email, gender, dob, mobile, usertype, state, city, joining_date, bio, image_url,userId;

    public volunteerDetails(String TextuserName){
        this.username = TextuserName;
    }

    // Constructor to initialize values
    public volunteerDetails(String TextfullName, String Textemail, String TextuserName, String Textgender, String Textdob, String TextmobileNo, String userType, String selectedState, String selectedCity,
                            String joiningDate, String bio, String imgUrl, String userId) {
        this.fullname = TextfullName;
        this.email = Textemail;
        this.username = TextuserName;
        this.gender = Textgender;
        this.dob = Textdob;
        this.mobile = TextmobileNo;
        this.usertype = userType;
        this.state = selectedState;
        this.city = selectedCity;
        this.joining_date = joiningDate;
        this.bio = bio;
        this.image_url = imgUrl;
        this.userId = userId;
    }

    // Getter and Setter methods for each field
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJoining_date() {
        return joining_date;
    }

    public void setJoining_date(String joining_date) {
        this.joining_date = joining_date;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

