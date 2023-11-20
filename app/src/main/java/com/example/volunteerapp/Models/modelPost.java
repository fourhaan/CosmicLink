package com.example.volunteerapp.Models;

public class modelPost {
    private String pId, pTitle, pDescr, pInterested, pImage, pTime, uid, uEmail, uDp, uName, pTags,address,date;
    private double latitude,longitude;

    public modelPost() {
        // Default constructor required for Firebase
    }

    public modelPost(String pId, String pTitle,String pImage){
        this.pId = pId;
        this.pTitle = pTitle;
        this.pImage = pImage;
    }

    public modelPost(String pId, String pTitle, String pDescr, String pInterested, String pImage, String pTime, String uid, String uEmail, String uDp, String uName, String pTags,String date,String fulladdress, double latitude,double longitude) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescr = pDescr;
        this.pInterested = pInterested;
        this.pImage = pImage;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
        this.pTags = pTags;
        this.address= fulladdress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public  String getDate(){return date;}
    public void setDate(String date){this.date = date;}
    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpInterested() {
        return pInterested;
    }

    public void setpInterested(String pInterested) {
        this.pInterested = pInterested;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getpTags() {
        return pTags;
    }

    public void setpTags(String pTags) {
        this.pTags = pTags;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}