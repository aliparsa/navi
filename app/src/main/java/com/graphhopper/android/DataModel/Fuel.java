package com.graphhopper.android.DataModel;

/**
 * Created by aliparsa on 10/27/2014.
 */
public class Fuel {

    String date;
    String liter;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLiter() {
        return liter;
    }

    public void setLiter(String liter) {
        this.liter = liter;
    }



    public Fuel(String date, String liter) {
        this.date = date;
        this.liter = liter;
    }


}
