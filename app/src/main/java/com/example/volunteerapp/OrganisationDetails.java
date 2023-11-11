package com.example.volunteerapp;

public class OrganisationDetails {
    private String name_of_org,email,mobile,usertype,state,city,joining_date,org_type,bio,image_url;

    // Constructor to initialize values
    public OrganisationDetails(String TextfullName, String Textemail, String TextmobileNo, String userType, String selectedState, String selectedCity, String joiningDate, String selectedOrgType, String bio,String imgUrl ) {
        this.name_of_org = TextfullName;
        this.mobile = TextmobileNo;
        this.email = Textemail;
        this.usertype = userType;
        this.state = selectedState;
        this.city = selectedCity;
        this.joining_date = joiningDate;
        this.org_type = selectedOrgType;
        this.bio = bio;
        this.image_url = imgUrl;
    }

    // Getter and Setter methods for name_of_org
    public String getEmail(){
        return email;
    }
    public void setEmail(){
        this.email = email;
    }
    public String getName_of_org() {
        return name_of_org;
    }

    public void setName_of_org(String name_of_org) {
        this.name_of_org = name_of_org;
    }

    // Getter and Setter methods for mobile
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    // Getter and Setter methods for usertype
    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    // Getter and Setter methods for state
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    // Getter and Setter methods for city
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter and Setter methods for joining_date
    public String getJoining_date() {
        return joining_date;
    }

    public void setJoining_date(String joining_date) {
        this.joining_date = joining_date;
    }

    // Getter and Setter methods for org_type
    public String getOrg_type() {
        return org_type;
    }

    public void setOrg_type(String org_type) {
        this.org_type = org_type;
    }

    // Getter and Setter methods for bio
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // Getter and Setter methods for image_url
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


}
