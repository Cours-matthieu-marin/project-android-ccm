package fr.upjv.project_android_ccm.data.model;

import java.time.LocalDateTime;

public class Location {
    private double latitude;
    private double longitude;
    private LocalDateTime date;
    private String idVoyage;

    public Location(double latitude, double longitude, LocalDateTime date, String idVoyage) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.idVoyage = idVoyage;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setNowDate() {
        this.date = LocalDateTime.now();
    }

    public String getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(String idVoyage) {
        this.idVoyage = idVoyage;
    }
}
