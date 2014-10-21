package com.graphhopper.android.DataModel;

import android.location.Location;

/**
 * Created by aliparsa on 9/20/2014.
 */
public class MyLocation extends Location {
    private int record_id;
    private int device_id;
    private String date;
    public MyLocation(String provider) {
        super(provider);
    }

    public MyLocation(Location l) {
        super(l);
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public void setDevice_id(int device_id){
        this.device_id = device_id;
    }

    public int getRecord_id() {
        return record_id;
    }

    public int getDevice_id() {
        return device_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
