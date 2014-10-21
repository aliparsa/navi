/*


package com.graphhopper.android.DataModel;


import android.content.Context;
import android.content.SharedPreferences;


*/
/**
 * Created by aliparsa on 10/11/2014.
 *//*



public class Setting {
    public final String ON="ON";
    public final String OFF="OFF";
    public final String TAXIMETER="TAXIMETER";
    public final String VOICE_INSTRUCTION="VOICE_INSTRUCTION";
    public final String TEXT_INSTRUCTION="TEXT_INSTRUCTION";

    public static String voice;
    public static String taximeter;
    SharedPreferences sharedPreferences;
    private final Sha sharedPreference;
    Context context;

    public Setting(Context context){

        context=this.context;
        sharedPreference= new SharedPreference(context);
        loadSetting();
    }

    private void loadSetting(){


        if (!sharedPreference.contains(VOICE_INSTRUCTION))
            sharedPreference.putString(VOICE_INSTRUCTION,OFF);
        else
            Setting.voice=sharedPreference.getString(VOICE_INSTRUCTION,OFF);



        if (!sharedPreference.contains(TAXIMETER))
            sharedPreference.putString(TAXIMETER,OFF);
        else
            Setting.taximeter=sharedPreference.getString(TAXIMETER,OFF);


    }

    public void setVoiceRoutingOn(){
        sharedPreference.putString(VOICE_INSTRUCTION,ON);
    }

    public void setVoiceRoutingOff(){
        sharedPreference.putString(VOICE_INSTRUCTION,OFF);
    }

    public void setTaximeterOn(){
        sharedPreference.putString(TAXIMETER,ON);
    }

    public void setTaximeterOff(){
        sharedPreference.putString(TAXIMETER,OFF);
    }

    public String getVoiceRoutingStatus(){
        return sharedPreference.getString(VOICE_INSTRUCTION,OFF);
    }

    public String getTaximeterStatus(){
        return sharedPreference.getString(TAXIMETER,OFF);
    }

}

*/
