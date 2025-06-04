package fr.upjv.project_android_ccm.data.model;

import java.time.LocalDateTime;

public class UserLocation {
    private String id;
    private double latitude;
    private double longitude;
    private String date;
    private String idVoyage;
    public UserLocation() {}
    public UserLocation(double latitude, double longitude, String date, String idVoyage) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.idVoyage = idVoyage;
    }

    public UserLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNowDate() {
        this.date = LocalDateTime.now().toString();
    }

    public String getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(String idVoyage) {
        this.idVoyage = idVoyage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
