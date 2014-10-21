package com.graphhopper.android.Helpers;

import android.location.Location;

import org.mapsforge.core.model.LatLong;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class GpsHelper {

    public static  double distance(LatLong start,LatLong end){
        int R = 6371; // km
        double x = (end.longitude - start.longitude) * Math.cos((start.latitude + end.latitude) / 2);
        double y = (end.latitude - start.latitude);
        return  Math.sqrt(x * x + y * y) * R;
    }

    public static  double distance(Location start,Location end){
        int R = 6371; // km
        double x = (end.getLongitude() - start.getLongitude()) * Math.cos((start.getLatitude() + end.getLatitude()) / 2);
        double y = (end.getLatitude() - start.getLatitude());
        return  Math.sqrt(x * x + y * y) * R;
    }
}
