package com.graphhopper.android.DataModel;

import java.io.Serializable;

/**
 * Created by aliparsa on 10/15/2014.
 */
public class Task implements Serializable {
    private double fromLat;
    private double fromLon;
    private double toLat;
    private double toLon;
    private String description;
    private String date;

    public Task(double fromLat, double fromLon, double toLat, double toLon, String description,String date) {
        this.fromLat = fromLat;
        this.fromLon = fromLon;
        this.toLat = toLat;
        this.toLon = toLon;
        this.description = description;
        this.date=date;
    }

    public double getFromLat() {
        return fromLat;
    }

    public void setFromLat(double fromLat) {
        this.fromLat = fromLat;
    }

    public double getFromLon() {
        return fromLon;
    }

    public void setFromLon(double fromLon) {
        this.fromLon = fromLon;
    }

    public double getToLat() {
        return toLat;
    }

    public void setToLat(double toLat) {
        this.toLat = toLat;
    }

    public double getToLon() {
        return toLon;
    }

    public void setToLon(double toLon) {
        this.toLon = toLon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
