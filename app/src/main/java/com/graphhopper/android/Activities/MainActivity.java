package com.graphhopper.android.Activities;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.rendertheme.AssetsRenderTheme;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.android.AndroidHelper;
import com.graphhopper.android.DataModel.Task;
import com.graphhopper.android.DataModel.Taximeter;
import com.graphhopper.android.DataModel.VoiceFlags;
import com.graphhopper.android.GHAsyncTask;
import com.graphhopper.android.GpsHelper.GpsStatusListener;
import com.graphhopper.android.R;
import com.graphhopper.android.Services.ConnectionService;
import com.graphhopper.android.Services.LocationFinderService;
import com.graphhopper.android.Services.LocationSenderService;
import com.graphhopper.android.utilities.TimerHelper;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Downloader;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.PointList;
import com.graphhopper.util.ProgressListener;
import com.graphhopper.util.StopWatch;

public class MainActivity extends Activity implements GpsStatus.Listener, SensorEventListener {
    private MapView mapView;
    private GraphHopper hopper;
    private LatLong start;
    private LatLong end;
    private Spinner localSpinner;
    private Button localButton;
    private Spinner remoteSpinner;
    private Button remoteButton;
    private volatile boolean prepareInProgress = false;
    private volatile boolean shortestPathRunning = false;
    private String currentArea = "berlin";
    private String fileListURL = "https://graphhopper.com/public/maps/0.3/";
    private String prefixURL = fileListURL;
    private String downloadURL;
    private File mapsFolder;
    private TileCache tileCache;

    // my fields
    private LatLong lastKnowLocation;
    private boolean lockOnGps = true;
    public static Context context;
    private GpsStatusListener gpsStatusListener;
    LocationManager locationManager;

    ImageView img2;
    ImageView img4;
    private LatLong lastGPSLocation;
    TileRendererLayer tileRendererLayer;
    public boolean isInRoutingMode = false;
    GHResponse lastRouteResponce;

    SensorManager mSensorManager;
    Sensor mCompass;
    private float lastAzimuth = 0;

    TextView log;
    public boolean newLocationEventInProgress;

    Button btn_taximeter1;
    Button btn_taximeter2;
    Button btn_taximeter3;
    Button btn_taximeter4;

    Taximeter taximeter1;
    Taximeter taximeter2;
    Taximeter taximeter3;
    Taximeter taximeter4;

    public Location loc_lastKnowLocation;


    //Setting setting;

    LinearLayout llDirectionList;
    private double LastRouteLat;
    private double LastRouteLon;
    private int layerCount = -1;


    Button btn1;
    Button btn2;
    Button btn3;

    Button btnGpsStatus;
    Button btnSrvStatus;
    Button btnBtrStatus;
    Button btnSos;
    Button btnStopRouting;
    Button btnSetting;
    Button btnMessaging;
    Button btnPinOnGps;


    protected boolean onMapTap(LatLong tapLatLong, Point layerXY, Point tapXY) {


        if (isInRoutingMode)
        {
            Location location = new Location("ali");
            location.setLatitude(tapLatLong.latitude);
            location.setLongitude(tapLatLong.longitude);
            listener.onLocationChanged(location);
        }

        if (!isReady())
            return false;

        if (shortestPathRunning) {
            logUser("Calculation still in progress");
            return false;
        }
        Layers layers = mapView.getLayerManager().getLayers();


        //showSelectorDialog( );

        start = lastKnowLocation;

        if (start != null) {

            end = tapLatLong;
            shortestPathRunning = true;
            isInRoutingMode = true;

            if (voiceFlags!=null)
                voiceFlags.clear();

            calcPath(start.latitude, start.longitude, end.latitude,
                    end.longitude);

        }

       /* else
        {
            //showSelectorDialog();
            start = tapLatLong;
            end = null;
            // remove all layers but the first one, which is the map
            while (layers.size() > 1)
            {
                layers.remove(1);
            }

            Marker marker = createMarker(start, R.drawable.flag_green);
            if (marker != null)
            {
                layers.add(marker);
            }
        }*/


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = this;
        AndroidGraphicFactory.createInstance(getApplication());


        mapView = new MapView(this) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                //lockOnGps=false;
                Log.i("ali", "> > > > > > > unlock");
                return super.onTouchEvent(motionEvent);

            }
        };


        mapView.setClickable(true);
        mapView.setBuiltInZoomControls(true);


        tileCache = AndroidUtil.createTileCache(this, getClass().getSimpleName(), mapView.getModel().displayModel.getTileSize(),
                1f, mapView.getModel().frameBufferModel.getOverdrawFactor());

        final EditText input = new EditText(this);
        input.setText(currentArea);
        boolean greaterOrEqKitkat = Build.VERSION.SDK_INT >= 19;
        if (greaterOrEqKitkat) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                logUser("GraphHopper is not usable without an external storage!");
                return;
            }
            mapsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "/graphhopper/maps/");
        } else
            mapsFolder = new File(Environment.getExternalStorageDirectory(), "/graphhopper/maps/");

        if (!mapsFolder.exists())
            mapsFolder.mkdirs();

        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Welcome to GraphHopper " + Constants.VERSION + "!");
        welcome.setPadding(6, 3, 3, 3);
        localSpinner = (Spinner) findViewById(R.id.locale_area_spinner);
        localButton = (Button) findViewById(R.id.locale_button);
        remoteSpinner = (Spinner) findViewById(R.id.remote_area_spinner);
        remoteButton = (Button) findViewById(R.id.remote_button);
        // TODO get user confirmation to download
        // if (AndroidHelper.isFastDownload(this))


        //chooseAreaFromRemote();
        chooseAreaFromLocal();


        // ali code start from here


        //setting = new Setting(this);

        llDirectionList = (LinearLayout) findViewById(R.id.directionList);

        loc_lastKnowLocation = new Location("ali");
        loc_lastKnowLocation.setLatitude(29.63854654);
        loc_lastKnowLocation.setLongitude(52.52854654);
        loc_lastKnowLocation.setTime(new Date().getTime());

        btn_taximeter1 = (Button) findViewById(R.id.taximeter1);
        btn_taximeter2 = (Button) findViewById(R.id.taximeter2);
        btn_taximeter3 = (Button) findViewById(R.id.taximeter3);
        btn_taximeter4 = (Button) findViewById(R.id.taximeter4);

        btn_taximeter1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taximeter1 == null) {
                    taximeter1 = new Taximeter();
                    taximeter1.start(loc_lastKnowLocation);
                    btn_taximeter1.setBackgroundResource(R.drawable.taximeter_background);

                } else {
                    calculateAndShowFair(taximeter1);


                    taximeter1 = null;
                    btn_taximeter1.setBackgroundResource(R.drawable.taximeter);
                    btn_taximeter1.setText("");

                }

            }
        });

        btn_taximeter2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taximeter2 == null) {
                    taximeter2 = new Taximeter();
                    taximeter2.start(loc_lastKnowLocation);
                    btn_taximeter2.setBackgroundResource(R.drawable.taximeter_background);


                } else {


                    calculateAndShowFair(taximeter2);


                    taximeter2 = null;
                    btn_taximeter2.setBackgroundResource(R.drawable.taximeter);
                    btn_taximeter2.setText("");


                }

            }
        });

        btn_taximeter3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taximeter3 == null) {
                    taximeter3 = new Taximeter();
                    taximeter3.start(loc_lastKnowLocation);
                    btn_taximeter3.setBackgroundResource(R.drawable.taximeter_background);


                } else {

                    calculateAndShowFair(taximeter3);


                    taximeter3 = null;
                    btn_taximeter3.setBackgroundResource(R.drawable.taximeter);
                    btn_taximeter3.setText("");


                }

            }
        });

        btn_taximeter4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taximeter4 == null) {
                    taximeter4 = new Taximeter();
                    taximeter4.start(loc_lastKnowLocation);
                    btn_taximeter4.setBackgroundResource(R.drawable.taximeter_background);


                } else {


                    calculateAndShowFair(taximeter4);
                    taximeter4 = null;
                    btn_taximeter4.setBackgroundResource(R.drawable.taximeter);
                    btn_taximeter4.setText("");


                }

            }
        });

        log = (TextView) findViewById(R.id.log);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_NORMAL);


       // satlateInView = (TextView) findViewById(R.id.satlate_in_view);

        img4 = (ImageView) findViewById(R.id.imageView6);

        btnPinOnGps = (Button) findViewById(R.id.btnPinOnGps);
        btnPinOnGps.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastKnowLocation != null) {
                    lockOnGps = !lockOnGps;

                    if (lockOnGps) {
                        animateToPoint(lastKnowLocation.latitude, lastKnowLocation.longitude);
                        Toast.makeText(context, "همیشه در مرکز نقشه هستید", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "همیشه در مرکز غیر فعال شد", Toast.LENGTH_SHORT).show();

                    }
                }
               /* else {
                    Toast.makeText(context, "Position not defined yet", Toast.LENGTH_SHORT).show();
                }*/
            }
        });






        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 1, listener);

        locationManager.addGpsStatusListener(this);


        //Toast.makeText(context,locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).toString(),Toast.LENGTH_SHORT).show();

        Location lastloc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastloc != null)
            lastKnowLocation = new LatLong(lastloc.getLatitude(), lastloc.getLongitude());
        else
            lastKnowLocation = new LatLong(29.615337, 52.524848);


        TimerHelper.timerFactory(1000, 0, new TimerHelper.TimerFunction() {
            @Override
            public void tick() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        if (taximeter1 != null) {
                            taximeter1.timer++;
                            btn_taximeter1.setText(convertSecToStr(taximeter1.timer));
                            btn_taximeter1.append("\n" + (int)taximeter1.getRouteDistance() + "M");
                        }

                        if (taximeter2 != null) {
                            taximeter2.timer++;
                            btn_taximeter2.setText(convertSecToStr(taximeter2.timer));
                            btn_taximeter2.append("\n" + (int)taximeter2.getRouteDistance() + "M");

                        }
                        if (taximeter3 != null) {
                            taximeter3.timer++;
                            btn_taximeter3.setText(convertSecToStr(taximeter3.timer));
                            btn_taximeter3.append("\n" +(int) taximeter3.getRouteDistance() + "M");

                        }
                        if (taximeter4 != null) {
                            taximeter4.timer++;
                            btn_taximeter4.setText(convertSecToStr(taximeter4.timer));
                            btn_taximeter4.append("\n" + (int)taximeter4.getRouteDistance() + "M");

                        }


                    }

                });
            }

        });

        localButton.callOnClick();

         btn1 = (Button) findViewById(R.id.button);
         btn2 = (Button) findViewById(R.id.button2);
         btn3 = (Button) findViewById(R.id.button3);

        btn1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = new Location("ali");
                location.setLatitude(29.61747028);
                location.setLongitude(52.52598551);
               listener.onLocationChanged(location);
            }
        });

        btn2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = new Location("ali");
                location.setLatitude(29.61740732);
                location.setLongitude(52.52607100);
                listener.onLocationChanged(location);

            }
        });

        btn3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = new Location("ali");
                location.setLatitude(29.6172067);
                location.setLongitude(52.52643042);
                listener.onLocationChanged(location);
            }
        });

        startConnectionService();





        Intent mServiceIntent = new Intent(this, LocationFinderService.class);
        startService(mServiceIntent);

        // start location Sender on boot complete
        Intent mServiceIntent2 = new Intent(this, LocationSenderService.class);
        startService(mServiceIntent2);


        btnGpsStatus = (Button) findViewById(R.id.btnGpsStatus);
        btnSrvStatus = (Button) findViewById(R.id.btnSrvStatus);
        btnBtrStatus = (Button) findViewById(R.id.btnBtrStatus);
        btnSos = (Button) findViewById(R.id.btnSos);
        btnStopRouting = (Button) findViewById(R.id.btnStopRouting);
        btnSetting = (Button) findViewById(R.id.btnSetting);
        btnMessaging = (Button) findViewById(R.id.btnMessaging);

        btnMessaging.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessagingActivity.class);
                startActivity(intent);
            }
        });


        btnSetting.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivity(intent);

            }
        });

        btnStopRouting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRouting();
            }
        });

        btnSos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new AlertDialog.Builder(context)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle("درخواست کمک")
                        .setCancelable(false)
                        .setMessage("آیا درخواست کمک به سرور ارسال شود ؟")
                        .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO Dismiss
                            }
                        })

                        .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO ersal darkhast help be server
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        // ali code end here


    }


    public String convertSecToStr(Long sec) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(new Date(sec * 1000));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hopper != null)
            hopper.close();

        hopper = null;
        // necessary?
        System.gc();
    }

    boolean isReady() {
        // only return true if already loaded
        if (hopper != null)
            return true;

        if (prepareInProgress) {
            logUser("Preparation still in progress");
            return false;
        }
        log("Prepare finished but hopper not ready. This happens when there was an error while loading the files");
        return false;
    }

    private void initFiles(String area) {
        prepareInProgress = true;
        currentArea = area;
        downloadingFiles();
    }

    private void chooseAreaFromLocal() {
        List<String> nameList = new ArrayList<String>();
        String[] files = mapsFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename != null
                        && (filename.endsWith(".ghz") || filename
                        .endsWith("-gh"));
            }
        });
        for (String file : files) {
            nameList.add(file);
        }

        if (nameList.isEmpty())
            return;

        chooseArea(localButton, localSpinner, nameList,
                new MySpinnerListener() {
                    @Override
                    public void onSelect(String selectedArea, String selectedFile) {
                        initFiles(selectedArea);
                    }
                }
        );
    }

    private void chooseAreaFromRemote() {
        new GHAsyncTask<Void, Void, List<String>>() {
            protected List<String> saveDoInBackground(Void... params)
                    throws Exception {
                String[] lines = new Downloader("GraphHopper Android").
                        downloadAsString(fileListURL).split("\n");
                List<String> res = new ArrayList<String>();
                for (String str : lines) {
                    int index = str.indexOf("href=\"");
                    if (index >= 0) {
                        index += 6;
                        int lastIndex = str.indexOf(".ghz", index);
                        if (lastIndex >= 0)
                            res.add(prefixURL + str.substring(index, lastIndex)
                                    + ".ghz");
                    }
                }

                return res;
            }

            @Override
            protected void onPostExecute(List<String> nameList) {
                if (hasError() || nameList.isEmpty()) {
                    logUser("Are you connected to the internet? Problem while fetching remote area list: "
                            + getErrorMessage());
                    return;
                }
                MySpinnerListener spinnerListener = new MySpinnerListener() {
                    @Override
                    public void onSelect(String selectedArea, String selectedFile) {
                        if (selectedFile == null
                                || new File(mapsFolder, selectedArea + ".ghz")
                                .exists()
                                || new File(mapsFolder, selectedArea + "-gh")
                                .exists()) {
                            downloadURL = null;
                        } else {
                            downloadURL = selectedFile;
                        }
                        initFiles(selectedArea);
                    }
                };
                chooseArea(remoteButton, remoteSpinner, nameList,
                        spinnerListener);
            }
        }.execute();
    }

    private void chooseArea(Button button, final Spinner spinner,
                            List<String> nameList, final MySpinnerListener mylistener) {
        final Map<String, String> nameToFullName = new TreeMap<String, String>();
        for (String fullName : nameList) {
            String tmp = Helper.pruneFileEnd(fullName);
            if (tmp.endsWith("-gh"))
                tmp = tmp.substring(0, tmp.length() - 3);

            tmp = AndroidHelper.getFileName(tmp);
            nameToFullName.put(tmp, fullName);
        }
        nameList.clear();
        nameList.addAll(nameToFullName.keySet());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, nameList);
        spinner.setAdapter(spinnerArrayAdapter);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object o = spinner.getSelectedItem();
                if (o != null && o.toString().length() > 0 && !nameToFullName.isEmpty()) {
                    String area = o.toString();
                    mylistener.onSelect(area, nameToFullName.get(area));
                } else {
                    mylistener.onSelect(null, null);
                }
            }
        });
    }
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
                Location gpslocation = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

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



    public interface MySpinnerListener {
        void onSelect(String selectedArea, String selectedFile);
    }

    void downloadingFiles() {
        final File areaFolder = new File(mapsFolder, currentArea + "-gh");
        if (downloadURL == null || areaFolder.exists()) {
            loadMap(areaFolder);
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Downloading and uncompressing " + downloadURL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        new GHAsyncTask<Void, Integer, Object>() {
            protected Object saveDoInBackground(Void... _ignore)
                    throws Exception {
                String localFolder = Helper.pruneFileEnd(AndroidHelper.getFileName(downloadURL));
                localFolder = new File(mapsFolder, localFolder + "-gh").getAbsolutePath();
                log("downloading & unzipping " + downloadURL + " to " + localFolder);
                new Downloader("GraphHopper Android").downloadAndUnzip(downloadURL, localFolder,
                        new ProgressListener() {
                            @Override
                            public void update(long val) {
                                publishProgress((int) val);
                            }
                        }
                );
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                dialog.setProgress(values[0]);
            }

            protected void onPostExecute(Object _ignore) {
                dialog.hide();
                if (hasError()) {
                    String str = "An error happend while retrieving maps:" + getErrorMessage();
                    log(str, getError());
                    logUser(str);
                } else {
                    loadMap(areaFolder);
                }
            }
        }.execute();
    }

    void loadMap(File areaFolder) {
        logUser("بارگذاری نقشه");
        File mapFile = new File(areaFolder, currentArea + ".map");

        mapView.getLayerManager().getLayers().clear();


        tileRendererLayer = new TileRendererLayer(tileCache, mapView.getModel().mapViewPosition,
                true, AndroidGraphicFactory.INSTANCE) {
            @Override
            public boolean onLongPress(LatLong tapLatLong, Point layerXY, Point tapXY) {
                return onMapTap(tapLatLong, layerXY, tapXY);
            }


        };
        tileRendererLayer.setMapFile(mapFile);
        tileRendererLayer.setTextScale(1.5f);


        try {
            tileRendererLayer.setXmlRenderTheme(new AssetsRenderTheme(context, "", "myRender.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        //tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
//        mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(tileRendererLayer.getMapDatabase().getMapFileInfo().boundingBox.getCenterPoint(), (byte) 15));

        mapView.getModel().mapViewPosition.setMapPosition(
                new MapPosition(lastKnowLocation, (byte) 15));


        mapView.getLayerManager().getLayers().add(tileRendererLayer);


        mapView.setBackgroundColor(Color.WHITE);
        mapView.getModel().displayModel.setBackgroundColor(Color.WHITE);


        LinearLayout llCongig = (LinearLayout) findViewById(R.id.llConfig);
        llCongig.setVisibility(View.GONE);

        RelativeLayout llMap = (RelativeLayout) findViewById(R.id.llMap2);

        llMap.addView(mapView, 0);
        // setContentView(mapView);vbdf
        loadGraphStorage();


// ali code to show blue circle on start on last known location
        showImgOnThisPoint(lastKnowLocation, R.drawable.blue_circle);


    }

    void loadGraphStorage() {
        logUser("بارگذاری مسیریاب (" + Constants.VERSION + ") ... ");
        new GHAsyncTask<Void, Void, Path>() {
            protected Path saveDoInBackground(Void... v) throws Exception {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.setCHShortcuts("fastest");
                tmpHopp.load(new File(mapsFolder, currentArea).getAbsolutePath());
                log("found graph " + tmpHopp.getGraph().toString() + ", nodes:" + tmpHopp.getGraph().getNodes());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute(Path o) {
                if (hasError()) {
                    logUser("An error happend while creating graph:"
                            + getErrorMessage());
                } else {
                    logUser("آماده");
                }

                finishPrepare();
            }
        }.execute();
    }

    private void finishPrepare() {
        prepareInProgress = false;
    }

    private Polyline createPolyline(GHResponse response) {
        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.BLUE);
//        paintStroke.setDashPathEffect(new float[]
//        {
//            25, 15
//        });
        paintStroke.setStrokeWidth(9


        );

        // TODO: new mapsforge version wants an mapsforge-paint, not an android paint.
        // This doesn't seem to support transparceny
        //paintStroke.setAlpha(128);
        Polyline line = new Polyline((org.mapsforge.core.graphics.Paint) paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = response.getPoints();
        for (int i = 0; i < response.getPoints().getSize(); i++) {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));


        }

        return line;
    }

    private Marker createMarker(LatLong p, int resource) {
        Drawable drawable = getResources().getDrawable(resource);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        return new Marker(p, bitmap, 0, -bitmap.getHeight() / 2);
    }

    public void calcPath(final double fromLat, final double fromLon,
                         final double toLat, final double toLon) {

        log("calculating path ...");
        new AsyncTask<Void, Void, GHResponse>() {
            float time;

            protected GHResponse doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).
                        setAlgorithm("dijkstrabi").
                        putHint("instructions", true).
                        putHint("douglas.minprecision", 1);
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute(GHResponse resp) {
                if (!resp.hasErrors()) {

                    btnStopRouting.setVisibility(View.VISIBLE);
                    lastRouteResponce = resp;

                    if (lastRouteResponce != null) {
                        LastRouteLat = lastRouteResponce.getInstructions().get(0).getPoints().getLat(0);
                        LastRouteLon = lastRouteResponce.getInstructions().get(0).getPoints().getLon(0);
                    }

                    createAndShowInstructionList(lastRouteResponce, llDirectionList);

                    createAndPlayVoiceCommand(lastRouteResponce);




                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                            + toLon + " found path with distance:" + resp.getDistance()
                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                            + time + " " + resp.getDebugInfo());

//                    logUser("the route is " + (int) (resp.getDistance() / 100) / 10f
//                            + "km long, time:" + resp.getMillis() / 60000f + "min, debug:" + time);

                    logUser(" طول مسیر " +
                                    (int) (resp.getDistance() / 100) / 10f +
                                    " کیلومتر "
                    );

                    /*if (lastRouteResponce!=null && lastRouteResponce.getInstructions()!=null && lastRouteResponce.getInstructions().size()>0) {
                        satlateInView.setText(getStringFromSign(lastRouteResponce.getInstructions().get(0).getSign()));
                        satlateInView.append(" پس از ");
                        satlateInView.append(((int) lastRouteResponce.getInstructions().get(0).getDistance()) + "");
                        satlateInView.append(" متر ");
                        satlateInView.append(getStringFromSign(lastRouteResponce.getInstructions().get(1).getSign()));
                    }*/





                    clearMapView();

                    showImgOnThisPoint(lastKnowLocation, R.drawable.blue_circle);

                    // draw routing
                    mapView.getLayerManager().getLayers().add(createPolyline(resp));


                    // create end flag
                  /*  Marker marker = createMarker(new LatLong(toLat,toLon), R.drawable.destination);
                    if (marker != null)
                    {
                        // add to mapview
                        mapView.getLayerManager().getLayers().add(marker);
                    }*/


                    //mapView.redraw();
                } else {
                    logUser("Error:" + resp.getErrors());
                }

                shortestPathRunning = false;
            }
        }.execute();
    }

    private void createAndShowInstructionList(GHResponse lastRouteResponce,LinearLayout llDirectionList) {
        llDirectionList.removeAllViews();
        for (int i = 0; i < lastRouteResponce.getInstructions().size(); i++) {

            final Instruction instruction = lastRouteResponce.getInstructions().get(i);

            LayoutInflater inflater = getLayoutInflater();
            RelativeLayout llDirection_item = (RelativeLayout) inflater.inflate(R.layout.direction_item, null);
            ImageView img = (ImageView) llDirection_item.findViewById(R.id.imageView);
            TextView txt = (TextView) llDirection_item.findViewById(R.id.textView);

            String insName = instruction.getName();
            int insDistance = (int) instruction.getDistance();
            int insSign = (int) instruction.getSign();


            // if (insDistance==0 && insSign !=4) continue;


            switch (insSign) {
                case -3:
                    txt.setText(" پیچ تند به سمت چپ ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.hardleft);
                    break;
                case -2:
                    txt.setText(" پیچ به سمت چپ ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.left);
                    break;
                case -1:
                    txt.setText("پیچ ملایم به سمت چپ ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.light_left);
                    break;
                case 0:
                    img.setImageResource(R.drawable._continue);
                    if (insName.length() > 0)
                        txt.setText(" در  " + insName + "\n" + insDistance + " متر " + " ادامه دهید ");
                    else
                        txt.setText(" در همین خیابان " + "\n" + insDistance + " متر " + " ادامه دهید ");
                    break;
                case 1:
                    txt.setText(" پیچ ملایم به سمت راست ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.light_right);
                    break;
                case 2:
                    txt.setText(" پیچ  به سمت راست ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.right);
                    break;
                case 3:
                    txt.setText(" پیچ تند به سمت راست ");
                    if (insName.length() > 0)
                        txt.append("\n" + " به " + insName);
                    txt.append("\n" + " سپس " + insDistance + " متر " + " ادامه مسیر ");
                    img.setImageResource(R.drawable.hardright);
                    break;
                case 4:
                    txt.setText(" به مقصد میرسید ");
                    img.setImageResource(R.drawable.destination);
                    break;
            }

            llDirection_item.setTag(instruction);
            llDirection_item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawPolylineByPoints(instruction.getPoints());
                }
            });
            llDirectionList.addView(llDirection_item);

        }
    }

    private void log(String str) {
        Log.i("GH", str);
    }

    private void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    private void logUser(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    private static final int NEW_MENU_ID = Menu.FIRST + 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, NEW_MENU_ID, 0, "Google");
        // menu.add(0, NEW_MENU_ID + 1, 0, "Other");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case NEW_MENU_ID:
                if (start == null || end == null) {
                    logUser("tap screen to set start and end of route");
                    break;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // get rid of the dialog
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                intent.setData(Uri.parse("http://maps.google.com/maps?saddr="
                        + start.latitude + "," + start.longitude + "&daddr="
                        + end.latitude + "," + end.longitude));
                startActivity(intent);
                break;
        }
        return true;
    }

    private void showImgOnThisPoint(LatLong latlong, int IMGresID) {
        Marker marker = new Marker(latlong,
                AndroidGraphicFactory.convertToBitmap((getResources().getDrawable(IMGresID))),
                0,
                0);
        mapView.getLayerManager().getLayers().add(marker);
    }

    private void animateToPoint(double lat, double lon) {
        mapView.getModel().mapViewPosition.animateTo(new LatLong(lat, lon));
    }

    private LocationListener listener = new LocationListener() {


        @Override
        public void onLocationChanged(Location location) {

            btnGpsStatus.setBackgroundColor(Color.GREEN);

            loc_lastKnowLocation = location;


            // send location to store in taximeters
            putLocationToActiveTaximeters(location);

            if (newLocationEventInProgress) {
                log.append("new location event ignored\n");
                return;
            }

            newLocationEventInProgress = true;

            try {

                log.append("new lat lon\n");
                // if we lock on gps we navigate to new position
                if (lockOnGps)
                    animateToPoint(location.getLatitude(), location.getLongitude());

                // get last location
                lastGPSLocation = new LatLong(location.getLatitude(), location.getLongitude());
                lastKnowLocation = new LatLong(location.getLatitude(), location.getLongitude());


                if (isInRoutingMode) {
                    // we are in routing mode
                    log.append("we are in routing mode\n");


                    if (!isReady()) {
                        log.append("no ready >ret\n");
                        return;
                    }

                    if (shortestPathRunning) {
                        log.append("route in running >ret\n");
                        return;
                    }

                    shortestPathRunning = true;
                    log.append("req new route from lat: " + location.getLatitude() + " lon: " + location.getLongitude() + " to lat: " + end.latitude + " lon: " + end.longitude + "\n");
                    calcPath(location.getLatitude(), location.getLongitude(), end.latitude, end.longitude);

                } else {
                    // we are not in routing mode
                    // show new current point on map
                    log.append("NOT in routing mode\n");
                    clearMapView();
                    showImgOnThisPoint(lastKnowLocation, R.drawable.blue_circle);
                }


            } catch (Exception e) {
                logUser(e.getMessage());
                newLocationEventInProgress = false;
            }

            newLocationEventInProgress = false;
        }

        @Override
        public void onProviderDisabled(String provider) {
            btnGpsStatus.setBackgroundColor(Color.GRAY);
        }

        @Override
        public void onProviderEnabled(String provider) {
            btnGpsStatus.setBackgroundColor(Color.YELLOW);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //satlateInView.setText(gpsStatusListener.getSatlateInView()+"");


        }
    };

    public void clearMapView() {
        while (mapView.getLayerManager().getLayers().size() > 1) {
            mapView.getLayerManager().getLayers().remove(1);
        }
    }

    public void cancelRouting() {
        Dialog dialog = new AlertDialog.Builder(context)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("مسیریابی")
                .setCancelable(false)
                .setMessage("آیا مسیریابی فعلی لغو شود ؟")
                .setPositiveButton("بله", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isInRoutingMode = false;
                        clearMapView();
                        showImgOnThisPoint(lastKnowLocation, R.drawable.blue_circle);
                        llDirectionList.removeAllViews();
                        btnStopRouting.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();

        dialog.show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float azimuth = Math.round(sensorEvent.values[0]);
        float pitch = Math.round(sensorEvent.values[1]);
        float roll = Math.round(sensorEvent.values[2]);
        // The other values provided are:
        //  float pitch = event.values[1];
        //  float roll = event.values[2];
        // log.setText("Azimuth: " + Float.toString(azimuth) + "\n");
        //satlateInView.append("pitch: " + Float.toString(pitch) + "\n");
        //satlateInView.append("roll: " + Float.toString(roll)+"\n");

        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(false);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(-lastAzimuth, -azimuth,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(200);
        animRotate.setFillAfter(false);
        animSet.addAnimation(animRotate);


        img4.startAnimation(animSet);
        lastAzimuth = azimuth;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void putLocationToActiveTaximeters(Location location) {

        if (taximeter1 != null)
            taximeter1.addToRoute(location);


        if (taximeter2 != null)
            taximeter2.addToRoute(location);

        if (taximeter3 != null)
            taximeter3.addToRoute(location);

        if (taximeter4 != null)
            taximeter4.addToRoute(location);
    }



    public int getVoiceFromSign(int sign) {
        switch (sign) {
            case -3:
                return R.raw.left_sh;
            case -2:
                return R.raw.left;
            case -1:
                return R.raw.left_sl;
            case 0:
                return R.raw.go_ahead;
            case 1:
                return R.raw.right_sl;
            case 2:
                return R.raw.right;
            case 3:
                return R.raw.right_sh;
            case 4:
                return R.raw.reached_destination;
//            case 5:
//                return "REACHED_VIA";

        }
        return 0;
    }

    public int getVoiceId(int meter) {

        if (meter == 1) return R.raw._1;
        if (meter == 2) return R.raw._2;
        if (meter == 3) return R.raw._3;
        if (meter == 4) return R.raw._4;
        if (meter == 5) return R.raw._5;
        if (meter == 6) return R.raw._6;
        if (meter == 7) return R.raw._7;
        if (meter == 8) return R.raw._8;
        if (meter == 9) return R.raw._9;
        if (meter == 10) return R.raw._10;
        if (meter == 11) return R.raw._11;
        if (meter == 12) return R.raw._12;
        if (meter == 13) return R.raw._13;
        if (meter == 14) return R.raw._14;
        if (meter == 15) return R.raw._15;
        if (meter == 16) return R.raw._16;
        if (meter == 17) return R.raw._17;
        if (meter == 18) return R.raw._18;
        if (meter == 19) return R.raw._19;
        if (meter > 19 && meter <= 20) return R.raw._20;
        if (meter > 20 && meter <= 25) return R.raw._25;
        if (meter > 25 && meter <= 30) return R.raw._30;
        if (meter > 30 && meter <= 35) return R.raw._35;
        if (meter > 35 && meter <= 40) return R.raw._40;
        if (meter > 40 && meter <= 45) return R.raw._45;
        if (meter > 45 && meter <= 50) return R.raw._50;
        if (meter > 50 && meter <= 55) return R.raw._55;
        if (meter > 55 && meter <= 60) return R.raw._60;
        if (meter > 60 && meter <= 65) return R.raw._65;
        if (meter > 65 && meter <= 70) return R.raw._70;
        if (meter > 70 && meter <= 75) return R.raw._75;
        if (meter > 75 && meter <= 80) return R.raw._80;
        if (meter > 80 && meter <= 85) return R.raw._85;
        if (meter > 85 && meter <= 90) return R.raw._90;
        if (meter > 90 && meter <= 95) return R.raw._95;
        if (meter > 95 && meter <= 100) return R.raw._100;
        if (meter > 100 && meter <= 150) return R.raw._150;
        if (meter > 150 && meter <= 200) return R.raw._200;

        if (meter > 200 && meter <= 250) return R.raw._250;
        if (meter > 250 && meter <= 300) return R.raw._300;
        if (meter > 300 && meter <= 350) return R.raw._350;
        if (meter > 350 && meter <= 400) return R.raw._400;
        if (meter > 400 && meter <= 450) return R.raw._450;
        if (meter > 450 && meter <= 500) return R.raw._500;
        if (meter > 500 && meter <= 550) return R.raw._550;
        if (meter > 550 && meter <= 600) return R.raw._600;
        if (meter > 600 && meter <= 650) return R.raw._650;
        if (meter > 650 && meter <= 700) return R.raw._700;
        if (meter > 700 && meter <= 750) return R.raw._750;
        if (meter > 750 && meter <= 800) return R.raw._800;
        if (meter > 800 && meter <= 850) return R.raw._850;
        if (meter > 850 && meter <= 900) return R.raw._900;

        if (meter > 900 && meter <= 1000) return R.raw._1000;
        if (meter > 1000 && meter <= 2000) return R.raw._2000;
        if (meter > 2000 && meter <= 3000) return R.raw._3000;
        if (meter > 3000 && meter <= 4000) return R.raw._4000;
        if (meter > 4000 && meter <= 5000) return R.raw._5000;
        if (meter > 5000 && meter <= 6000) return R.raw._6000;
        if (meter > 6000 && meter <= 7000) return R.raw._7000;
        if (meter > 7000 && meter <= 8000) return R.raw._8000;
        if (meter > 8000 && meter <= 9000) return R.raw._9000;


        return R.raw._1;
    }

    VoiceFlags voiceFlags;
    public void createAndPlayVoiceCommand(GHResponse lastRouteResponce) {

        Instruction instruction = lastRouteResponce.getInstructions().get(1);

        if (voiceFlags==null)
            voiceFlags = new VoiceFlags(instruction);

        voiceFlags.calculate(instruction);


        ArrayList<Integer> voiceList=null;

        if (lastRouteResponce.getDistance() < 10 && !voiceFlags.reachedDestination) {
            voiceList = new ArrayList<Integer>();
            voiceList.add(R.raw.reached_destination);
            voiceFlags.reachedDestination=true;
        }
        else if (lastRouteResponce.getInstructions().get(0).getDistance() > 100 && !voiceFlags.over100m) {
            voiceList = new ArrayList<Integer>();
            voiceList.add(R.raw.go_ahead);
            voiceFlags.over100m=true;
        }
        else if (lastRouteResponce.getInstructions().get(0).getDistance() <= 100 && lastRouteResponce.getInstructions().get(0).getDistance() > 10 && !voiceFlags.over10m) {
            voiceList = new ArrayList<Integer>();
            voiceList.add(R.raw.after);
            voiceList.add(getVoiceId((int) (lastRouteResponce.getInstructions().get(0).getDistance())));
            voiceList.add(R.raw.meters);
            voiceList.add(getVoiceFromSign(lastRouteResponce.getInstructions().get(1).getSign()));
            voiceFlags.over10m=true;
        }
        else if (lastRouteResponce.getInstructions().get(0).getDistance() <= 10 && !voiceFlags.under10m) {
            voiceList = new ArrayList<Integer>();
            voiceList.add(getVoiceFromSign(lastRouteResponce.getInstructions().get(1).getSign()));
            voiceFlags.under10m=true;
        }


        playVoiceList(voiceList);


    }

    public void playVoiceList(final ArrayList<Integer> voiceList){

        if (voiceList==null) return;

        if (voiceList.size() > 0) {
            int voiceId = voiceList.get(0);
            voiceList.remove(0);

            MediaPlayer mediaPlayer = MediaPlayer.create(context, voiceId);
            mediaPlayer.getDuration();


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    playVoiceList(voiceList);
                }
            });
            mediaPlayer.start();

        }

    }

    public void drawPolylineByPoints(PointList pointList) {


        if (layerCount == -1)
            layerCount = mapView.getLayerManager().getLayers().size();
        else {
            while (mapView.getLayerManager().getLayers().size() != layerCount) {
                mapView.getLayerManager().getLayers().remove(mapView.getLayerManager().getLayers().size() - 1);
            }
        }

        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.RED);
//        paintStroke.setDashPathEffect(new float[]
//                {
//                        25, 15
//                });
        paintStroke.setStrokeWidth(20);

        Polyline line = new Polyline((org.mapsforge.core.graphics.Paint) paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = pointList;
        for (int i = 0; i < pointList.getSize(); i++) {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));

        }


        mapView.getLayerManager().getLayers().add(line);
        animateToPoint(pointList.getLat(0), pointList.getLon(0));

    }

    public void calculateAndShowFair(Taximeter taximeter) {
        new AlertDialog.Builder(context)
                .setTitle("محاسبه هزینه")
                .setMessage("مدت زمان سپری شده" + " : " + convertSecToStr(taximeter.timer) + "\n" +
                        " مسافت " + " : " + taximeter.getRouteDistance())
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public double distance(LatLong start,LatLong end){
        int R = 6371; // km
        double x = (end.longitude - start.longitude) * Math.cos((start.latitude + end.latitude) / 2);
        double y = (end.latitude - start.latitude);
        return  Math.sqrt(x * x + y * y) * R;
    }

   /* public Instruction getMinDistanceBetweenPointAndInstructionList(InstructionList instructions,double gpsLat,double gpsLon){

        Instruction minInstruction=null;
        Point minPoint=null;
        double minDistanc=-1;

       for(Instruction instruction : instructions){
           for (int i = 0; i < instruction.getPoints().size(); i++) {
             double lat = instruction.getPoints().getLatitude(i);
             double lon = instruction.getPoints().getLatitude(i);
             double distance = distance(new LatLong(gpsLat,gpsLon),new LatLong(lat,lon));
             if (distance<minDistanc){
                 minDistanc=distance;
                 minInstruction = instruction;
             }

           }
       }
        return minInstruction;
    }*/

    public void startConnectionService(){
        Intent intent = new Intent(this, ConnectionService.class);
        startService(intent);

    }



    // - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - -
    public class TaskReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {

            handelNewRweceivedTask((Task) intent.getSerializableExtra("TASK"));
        }
    }

    // - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - -
    public class ConnectionStatusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
            switch (intent.getIntExtra("status", 0)){
                case 1:

                    btnSrvStatus.setBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    btnSrvStatus.setBackgroundColor(Color.RED);
                    break;
                case 3:
                    btnSrvStatus.setBackgroundColor(Color.YELLOW);
                    break;
            }

        }
    }
    // - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - -

    private void handelNewRweceivedTask(final Task task) {
        new AlertDialog.Builder(this)
                .setTitle("ماموریت جدید دریافت شد")
                .setMessage("fromlat:"+task.getFromLat()+"\n"+"fromlon:"+task.getFromLon()+"\n"+"tolat:"+task.getToLat()+"\n"+"tolon:"+task.getToLat()+"\n"+"description:"+task.getDescription()+"\n"+"\n"+"date:"+task.getDate())
                .setPositiveButton("قبول و مسیریابی", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        shortestPathRunning = true;
                        isInRoutingMode = true;

                        if (voiceFlags!=null)
                            voiceFlags.clear();

                        calcPath(lastKnowLocation.latitude, lastKnowLocation.longitude,task.getToLat(),task.getToLon());
                    }
                })
                .setNegativeButton("رد", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onResume() {

        IntentFilter newTaskReceived;
        newTaskReceived = new IntentFilter(ConnectionService.CONNECTION_SERVICE_INTENT);
        TaskReceiver receiver = new TaskReceiver();
        registerReceiver(receiver, newTaskReceived);



        IntentFilter a;
        a = new IntentFilter(ConnectionService.CONNECTION_STATUS);
        ConnectionStatusReceiver b = new ConnectionStatusReceiver();
        registerReceiver(b, a);


        super.onResume();
    }


}

















