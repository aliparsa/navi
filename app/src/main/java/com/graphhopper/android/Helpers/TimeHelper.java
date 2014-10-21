package com.graphhopper.android.Helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class TimeHelper {

    public static  String convertSecToStr(Long sec) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(tz);
        return df.format(new Date(sec * 1000));

    }
}
