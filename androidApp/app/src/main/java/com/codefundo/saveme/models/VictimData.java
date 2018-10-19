package com.codefundo.saveme.models;

public class VictimData {

    private String id;          //IMEI id
    private double currentLat;
    private double currentLong;
    private String azureId;     // use LoginActivity.getCurrentUserUniqueId() to get this
    private String status;      //"safe" or "danger"
    private String savedBy;     // "null" or "IMEI_ID_VOLUNTEER"
    private String savedByUUID; // "null" or "uuid volunteer"

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

    public String getAzureId() {
        return azureId;
    }

    public void setAzureId(String azureId) {
        this.azureId = azureId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSavedBy() {
        return savedBy;
    }

    public void setSavedBy(String savedBy) {
        this.savedBy = savedBy;
    }

    public String getSavedByUUID() {
        return savedByUUID;
    }

    public void setSavedByUUID(String savedByUUID) {
        this.savedByUUID = savedByUUID;
    }


}
