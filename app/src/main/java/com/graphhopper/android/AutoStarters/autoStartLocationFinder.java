package com.graphhopper.android.AutoStarters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.graphhopper.android.Services.LocationFinderService;


/**
 * Created by aliparsa on 9/21/2014.
 */
public class autoStartLocationFinder extends BroadcastReceiver
{
    private boolean isFinderServiceRunning;

    public void onReceive(Context arg0, Intent arg1)
    {
        // start location finder on close
        Intent mServiceIntent = new Intent(arg0, LocationFinderService.class);
        arg0.startService(mServiceIntent);
    }








}
