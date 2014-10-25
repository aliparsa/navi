package com.graphhopper.android.utilities;


import android.content.Context;
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
    final static public String SERVER_ADDRESS = "http://79.175.166.110:8080";



    //------------------------------------------------------------------------------
    public static void sendDataToServer(Context context,BasicNameValuePair[] arr, final CallBack<ServerResponse> callBack) {
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


}