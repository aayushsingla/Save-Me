package com.codefundo.saveme.models;


public class UserData {
    // Create a Easy table with name same as class.
    //mandatory information for user to provide
    private String id; //IMEI id
    private String name;
    private String emailAddress;
    private String photoUrl;
    private String memberType;

    //details
    //optional information
    private String age;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String contactNumber;
    private String emergencyNumber1;
    private String emergencyNumber2;
    private String bloodGroup;
    private String azureId;

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

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmergencyNumber1() {
        return emergencyNumber1;
    }

    public void setEmergencyNumber1(String emergencyNumber1) {
        this.emergencyNumber1 = emergencyNumber1;
    }

    public String getEmergencyNumber2() {
        return emergencyNumber2;
    }

    public void setEmergencyNumber2(String emergencyNumber2) {
        this.emergencyNumber2 = emergencyNumber2;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAzureId() {
        return azureId;
    }

    public void setAzureId(String azureId) {
        this.azureId = azureId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
