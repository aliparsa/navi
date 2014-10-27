package com.graphhopper.android.DataModel;

import org.json.JSONObject;

/**
 * Created by aliparsa on 10/26/2014.
 */
public class FavoritePoint {

    private int id;
    private double lat;
    private double lon;
    private String date;
    private String description;
    private String X1;
    private String X2;
    private String X3;
    private String X4;
    private String X5;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }






    public FavoritePoint(Double lat,Double lon,String description,String date,String x1, String x2, String x3, String x4, String x5) {

        this.lat=lat;
        this.lon=lon;
        this.description=description;
        this.date=date;
        X5 = x5;
        X4 = x4;
        X3 = x3;
        X2 = x2;
        X1 = x1;
    }



    public void setX5(String x5) {
        X5 = x5;
    }

    public void setX4(String x4) {
        X4 = x4;
    }

    public void setX3(String x3) {
        X3 = x3;
    }

    public void setX2(String x2) {
        X2 = x2;
    }

    public void setX1(String x1) {
        X1 = x1;
    }

    public String getX5() {
        return X5;
    }

    public String getX4() {
        return X4;
    }

    public String getX3() {
        return X3;
    }

    public String getX2() {
        return X2;
    }

    public String getX1() {
        return X1;
    }


    public static FavoritePoint getObjectFromJSON(JSONObject obj) {
        try {
            return new FavoritePoint(
                    obj.getDouble("lat"),
                    obj.getDouble("lon"),
                    obj.getString("description"),
                    obj.getString("date"),
                    obj.getString("x1"),
                    obj.getString("x2"),
                    obj.getString("x3"),
                    obj.getString("x4"),
                    obj.getString("x5")
            );
        }catch (Exception e){

        }
        return  null;
    }
}
