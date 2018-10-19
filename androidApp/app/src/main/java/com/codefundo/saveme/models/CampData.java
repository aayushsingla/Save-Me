package com.codefundo.saveme.models;

public class CampData {

    private String id;          //any Random unique Id
    private double latitude;
    private double longitude;
    private String creatorAzureId;     // use LoginActivity.getCurrentUserUniqueId() to get this
    private String name;      // "name"
    private String type;     // "Medical Help" or "Food Camp" or we will think of this

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCreatorAzureId() {
        return creatorAzureId;
    }

    public void setCreatorAzureId(String creatorAzureId) {
        this.creatorAzureId = creatorAzureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
