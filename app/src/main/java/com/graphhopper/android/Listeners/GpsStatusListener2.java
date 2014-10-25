package com.graphhopper.android.Listeners;

import android.location.GpsStatus;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class GpsStatusListener2 implements GpsStatus.Listener {
    @Override
    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                //Toast.makeText(context, "GPS_SEARCHING", Toast.LENGTH_SHORT).show();
                //   satlateInView.setText("درحال جستجو");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                //     satlateInView.setText("توقف جی پی اس");
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                //     satlateInView.setText("موقعیت شما پیدا شد");
                /*
                 * GPS_EVENT_FIRST_FIX Event is called when GPS is locked
                 */
                //Toast.makeText(context, "GPS_FIXED", Toast.LENGTH_SHORT).show();


             /*   if(gpslocation != null)
                {
                    System.out.println("GPS Info:"+gpslocation.getLatitude()+":"+gpslocation.getLongitude());

                    *//*
                     * Removing the GPS status listener once GPS is locked
                     *//*
                    locationManager.removeGpsStatusListener(this);
                }*/

                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //                 System.out.println("TAG - GPS_EVENT_SATELLITE_STATUS");
                break;
        }

      /*  GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        if(gpsStatus != null) {
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            int CountInView = 0;
            int CountInUse = 0;

            if (satellites != null) {
                for (GpsSatellite gpsSatellite : satellites) {
                    CountInView++;
                    if (gpsSatellite.usedInFix()) {
                        CountInUse++;
                    }
                }
                satlateInView.setText("in view: "+CountInView+"\n in use: "+CountInUse);
        }
    }*/
    }
}
