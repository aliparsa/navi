package com.graphhopper.android.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;


import com.graphhopper.android.DataModel.MyLocation;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aliparsa on 9/20/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {


    // All Static variables
    SQLiteDatabase database;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db.db";

    // Contacts table name
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_NODES = "nodes";
    private static final String TABLE_WAYS = "ways";
    private static final String TABLE_WAY_NODES = "way_nodes";


    // Contacts Table Columns names LOCATIONS
    private static final String LOCATIONS_KEY_ID = "id";
    private static final String LOCATIONS_KEY_LAT = "lat";
    private static final String LOCATIONS_KEY_LON = "lon";
    private static final String LOCATIONS_KEY_DATE = "date";
    private static final String LOCATIONS_KEY_DEVICE_ID = "device_id";
    private static final String LOCATIONS_KEY_SPEED = "speed";
    private static final String LOCATIONS_KEY_SENDED = "sended";

    // Contacts Table Columns names NODES
    private static final String NODES_KEY_ID = "id";
    private static final String NODES_KEY_LAT = "lat";
    private static final String NODES_KEY_LON = "lon";

    // Contacts Table Columns names WAYS
    private static final String WAYS_KEY_ID = "id";


    // Contacts Table Columns names WAY_NODES
    private static final String WAY_NODES_KEY_WAY_ID = "way_id";
    private static final String WAY_NODES_KEY_NODE_ID = "node_id";



    private int record_id;
    private int device_id;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE_LOCATIONS + "("
                        + LOCATIONS_KEY_ID + " INTEGER PRIMARY KEY,"
                        + LOCATIONS_KEY_LAT + " TEXT,"
                        + LOCATIONS_KEY_LON + " TEXT,"
                        + LOCATIONS_KEY_DATE + " TEXT,"
                        + LOCATIONS_KEY_SPEED + " TEXT,"
                        + LOCATIONS_KEY_SENDED + " TEXT"
                        + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        database = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public void insertLocation(Location location,String date) {
        ContentValues values = new ContentValues();
        values.put(LOCATIONS_KEY_LAT, location.getLatitude()+"");
        values.put(LOCATIONS_KEY_LON, location.getLongitude()+"");
        values.put(LOCATIONS_KEY_DATE, date+"");
        values.put(LOCATIONS_KEY_SPEED, location.getSpeed()+"");
        values.put(LOCATIONS_KEY_SENDED, "0");
        this.getWritableDatabase().insert(TABLE_LOCATIONS,null,values);
    }

    public void bulkMarkRecordsAsSent(int maxID){
        String strSQL = "UPDATE locations SET sended = '1' WHERE id <= "+ maxID;
        this.getWritableDatabase().execSQL(strSQL);
        Log.e("ali", "record sent and mark as sent");
    }

    public ArrayList<MyLocation> getUnsentLocations(){
        final Cursor cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_LOCATIONS  + " WHERE " + LOCATIONS_KEY_SENDED + "='0'", null);
        ArrayList<MyLocation> loc_list = new ArrayList<MyLocation>();
        MyLocation location=null;

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    location = new MyLocation("ali");
                    location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_LAT))));
                    location.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_LON))));
                    location.setSpeed(Float.parseFloat(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_SPEED))));
                    location.setRecord_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_ID))));
                    location.setDate(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_DATE)));

                    loc_list.add(location);
                }while(cursor.moveToNext());

            }
        }
        return loc_list;
    }

    public ArrayList<LatLong> getAllLocations() {
        final Cursor cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_LOCATIONS , null);
        ArrayList<LatLong> latlon_list = new ArrayList<LatLong>();
        LatLong latlong=null;

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    latlong = new LatLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_LAT))),Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_LON))));
                    latlon_list.add(latlong);

                }while(cursor.moveToNext());

            }
        }
        return latlon_list;
    }

    public LatLong getLastKnownLocation() {
        final Cursor cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_LOCATIONS  , null);
        ArrayList<LatLong> latlon_list = new ArrayList<LatLong>();
        LatLong latlong=null;

        if (cursor != null) {
            if(cursor.moveToLast()) {
                    latlong = new LatLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_LAT))),Double.parseDouble(cursor.getString(cursor.getColumnIndex(LOCATIONS_KEY_LON))));
            }
        }

        return latlong;
    }

    public void clearLocationTable(int dataOlderThanDay){


    }




}