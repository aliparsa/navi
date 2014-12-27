package com.graphhopper.android.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.graphhopper.android.DataModel.Taximeter;

/**
 * Created by aliparsa on 10/21/2014.
 */
public class TaximeterHelper {

    public static void calculateAndShowFair(Context context,Taximeter taximeter) {
        new AlertDialog.Builder(context)
                .setTitle("محاسبه هزینه")
                .setMessage("مدت زمان سپری شده" + " : " + TimeHelper.convertSecToStr(taximeter.timer) + "\n" +
                        " مسافت " + " : " + taximeter.getRouteDistance())
                .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton("لغو", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
