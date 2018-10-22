package com.codefundo.saveme.models;

public class MissingPeopleData {
    private String id;
    private String name;
    private String age;
    private String gender;
    private String emailAddress;
    private String photoUrl;
    private String status; // "missing" or "found"

    //details
    //optional information
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String contactNumber;
    private String relation;
    private String victimContactNumber;
    private String reporterContactNumber;
    private String reportedByAzureId;
    private String reportedById; //device IMEI


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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getVictimContactNumber() {
        return victimContactNumber;
    }

    public void setVictimContactNumber(String victimContactNumber) {
        this.victimContactNumber = victimContactNumber;
    }

    public String getReporterContactNumber() {
        return reporterContactNumber;
    }

    public void setReporterContactNumber(String reporterContactNumber) {
        this.reporterContactNumber = reporterContactNumber;
    }

    public String getReportedByAzureId() {
        return reportedByAzureId;
    }

    public void setReportedByAzureId(String reportedByAzureId) {
        this.reportedByAzureId = reportedByAzureId;
    }

    public String getReportedById() {
        return reportedById;
    }

    public void setReportedById(String reportedById) {
        this.reportedById = reportedById;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


}
