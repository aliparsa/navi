package com.graphhopper.android.AutoStarters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.graphhopper.android.Activities.MainActivity;
import com.graphhopper.android.Services.LocationFinderService;
import com.graphhopper.android.Services.LocationSenderService;


/**
 * Created by aliparsa on 9/21/2014.
 */
public class autoStartOnBootComplete extends BroadcastReceiver
{
    private boolean isFinderServiceRunning;

    public void onReceive(Context arg0, Intent arg1)
    {

        Intent mServiceIntent0 = new Intent(arg0, MainActivity.class);
        mServiceIntent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        arg0.startActivity(mServiceIntent0);


        // start location finder on boot complete
        Intent mServiceIntent = new Intent(arg0, LocationFinderService.class);
        arg0.startService(mServiceIntent);

        // start location Sender on boot complete
        Intent mServiceIntent2 = new Intent(arg0, LocationSenderService.class);
        arg0.startService(mServiceIntent2);

    }








}
