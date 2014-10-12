package com.graphhopper.android.DataModel;

import android.location.Location;
import android.text.format.Time;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by aliparsa on 10/8/2014.
 */
public class Taximeter {
    private Location startLocation;
    private Location endLocation;
    private ArrayList<Location> route;
    public long timer;

    public Taximeter(){
        route = new ArrayList<Location>();

    }
    public Location getStartLocation() {
        return startLocation;
    }

    public void start(Location location){
        startLocation=location;
        route.add(location);

    }

    public void addToRoute(Location location){
        route.add(location);
        endLocation = location;

    }

    public double getRouteDistance(){
        double distance=0;

        Location preLoc = route.get(0);
        for (int i = 1; i < route.size(); i++) {
             distance+=distance(preLoc,route.get(i));
             preLoc = route.get(i);
        }
        return distance;
    }

    public Long getRouteTime(){
        return (route.get((route.size()-1)).getTime()) -  (route.get(0).getTime());
    }



    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public ArrayList<Location> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Location> route) {
        this.route = route;
    }

    public double distance(Location start,Location end){
    int R = 6371; // km
    double x = (end.getLongitude() - start.getLongitude()) * Math.cos((start.getLatitude() + end.getLatitude()) / 2);
    double y = (end.getLatitude() - start.getLatitude());
    double dis = Math.sqrt(x * x + y * y) * R;
        return dis;
    }
}
