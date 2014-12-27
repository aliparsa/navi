
package com.graphhopper.android.Services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.graphhopper.android.DataModel.Task;
import com.graphhopper.android.Helpers.DeviceInfoHelper;


import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by ashkan on 2014-10-11.
 */

public class ConnectionService extends Service {

    Socket socket;
    public final static String CONNECTION_SERVICE_INTENT="CONNECTION_SERVICE_INTENT";
    public final static String CONNECTION_STATUS="CONNECTION_STATUS";
    private final String SOCKET_ADDRESS = "http://192.168.0.76:8888";
    private final long RECONNECT_SLEEP_TIME = 3000;
    private boolean isSocketConnected = false;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("ashkan","connection service started");
        sendConnectionStatusIntent(2);


        startConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{

        if (intent.hasExtra("locations")) {
            String json = intent.getStringExtra("locations");
            if (isSocketConnected)
                socket.emit("locations", new JSONArray(json));
        }

        if (intent.hasExtra("sos_location")) {
            Location sos_location = (Location) intent.getParcelableExtra("sos_location");
            JSONObject json = new JSONObject("{\"device_id\":"+ DeviceInfoHelper.getDevice_id()+",\"tag\":\"sos\",\"lat\":"+sos_location.getLatitude()+",\"lon\":"+sos_location.getLongitude()+"}");
             if (isSocketConnected)
              socket.emit("sos_location", json);
        }

        } catch (Exception e) {
e.printStackTrace();
        }


        return super.onStartCommand(intent, flags, startId);

    }

    private void startConnection() {

        try{
            IO.Options opts = new IO.Options();
            opts.forceNew = false;
            opts.reconnection = true;
            opts.timeout = 15000;

            socket = IO.socket(SOCKET_ADDRESS, opts);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    try {
                        socket.emit("introduce", new JSONObject("{\"device_id\":\"5\"}"));

                        Log.i("ashkan", "socket connected to " + SOCKET_ADDRESS);
                        isSocketConnected=true;
                        sendConnectionStatusIntent(1);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            socket.on("task", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    try {
                        JSONObject obj = (JSONObject) args[0];
                        //String StrJsonObject  = (String) args[0];
                        broadcastObject(obj);
                        //broadcast(StrJsonObject);
                        Log.i("ashkan", "socket new message news recieved");

                        //sendNotification("hello", obj.getString("hello"));
                    }catch (Exception e){

                        int x = 0;
                    }
                }
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.i("ashkan", "socket connection error");
                    isSocketConnected=false;
                    sendConnectionStatusIntent(2);
                    Log.i("ashkan", "thread sleep before reconnect");

                    try {
                        Thread.sleep(RECONNECT_SLEEP_TIME);
                    } catch (Exception e) {

                    }

                    Log.i("ashkan", "socket trying to connect....");
                    sendConnectionStatusIntent(3);


                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.i("ashkan", "socket dissconnected");
                    isSocketConnected=false;
                    sendConnectionStatusIntent(2);

                    Log.i("ashkan", "thread sleep before reconnect");

                    try {
                        Thread.sleep(RECONNECT_SLEEP_TIME);
                    } catch (Exception e) {

                    }


                    Log.i("ashkan", "socket trying to connect....");
                    sendConnectionStatusIntent(3);

                }
            });

            socket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    Log.i("ashkan", "socket reconnected");
                    isSocketConnected=true;
                    sendConnectionStatusIntent(1);
                }
            });

            socket.connect();
            Log.i("ashkan", "socket trying to connect....");
            sendConnectionStatusIntent(3);


        }catch(Exception e){

            e.printStackTrace();

        }
    }



/* public void sendNotification(String title, String text){

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        CharSequence notiText = "Your notification from the service";
        long meow = System.currentTimeMillis();

        Notification notification = new Notification(icon, notiText, meow);

        notification.flags = Notification.DEFAULT_VIBRATE;

        Context context = getApplicationContext();
        CharSequence contentTitle = title;
        CharSequence contentText = text;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        int SERVER_DATA_RECEIVED = 1;
        notificationManager.notify(SERVER_DATA_RECEIVED, notification);
    }
*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        socket.disconnect();
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private void broadcastObject(JSONObject obj)//this method sends broadcast messages
    {
        try {



            Intent intent = new Intent(CONNECTION_SERVICE_INTENT);
            intent.putExtra("TASK", new Task(obj.getDouble("fromLat"), obj.getDouble("fromLon"), obj.getDouble("toLat"), obj.getDouble("toLon"),obj.getString("description"),obj.getString("date")));
            sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void broadcast(String obj)//this method sends broadcast messages
    {
        try {

            Intent intent = new Intent("MESSAGE");
            intent.putExtra("MESSAGE", obj);
            sendBroadcast(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendConnectionStatusIntent(int connectionStatus)//this method sends broadcast messages
    {
        try {
            Intent intent = new Intent(CONNECTION_STATUS);
            intent.putExtra("status", connectionStatus);
            sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public enum ConnectionStatus{
        Connected,Disconnected,Connecting
    }




    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -


}
