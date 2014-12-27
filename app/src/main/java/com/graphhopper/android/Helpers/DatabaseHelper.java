package com.graphhopper.android.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;


import com.graphhopper.android.DataModel.FavoritePoint;
import com.graphhopper.android.DataModel.Fuel;
import com.graphhopper.android.DataModel.Message;
import com.graphhopper.android.DataModel.MyLocation;
import com.graphhopper.android.DataModel.Setting;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;

/**
 * Created by aliparsa on 9/20/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    // All Static variables
    SQLiteDatabase database;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db.db";

    // Contacts table name
    private static final String TABLE_LOCATIONS = "locations";
    private static final String TABLE_FAVORITE_POINT = "favorite_point";
    private static final String TABLE_MESSAGE = "message";
    private static final String TABLE_FEUL = "fuel";

    // Contacts Table Columns names LOCATIONS
    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_DATE = "date";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SPEED = "speed";
    private static final String KEY_SENT = "sended";
    private static final String KEY_LITER = "liter";
    private static final String KEY_X1 = "x1";
    private static final String KEY_X2 = "x2";
    private static final String KEY_X3 = "x3";
    private static final String KEY_X4 = "x4";
    private static final String KEY_X5 = "x5";
    private static final String KEY_X6 = "x6";
    private static final String KEY_X7 = "x7";
    private static final String KEY_X8 = "x8";
    private static final String KEY_X9 = "x9";
    private static final String KEY_X10 = "x10";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {


        String CREATE_LOCATIONS_TABLE =
                "CREATE TABLE " + TABLE_LOCATIONS + "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_LAT + " TEXT,"
                        + KEY_LON + " TEXT,"
                        + KEY_DATE + " TEXT,"
                        + KEY_SPEED + " TEXT,"
                        + KEY_SENT + " TEXT"
                        + ")";
        db.execSQL(CREATE_LOCATIONS_TABLE);


        String CREATE_FAVORITE_POINT_TABLE =
                "CREATE TABLE " + TABLE_FAVORITE_POINT + "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_LAT + " TEXT,"
                        + KEY_LON + " TEXT,"
                        + KEY_DATE + " TEXT,"
                        + KEY_DESCRIPTION + " TEXT,"
                        + KEY_X1 + " TEXT,"
                        + KEY_X2 + " TEXT,"
                        + KEY_X3 + " TEXT,"
                        + KEY_X4 + " TEXT,"
                        + KEY_X5 + " TEXT"
                        + ")";
        db.execSQL(CREATE_FAVORITE_POINT_TABLE);


        String CREATE_MESSAGE_TABLE =
                "CREATE TABLE " + TABLE_MESSAGE + "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_X1 + " TEXT,"
                        + KEY_X2 + " TEXT,"
                        + KEY_X3 + " TEXT,"
                        + KEY_X4 + " TEXT,"
                        + KEY_X5 + " TEXT,"
                        + KEY_X6 + " TEXT,"
                        + KEY_X7 + " TEXT,"
                        + KEY_X8 + " TEXT,"
                        + KEY_X9 + " TEXT,"
                        + KEY_X10 + " TEXT"
                        + ")";
        db.execSQL(CREATE_MESSAGE_TABLE);


        String CREATE_FUEL_TABLE =
                "CREATE TABLE " + TABLE_FEUL + "("
                        + KEY_ID + " INTEGER PRIMARY KEY,"
                        + KEY_DATE + " TEXT,"
                        + KEY_LITER + " TEXT"
                        + ")";
        db.execSQL(CREATE_FUEL_TABLE);

        database = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }


    public void insertFuel(String liter,String date){
        ContentValues values = new ContentValues();
        values.put(KEY_LITER,liter );
        values.put(KEY_DATE,date);
        this.getWritableDatabase().insert(TABLE_FEUL,null,values);
    }


    public void insertMessage(Message message){
        ContentValues values = new ContentValues();
        values.put(KEY_X1,message.getX1() );
        values.put(KEY_X2,message.getX2() );
        values.put(KEY_X3,message.getX3() );
        values.put(KEY_X4,message.getX4() );
        values.put(KEY_X5,message.getX5() );
        values.put(KEY_X6,message.getX6() );
        values.put(KEY_X7,message.getX7() );
        values.put(KEY_X8,message.getX8() );
        values.put(KEY_X9,message.getX9() );
        values.put(KEY_X10,message.getX10() );
        this.getWritableDatabase().insert(TABLE_MESSAGE,null,values);
    }

    public void insertLocation(Location location,String date) {
        ContentValues values = new ContentValues();
        values.put(KEY_LAT, location.getLatitude()+"");
        values.put(KEY_LON, location.getLongitude()+"");
        values.put(KEY_DATE, date+"");
        values.put(KEY_SPEED, location.getSpeed()+"");
        values.put(KEY_SENT, "0");
        long a;
        a=this.getWritableDatabase().insert(TABLE_LOCATIONS,null,values);
        int aa=10;
    }

    public void insertFavoritePoint(FavoritePoint favoritePoint) {
        ContentValues values = new ContentValues();
        values.put(KEY_LAT, favoritePoint.getLat()+"");
        values.put(KEY_LON, favoritePoint.getLon()+"");
        values.put(KEY_DATE, favoritePoint.getDate());
        values.put(KEY_DESCRIPTION, favoritePoint.getDescription());
        values.put(KEY_X1, favoritePoint.getX1());
        values.put(KEY_X2, favoritePoint.getX2());
        values.put(KEY_X3, favoritePoint.getX3());
        values.put(KEY_X4, favoritePoint.getX4());
        values.put(KEY_X5, favoritePoint.getX5());
        long a;
        a=this.getWritableDatabase().insert(TABLE_FAVORITE_POINT,null,values);
        int aa=10;
    }

    public void bulkMarkRecordsAsSent(int maxID){
        String strSQL = "UPDATE locations SET sended = '1' WHERE id <= "+ maxID;
        this.getWritableDatabase().execSQL(strSQL);
        Log.e("ali", "record sent and mark as sent");
    }

    public ArrayList<MyLocation> getUnsentLocations(){
        final Cursor cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_LOCATIONS  + " WHERE " + KEY_SENT + "='0'", null);
        ArrayList<MyLocation> loc_list = new ArrayList<MyLocation>();
        MyLocation location=null;

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    location = new MyLocation("ali");
                    location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))));
                    location.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LON))));
                    location.setSpeed(Float.parseFloat(cursor.getString(cursor.getColumnIndex(KEY_SPEED))));
                    location.setRecord_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                    location.setDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));

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
                    latlong = new LatLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))),Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LON))));
                    latlon_list.add(latlong);

                }while(cursor.moveToNext());

            }
        }
        return latlon_list;
    }

    public ArrayList<Location> getAllLocation() {
        final Cursor cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_LOCATIONS , null);
        ArrayList<Location> latlon_list = new ArrayList<Location>();

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    Location location = new Location("ali");
                    location.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LAT)));
                    location.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LON)));
                    location.setSpeed(cursor.getFloat(cursor.getColumnIndex(KEY_SPEED)));
                    Bundle bundle = new Bundle();
                    bundle.putString("date",cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                    location.setExtras(bundle);
                    latlon_list.add(location);

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
                    latlong = new LatLong(Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LAT))),Double.parseDouble(cursor.getString(cursor.getColumnIndex(KEY_LON))));
            }
        }

        return latlong;
    }

    public void clearLocationTable(int dataOlderThanDay){


    }

    public  ArrayList<Location> getLocationsOfDay(String s) {
        final Cursor cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_LOCATIONS +" WHERE "+KEY_DATE+" LIKE '"+s+"%'" , null);
        ArrayList<Location> latlon_list = new ArrayList<Location>();

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    Location location = new Location("ali");
                    location.setLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LAT)));
                    location.setLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LON)));
                    location.setSpeed(cursor.getFloat(cursor.getColumnIndex(KEY_SPEED)));
                    Bundle bundle = new Bundle();
                    bundle.putString("date",cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                    location.setExtras(bundle);
                    latlon_list.add(location);

                }while(cursor.moveToNext());

            }
        }
        return latlon_list;
    }

    public ArrayList<Message> getAllMessages(String key) {

        final Cursor cursor;
        if (key==null || key=="")
            cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_MESSAGE , null);
        else
            cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_MESSAGE+" WHERE "+KEY_X1+" LIKE '"+key+"%'" , null);

        ArrayList<Message> messageArrayList = new ArrayList<Message>();

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    Message message = new Message(
                            cursor.getString(cursor.getColumnIndex(KEY_X1)),
                            cursor.getString(cursor.getColumnIndex(KEY_X2)),
                            cursor.getString(cursor.getColumnIndex(KEY_X3)),
                            cursor.getString(cursor.getColumnIndex(KEY_X4)),
                            cursor.getString(cursor.getColumnIndex(KEY_X5)),
                            cursor.getString(cursor.getColumnIndex(KEY_X6)),
                            cursor.getString(cursor.getColumnIndex(KEY_X7)),
                            cursor.getString(cursor.getColumnIndex(KEY_X8)),
                            cursor.getString(cursor.getColumnIndex(KEY_X9)),
                            cursor.getString(cursor.getColumnIndex(KEY_X10))
                            );

                    messageArrayList.add(message);

                }while(cursor.moveToNext());

            }
        }
        return messageArrayList;
    }

    public ArrayList<FavoritePoint> getAllFavoritePoint(String key) {


        final Cursor cursor;
        if (key==null || key=="")
            cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_FAVORITE_POINT , null);
        else
            cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_FAVORITE_POINT+" WHERE "+KEY_DESCRIPTION+" LIKE '"+key+"%'" , null);

        ArrayList<FavoritePoint> favoritePoints = new ArrayList<FavoritePoint>();

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    FavoritePoint favoritePoint = new FavoritePoint(
                            cursor.getDouble(cursor.getColumnIndex(KEY_LAT)),
                            cursor.getDouble(cursor.getColumnIndex(KEY_LON)),
                            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                            cursor.getString(cursor.getColumnIndex(KEY_X1)),
                            cursor.getString(cursor.getColumnIndex(KEY_X2)),
                            cursor.getString(cursor.getColumnIndex(KEY_X3)),
                            cursor.getString(cursor.getColumnIndex(KEY_X4)),
                            cursor.getString(cursor.getColumnIndex(KEY_X5))

                    );

                    favoritePoints.add(favoritePoint);

                }while(cursor.moveToNext());

            }
        }
        return favoritePoints;

    }

    public ArrayList<Fuel> getAllFuel(String key) {
        final Cursor cursor;
        if (key==null || key=="")
            cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_FEUL , null);
        else
            cursor =  this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_FEUL+" WHERE "+KEY_LITER+" LIKE '"+key+"%'" , null);

        ArrayList<Fuel> fuels = new ArrayList<Fuel>();

        if (cursor != null) {
            if(cursor.moveToFirst()) {

                do{
                    Fuel fuel = new Fuel(
                            cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                            cursor.getString(cursor.getColumnIndex(KEY_LITER))
                    );

                    fuels.add(fuel);

                }while(cursor.moveToNext());

            }
        }
        return fuels;
    }
}