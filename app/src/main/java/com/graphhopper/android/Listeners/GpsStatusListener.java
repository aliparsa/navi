package com.graphhopper.android.Listeners;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

import java.util.Iterator;

/**
 * Created by aliparsa on 10/4/2014.
 */
public class GpsStatusListener implements GpsStatus.Listener {

    private LocationManager locationManager;
    private int satlateInView = 0;
    private int satlateInUse = 0;

    public GpsStatusListener(LocationManager locationManager) {

        this.locationManager = locationManager;
    }

    @Override
    public void onGpsStatusChanged(int i) {
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        if (gpsStatus != null) {
            Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            int satlateInView = 0;
            int satlateInUse = 0;

            if (satellites != null) {
                for (GpsSatellite gpsSatellite : satellites) {
                    satlateInView++;
                    if (gpsSatellite.usedInFix()) {
                        satlateInUse++;
                    }
                }
            }
        }
    }

    public int getSatlateInView() {
        return satlateInView;
    }

    public int getSatlateInUse() {
        return satlateInUse;
    }
}
