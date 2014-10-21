package com.graphhopper.android.Helpers;

import android.content.Context;
import android.content.Intent;

import com.graphhopper.android.Services.ConnectionService;
import com.graphhopper.android.Services.LocationFinderService;
import com.graphhopper.android.Services.LocationSenderService;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class ServiceHelper {

    public static void startLocationSenderService(Context context) {
        // start location Sender on boot complete
        Intent mServiceIntent2 = new Intent(context, LocationSenderService.class);
        context.startService(mServiceIntent2);
    }

    public static void startLocationFinderService(Context context) {
        Intent mServiceIntent = new Intent(context, LocationFinderService.class);
        context.startService(mServiceIntent);
    }

    public static void startConnectionService(Context context){
        Intent intent = new Intent(context, ConnectionService.class);
        context.startService(intent);

    }
}
