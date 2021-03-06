package com.graphhopper.android.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


import com.graphhopper.android.DataModel.MyLocation;
import com.graphhopper.android.DataModel.ServerResponse;
import com.graphhopper.android.Helpers.DeviceInfoHelper;
import com.graphhopper.android.Interfaces.CallBack;
import com.graphhopper.android.Activities.MainActivity;
import com.graphhopper.android.Helpers.DatabaseHelper;
import com.graphhopper.android.utilities.Webservice;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by aliparsa on 9/20/2014.
 */
public class LocationSenderService extends Service {

    public static final String LOCATIONS_LIST = "LOCATIONS_LIST";
    PowerManager.WakeLock wakeLock;



    public LocationSenderService() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

        Log.i("alix", "Sender Service Created");
       // ((MyActivity) MyActivity.context).alog("Sender Service Created");

    }


    @Override
    @Deprecated
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

        Log.i("alix", "Sender Service Started");
        //((MyActivity) MyActivity.context).alog("Sender Service Started");

        Thread timer = new Thread() {
            public void run () {
                for (;;) {
                    try {
                        //checkAndSendPointToServer();
                        SendAllUnsentPoint();
                        Thread.sleep(5000);
                    }
                    catch (Exception e){
                        e.printStackTrace();

                    }
                }
            }
        };

        timer.start();
    }



    public void SendAllUnsentPoint(){
        Log.i("alix","check DB for point");
        final DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.context);
        ArrayList<MyLocation> loc_list = databaseHelper.getUnsentLocations();

        if (loc_list.size()==0)
            return;

        int maxId = -1;

        String jsonDeviceId = "\"device_id\":"+ DeviceInfoHelper.getDevice_id();
        String json = "[";


        for (int i = 0; i < loc_list.size(); i++) {

            MyLocation loc = loc_list.get(i);

            if (loc.getRecord_id()>maxId)
                maxId=loc.getRecord_id();

            json+="{";
                    json+="\"lat\":\""+loc.getLatitude()+"\",";
                    json+="\"lon\":\""+loc.getLongitude()+"\",";
                    json+="\"speed\":\""+loc.getSpeed()+"\",";
                    json+="\"date\":\""+loc.getDate()+"\"";
                  //  json+="\"device_id\":\""+"10"+"\"";

            json+="}";

            if (i<loc_list.size()-1)
                json+=",";
        }

        json+="]";

        String finalJson = "[{"+jsonDeviceId+","+"\"locations\":"+json+"}]";


        //BroadcastLocationsList(json);
        BroadcastLocationsList(finalJson);


        BasicNameValuePair[] arr = {
                new BasicNameValuePair("tag", "location"),
                new BasicNameValuePair("locations",json )
        };
        final int maximumId = maxId;
        Webservice.sendDataToServer(MainActivity.context, arr, new CallBack<ServerResponse>() {
                    @Override
                    public void onSuccess(ServerResponse result) {
                        Log.i("alix", "Data Sent");

//                        if (MainActivity.context != null)
//                            ((MainActivity) MainActivity.context).alog("Data Sent");

                        databaseHelper.bulkMarkRecordsAsSent(maximumId);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.i("alix", "Error >>>" + errorMessage);

//                        if (MyActivity.context != null)
//                            ((MyActivity) MyActivity.context).alog("Error >>>" + errorMessage);
                    }
                }
        );
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        sendBroadcast(new Intent("LOCATION_SENDER"));
        wakeLock.release();

    }

 /*   public JSONArray convertLocArrayToJsonArray(ArrayList<MyLocation> loc_list){
        JSONObject jsonObject;
        for (int i = 0; i < loc_list.size(); i++) {
            jsonObject.put();

        }
    }*/

    private void BroadcastLocationsList(String jsonLocationsList)//this method sends broadcast messages
    {
        try {

            Intent intent = new Intent(getApplicationContext(),ConnectionService.class);
            intent.putExtra("locations", jsonLocationsList);
            startService(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
