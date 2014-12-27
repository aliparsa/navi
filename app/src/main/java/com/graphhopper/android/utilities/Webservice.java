package com.graphhopper.android.utilities;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import com.graphhopper.android.DataModel.ServerResponse;
import com.graphhopper.android.Helpers.HttpHelper;
import com.graphhopper.android.Interfaces.CallBack;
import com.graphhopper.android.Interfaces.ResponseHandler;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aliparsa on 8/9/2014.
 */


public class Webservice {
    //this is sparta


    //final static public String SERVER_ADDRESS = "http://192.168.1.66:8099/";
    //final static public String SERVER_ADDRESS = "http://ict.farsportal.com/android/locgetter.php";
    static public String SERVER_ADDRESS = "http://79.175.166.110:8080";

    static public String SOCKET_ADDRESS = "http://79.175.166.110:8080";



    public static void prepareServerAddress(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String server_address = preferences.getString("server_address", null);

        if (server_address != null) {
            SERVER_ADDRESS = server_address;
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("server_address", SERVER_ADDRESS);
            editor.apply();
        }


    }
    // - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - ]
    public static void prepareSocketAddress(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String socket_address = preferences.getString("socket_address", null);

        if (socket_address != null) {
            SOCKET_ADDRESS = socket_address;
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("socket_address", SOCKET_ADDRESS);
            editor.apply();
        }


    }
    //------------------------------------------------------------------------------
    public static void sendDataToServer(Context context,BasicNameValuePair[] arr, final CallBack<ServerResponse> callBack) {

        prepareServerAddress(context);
        HttpHelper helper = new HttpHelper(context, SERVER_ADDRESS, false, 0);

        // sample arr
        /*BasicNameValuePair[] arr = {
                new BasicNameValuePair("tag", "location"),
                new BasicNameValuePair("device_id", device_id + ""),
                new BasicNameValuePair("date", device_id + ""),
                new BasicNameValuePair("lat", location.getLatitude() + ""),
                new BasicNameValuePair("lon", location.getLongitude() + ""),
        };*/
        helper.postHttp(arr, new ResponseHandler() {
            @Override
            public void handleResponse(ServerResponse response) {

                try {

                    switch (response.getStatusCode()) {
                        case SC_UNAUTHORIZED: {
                            callBack.onError("UNAUTHORIZED");
                            break;
                        }
                        case SC_OK: {
                            JSONObject jsonObject = new JSONObject(response.getResult());
                            callBack.onSuccess(response);
                            break;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    callBack.onError(e.getMessage());
                }

            }

            @Override
            public void error(String err) {
                Log.e("ali", " webservice / sendDataToServer ");
                callBack.onError(err);
            }
        });

    }


    public static String getServerAddress() {
        return SERVER_ADDRESS;
    }

    public static String getSocketAddress(Context context) {
        init(context);
        return SOCKET_ADDRESS;
    }

    public static void modifyServerAddress(String s, Context context) {
        SERVER_ADDRESS=s;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("server_address", s);
        editor.apply();
    }

    public static void modifySocketAddress(String s, Context context) {
        SOCKET_ADDRESS=s;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("socket_address", s);
        editor.apply();
    }

    public static void init(Context context) {
        prepareServerAddress(context);
        prepareSocketAddress(context);
    }
}