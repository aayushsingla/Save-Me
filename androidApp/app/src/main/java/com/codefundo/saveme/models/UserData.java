package com.codefundo.saveme.models;


public class UserData {
    // Create a Easy table with name same as class.
    //mandatory information for user to provide
    private String id;
    private String name;
    private String emailAddress;
    private String photoUrl;
    private String memberType;

    //details
    //optional information
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String contactNumber;
    private String emergencyNumber1;
    private String emergencyNumber2;
    private String bloodGroup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    //public Double currentLat,currentLong;
}
