


package com.graphhopper.android.DataModel;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



/**
 * Created by aliparsa on 10/11/2014.
 */



public class Setting {
    public final String ON = "ON";
    public final String OFF = "OFF";
    public final String TAXIMETER = "TAXIMETER";
    public final String VOICE_INSTRUCTION = "VOICE_INSTRUCTION";
    public final String TEXT_INSTRUCTION = "TEXT_INSTRUCTION";

    public static String voice;
    public static String taximeter;
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Setting(Context context) {

        this.context =context;
        loadSetting();

    }

    private void loadSetting() {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();


        if (!preferences.contains(VOICE_INSTRUCTION))
            editor.putString(VOICE_INSTRUCTION, OFF);
        else
            Setting.voice = preferences.getString(VOICE_INSTRUCTION, OFF);


        if (!preferences.contains(TEXT_INSTRUCTION))
            editor.putString(TEXT_INSTRUCTION, OFF);
        else
            Setting.voice = preferences.getString(TEXT_INSTRUCTION, OFF);



        if (!preferences.contains(TAXIMETER))
            editor.putString(TAXIMETER, OFF);
        else
            Setting.taximeter = preferences.getString(TAXIMETER, OFF);




    }

    public void setVoiceInstructionOn(){
        editor.putString(VOICE_INSTRUCTION,ON);
    }

    public void setVoiceInstructionOff(){
        editor.putString(VOICE_INSTRUCTION,OFF);
    }

    public void setTaximeterOn(){
        editor.putString(TAXIMETER,ON);
    }

    public void setTaximeterOff(){
        editor.putString(TAXIMETER,OFF);
    }

    public void setTextInstructionOn(){
        editor.putString(TEXT_INSTRUCTION,ON);
    }

    public void setTextInstructionOff(){
        editor.putString(TEXT_INSTRUCTION,OFF);
    }

}

