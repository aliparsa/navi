package com.graphhopper.android.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import com.graphhopper.android.Activities.MainActivity;
import com.graphhopper.android.Helpers.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by aliparsa on 9/20/2014.
 */
public class LocationFinderService extends Service {

    public static final String NEW_LOCATION = "NEW_LOCATION";
    PowerManager.WakeLock wakeLock;

    private LocationManager locationManager;

    public LocationFinderService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        Log.i("alix","Location Finder Service Created.");

    }


    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.i("alix","Location Finder Service Started.");


        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 5, listener);

    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            Log.i("alix", "Location Changed lat >"+location.getLatitude()+" lon >"+location.getLongitude());

//            if (MyActivity.context != null)
//            ((MyActivity) MyActivity.context).alog("Location Changed");

            broadcastNewLocation(location);

            if (location == null)
                return;

            // TODO save Location To DISK
            DatabaseHelper db_handler = new DatabaseHelper(MainActivity.context);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = sdf.format(c.getTime());

            db_handler.insertLocation(location, strDate);
            Log.i("alix", "Location Inserted to Database");

//            if (MyActivity.context != null)
//            ((MyActivity) MyActivity.context).alog("Location Inserted to Database");


        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("LOCATION_FINDER"));
        wakeLock.release();


    }


    private void broadcastNewLocation(Location location)//this method sends broadcast messages
    {
        try {
            Intent intent = new Intent(NEW_LOCATION);
            intent.putExtra("location", location);
            sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

}
