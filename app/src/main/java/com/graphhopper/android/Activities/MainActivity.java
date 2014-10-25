package com.graphhopper.android.Activities;

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
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
import com.graphhopper.android.DataModel.Message;
import com.graphhopper.android.DataModel.Task;
import com.graphhopper.android.DataModel.Taximeter;
import com.graphhopper.android.DataModel.VoiceFlags;
import com.graphhopper.android.GHAsyncTask;
import com.graphhopper.android.Helpers.AndroidHelper;
import com.graphhopper.android.Helpers.BattryHelper;
import com.graphhopper.android.Helpers.DatabaseHelper;

import com.graphhopper.android.Helpers.DrawOnMapHelper;
import com.graphhopper.android.Helpers.GpsHelper;
import com.graphhopper.android.Helpers.MapViewHelper;
import com.graphhopper.android.Helpers.ServiceHelper;
import com.graphhopper.android.Helpers.TaximeterHelper;
import com.graphhopper.android.Helpers.TextInstructionsHelper;
import com.graphhopper.android.Helpers.TimeHelper;
import com.graphhopper.android.Helpers.TimerHelper;
import com.graphhopper.android.Helpers.VoiceInstructionsHelper;
import com.graphhopper.android.Listeners.GpsStatusListener;
import com.graphhopper.android.Listeners.GpsStatusListener2;
import com.graphhopper.android.Listeners.SensorListener;
import com.graphhopper.android.R;
import com.graphhopper.android.Services.ConnectionService;
import com.graphhopper.android.utilities.PersianCalender;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Downloader;
import com.graphhopper.util.Helper;
import com.graphhopper.util.ProgressListener;
import com.graphhopper.util.StopWatch;

import org.json.JSONObject;
import org.mapsforge.core.graphics.Bitmap;
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
import org.mapsforge.map.layer.renderer.TileRendererLayer;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends Activity {
    private static final int NEW_MENU_ID = Menu.FIRST + 1;
    public static Context context;
    public boolean isInRoutingMode = false;
    public boolean newLocationEventInProgress;
    public Location loc_lastKnowLocation;
    LocationManager locationManager;
    ImageView img2;
    //ImageView img4;
    TileRendererLayer tileRendererLayer;
    GHResponse lastRouteResponce;
    SensorManager mSensorManager;
    Sensor mCompass;
    TextView log;
    Button btn_taximeter1;
    Button btn_taximeter2;
    Button btn_taximeter3;
    Button btn_taximeter4;
    Taximeter taximeter1;
    Taximeter taximeter2;
    Taximeter taximeter3;
    Taximeter taximeter4;
    LinearLayout llDirectionList;
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
    VoiceFlags voiceFlags;
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


    //Setting setting;
    private String fileListURL = "https://graphhopper.com/public/maps/0.3/";
    private String prefixURL = fileListURL;
    private String downloadURL;
    private File mapsFolder;
    private TileCache tileCache;
    // my fields
    private LatLong lastKnowLocation;
    private boolean lockOnGps = false;
    private GpsStatusListener gpsStatusListener;
    private LatLong lastGPSLocation;
    private LocationListener listener = new LocationListener() {


        @Override
        public void onLocationChanged(Location location) {

            loc_lastKnowLocation = location;

            updateGpsStateIcon(GpsState.connected);

            updateSpeedValue(location);

            updateDistanceValue(location);


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
                    MapViewHelper.animateToPoint(mapView, location.getLatitude(), location.getLongitude());

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
                    MapViewHelper.clearMapView(mapView);
                    DrawOnMapHelper.showImgOnThisPoint(context, lastKnowLocation, mapView, R.drawable.blue_circle);
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
    private float lastAzimuth = 0;
    private int oldDayOfYear = -1;
    private Location oldLocation;
    private double todayDistance = 0;
    private int layerCount = -1;
    private double LastRouteLat;
    private double LastRouteLon;
    private boolean DuringShowHistory = false;

    protected boolean onMapTap(LatLong tapLatLong, Point layerXY, Point tapXY) {

//        if (isInRoutingMode) {
//            Location location = new Location("ali");
//            location.setLatitude(tapLatLong.latitude);
//            location.setLongitude(tapLatLong.longitude);
//            listener.onLocationChanged(location);
//        }

        if (!isReady())
            return false;

        if (shortestPathRunning) {
            logUser("Calculation still in progress");
            return false;
        }
        Layers layers = mapView.getLayerManager().getLayers();


        showSelectorDialog(tapLatLong);



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

    private void showSelectorDialog(final LatLong tapLatLong) {


        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        CharSequence items[] = new CharSequence[]{"ذخیره", "مسیریابی به اینجا"};
        adb.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 1) {
                            dialogInterface.dismiss();
                            start = lastKnowLocation;

                            if (start != null) {

                                end = tapLatLong;
                                shortestPathRunning = true;
                                isInRoutingMode = true;

                                if (voiceFlags != null)
                                    voiceFlags.clear();

                                calcPath(start.latitude, start.longitude, end.latitude,
                                        end.longitude);


                            }
                        }

                        if (i == 0) {
                            dialogInterface.dismiss();


                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("نام این نقطه ؟");

// Set up the input
                            final EditText input = new EditText(context);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);

// Set up the buttons
                            builder.setPositiveButton("ذخیره", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseHelper db = new DatabaseHelper(context);
                                    try {
                                        db.insertPrivateLocation(tapLatLong, "2014-10-10", input.getText().toString());
                                        showToast("با موفقیت ذخیره شد");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        showToast("هنگان ذخیره سازی خطایی  رخ داد");
                                    }


                                }
                            });
                            builder.setNegativeButton("لغو", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();

                        }

                    }
                }
        );
        adb.setTitle("کدام ؟");
        adb.show();
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
                lockOnGps = false;
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
                    TaximeterHelper.calculateAndShowFair(context, taximeter1);


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


                    TaximeterHelper.calculateAndShowFair(context, taximeter2);


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

                    TaximeterHelper.calculateAndShowFair(context, taximeter3);


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


                    TaximeterHelper.calculateAndShowFair(context, taximeter4);
                    taximeter4 = null;
                    btn_taximeter4.setBackgroundResource(R.drawable.taximeter);
                    btn_taximeter4.setText("");


                }

            }
        });

        log = (TextView) findViewById(R.id.log);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(new SensorListener(), mCompass, SensorManager.SENSOR_DELAY_NORMAL);


        // satlateInView = (TextView) findViewById(R.id.satlate_in_view);

        // img4 = (ImageView) findViewById(R.id.imageView6);

        btnPinOnGps = (Button) findViewById(R.id.btnPinOnGps);
        btnPinOnGps.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastKnowLocation != null) {
                    lockOnGps = true;
                    MapViewHelper.animateToPoint(mapView, lastKnowLocation.latitude, lastKnowLocation.longitude);

                }
            }
        });


        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 1, listener);

        locationManager.addGpsStatusListener(new GpsStatusListener2());

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
                            btn_taximeter1.setText(TimeHelper.convertSecToStr(taximeter1.timer));
                            btn_taximeter1.append("\n" + (int) taximeter1.getRouteDistance() + "M");
                        }

                        if (taximeter2 != null) {
                            taximeter2.timer++;
                            btn_taximeter2.setText(TimeHelper.convertSecToStr(taximeter2.timer));
                            btn_taximeter2.append("\n" + (int) taximeter2.getRouteDistance() + "M");

                        }
                        if (taximeter3 != null) {
                            taximeter3.timer++;
                            btn_taximeter3.setText(TimeHelper.convertSecToStr(taximeter3.timer));
                            btn_taximeter3.append("\n" + (int) taximeter3.getRouteDistance() + "M");

                        }
                        if (taximeter4 != null) {
                            taximeter4.timer++;
                            btn_taximeter4.setText(TimeHelper.convertSecToStr(taximeter4.timer));
                            btn_taximeter4.append("\n" + (int) taximeter4.getRouteDistance() + "M");

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

        ServiceHelper.startConnectionService(this);

        ServiceHelper.startLocationFinderService(this);

        ServiceHelper.startLocationSenderService(this);

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
                showToast("موقعیت و درخواست شما به مرکز ارسال شد");
            }
        });

        showToast("Battery Level " + BattryHelper.getBatteryLevel(context) + "");

        final Button btnLocationHistory = (Button) findViewById(R.id.btnLocationHistory);
        btnLocationHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                Time now = new Time();
                now.setToNow();

                final ArrayList<String> EnDays = new ArrayList<String>();
                ArrayList<String> FaDays = new ArrayList<String>();

                for (int i = 0; i < 7; i++) {
                    Date d = new Date(System.currentTimeMillis() - (86400000) * i);
                    SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
                    SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
                    SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
                    PersianCalender persianCalender = new PersianCalender(Integer.parseInt(sdfYear.format(d)), Integer.parseInt(sdfMonth.format(d)), Integer.parseInt(sdfDay.format(d)));

                    FaDays.add(persianCalender.getIranianDate());
                    EnDays.add(persianCalender.getGregorianYear() + "-" + persianCalender.getGregorianMonth() + "-" + persianCalender.getGregorianDay());
                }

                String[] arrDays = FaDays.toArray(new String[FaDays.size()]);

                new AlertDialog.Builder(context)
                        .setSingleChoiceItems(arrDays, 0, null)
                        .setCancelable(false)
                        .setTitle("تاریخ را انتخاب نمایید")
                        .setPositiveButton("نمایش", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                // Do something useful withe the position of the selected radio button
                                lockOnGps = false;
                                DuringShowHistory = true;
                                DatabaseHelper db = new DatabaseHelper(context);
                                ArrayList<Location> locations = db.getLocationsOfDay(EnDays.get(selectedPosition));
                                if (locations.size() > 1) {
                                    MapViewHelper.clearMapView(mapView);
                                    DrawOnMapHelper.drawPolyLineByLocations(mapView, locations);
                                } else {
                                    showToast("موردی برای نمایش موجود نیست");
                                    DuringShowHistory = false;
                                }
                            }
                        })
                        .setNegativeButton("خروج", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });


        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctxt, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                btnBtrStatus.setText("باتری" + "\n" + String.valueOf(level) + "%");
                if (level <= 10) btnBtrStatus.setBackgroundColor(Color.RED);
                if (level > 10 && level <= 40) btnBtrStatus.setBackgroundColor(Color.YELLOW);
                if (level > 40 && level <= 70) btnBtrStatus.setBackgroundColor(Color.BLUE);
                if (level > 70) btnBtrStatus.setBackgroundColor(Color.GREEN);
            }
        };

        this.registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


       // ali code end here


    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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
        DrawOnMapHelper.showImgOnThisPoint(context, lastKnowLocation, mapView, R.drawable.blue_circle);


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

                    TextInstructionsHelper.createAndShowInstructionList(context, lastRouteResponce, llDirectionList, mapView, layerCount);

                    VoiceInstructionsHelper.createAndPlayVoiceCommand(context, lastRouteResponce, voiceFlags);


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


                    MapViewHelper.clearMapView(mapView);

                    DrawOnMapHelper.showImgOnThisPoint(context, lastKnowLocation, mapView, R.drawable.blue_circle);

                    // draw routing
                    mapView.getLayerManager().getLayers().add(DrawOnMapHelper.createPolyline(resp));


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

    private void log(String str) {
        Log.i("GH", str);
    }

    private void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    private void logUser(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, NEW_MENU_ID, 0, "پیشگامان آسیا");
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

    private void updateDistanceValue(Location location) {

        Calendar c = Calendar.getInstance();
        int newDayOfYear = c.get(Calendar.DAY_OF_YEAR);


        if (oldDayOfYear == -1 || oldLocation == null || oldDayOfYear != newDayOfYear) {
            oldLocation = location;
            oldDayOfYear = newDayOfYear;
            todayDistance = 0;
            return;
        } else {
            double betweenDistance = GpsHelper.distance(oldLocation, location);
            todayDistance += betweenDistance;
            Button speed = (Button) findViewById(R.id.btnDistance);
            speed.setText("مصافت پیموده شده امروز" + "\n" + (int) todayDistance + " m");
            oldDayOfYear = newDayOfYear;
            oldLocation = location;
        }


    }

    private void updateSpeedValue(Location location) {
        Button speed = (Button) findViewById(R.id.btnSpeed);
        speed.setText("سرعت" + "\n" + (int) location.getSpeed() + " Km");
    }

    private void updateGpsStateIcon(GpsState gpsState) {
        if (gpsState == GpsState.connected) {
            btnGpsStatus.setBackgroundColor(Color.GREEN);
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
                        MapViewHelper.clearMapView(mapView);
                        DrawOnMapHelper.showImgOnThisPoint(context, lastKnowLocation, mapView, R.drawable.blue_circle);
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


    public static enum GpsState {connected, disconnected}


    public interface MySpinnerListener {
        void onSelect(String selectedArea, String selectedFile);
    }

    // - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - -


    // - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - -

    public class TaskReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
            final Task task = ((Task) intent.getSerializableExtra("TASK"));
            new AlertDialog.Builder(context)
                    .setTitle("ماموریت جدید دریافت شد")
                    .setMessage("fromlat:" + task.getFromLat() + "\n" + "fromlon:" + task.getFromLon() + "\n" + "tolat:" + task.getToLat() + "\n" + "tolon:" + task.getToLat() + "\n" + "description:" + task.getDescription() + "\n" + "\n" + "date:" + task.getDate())
                    .setPositiveButton("قبول و مسیریابی", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            shortestPathRunning = true;
                            isInRoutingMode = true;

                            if (voiceFlags != null)
                                voiceFlags.clear();

                            calcPath(lastKnowLocation.latitude, lastKnowLocation.longitude, task.getToLat(), task.getToLon());
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
    }

    // - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - -
    public class ConnectionStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
            switch (intent.getIntExtra("status", 0)) {
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
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
        {
            try {
                JSONObject obj = new JSONObject(intent.getStringExtra("MESSAGE"));

                final Message message = Message.getObjectFromJSON(obj);
                if (message.getX1().equals("task")){
                    
                    new AlertDialog.Builder(context)
                            .setTitle("ماموریت جدید دریافت شد")
                            .setMessage("fromlat:" + message.getX2() + "\n" + "fromlon:" + message.getX3() + "\n" + "tolat:" + message.getX4() + "\n" + "tolon:" + message.getX5() + "\n" + "description:" + message.getX6() + "\n" + "\n" + "date:" + message.getX7())
                            .setPositiveButton("قبول و مسیریابی", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                    shortestPathRunning = true;
                                    isInRoutingMode = true;

                                    if (voiceFlags != null)
                                        voiceFlags.clear();

                                    calcPath(lastKnowLocation.latitude, lastKnowLocation.longitude, Double.parseDouble(message.getX1()),Double.parseDouble( message.getX1()));
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
            } catch (Exception e) {

            }
        }
    }


}
