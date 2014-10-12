/*
package com.graphhopper.android.DataModel;

import android.content.Context;


import java.util.Set;

*/
/**
 * Created by aliparsa on 10/11/2014.
 *//*

public class Setting {
    public final String ON="on";
    public final String OFF="off";
    public final String TAXIMETER="taximeter";
    public final String VOICE="voice";

    public static String voice;
    public static String taximeter;
    private final SharedPreference sharedPreference;
    Context context;

    public Setting(Context context){

        context=this.context;
        sharedPreference= new SharedPreference(context);
        loadSetting();
    }

    private void loadSetting(){

        if (!sharedPreference.contains(VOICE))
            sharedPreference.putString(VOICE,OFF);
        else
            Setting.voice=sharedPreference.getString(VOICE,OFF);


        if (!sharedPreference.contains(TAXIMETER))
            sharedPreference.putString(TAXIMETER,OFF);
        else
            Setting.taximeter=sharedPreference.getString(TAXIMETER,OFF);
    }

    public void setVoiceRoutingOn(){
        sharedPreference.putString(VOICE,ON);
    }

    public void setVoiceRoutingOff(){
        sharedPreference.putString(VOICE,OFF);
    }

    public void setTaximeterOn(){
        sharedPreference.putString(TAXIMETER,ON);
    }

    public void setTaximeterOff(){
        sharedPreference.putString(TAXIMETER,OFF);
    }

    public String getVoiceRoutingStatus(){
        return sharedPreference.getString(VOICE,OFF);
    }

    public String getTaximeterStatus(){
        return sharedPreference.getString(TAXIMETER,OFF);
    }

}
*/
