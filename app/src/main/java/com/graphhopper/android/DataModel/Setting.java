


package com.graphhopper.android.DataModel;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



/**
 * Created by aliparsa on 10/11/2014.
 */



public class Setting {
    public final boolean ON = true;
    public final boolean OFF = false;
    public final String TAXIMETER = "TAXIMETER";
    public final String VOICE_INSTRUCTION = "VOICE_INSTRUCTION";
    public final String TEXT_INSTRUCTION = "TEXT_INSTRUCTION";
    public final String SERVER_ADDRESS = "SERVER_ADDRESS";
    public final String SOCKET_ADDRESS = "SOCKET_ADDRESS";


    public static boolean voice;
    public static boolean text;
    public static boolean taximeter;
    public static String serverAddress;
    public static String socketAddress;


    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Setting(Context context) {

        this.context =context;
        loadSetting();

    }

    private void loadSetting() {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!preferences.contains(VOICE_INSTRUCTION)) {
            editor = preferences.edit();
            editor.putBoolean(VOICE_INSTRUCTION, OFF);
            editor.commit();
        } else
            Setting.voice = preferences.getBoolean(VOICE_INSTRUCTION, OFF);


        if (!preferences.contains(TEXT_INSTRUCTION)) {
            editor = preferences.edit();
            editor.putBoolean(TEXT_INSTRUCTION, OFF);
            editor.commit();
        } else
            Setting.text = preferences.getBoolean(TEXT_INSTRUCTION, OFF);


        if (!preferences.contains(TAXIMETER))
        {
            editor = preferences.edit();
            editor.putBoolean(TAXIMETER, OFF);
        editor.commit();
    }else
            Setting.taximeter = preferences.getBoolean(TAXIMETER, OFF);





    }

    public void setVoiceInstruction(boolean status){
        editor = preferences.edit();
        editor.putBoolean(VOICE_INSTRUCTION, status);
        editor.commit();
    }

    public void setTaximeter(boolean status){
        editor = preferences.edit();
        editor.putBoolean(TAXIMETER, status);
        editor.commit();
    }

    public void setTextInstruction(boolean status){
        editor = preferences.edit();
        editor.putBoolean(TEXT_INSTRUCTION, status);
        editor.commit();
    }



    public boolean getVoiceInstructionStatus(){
        return voice;
    }

    public boolean getTextInstructionStatus(){
        return text;
    }

    public boolean getTaximeterStatus(){
        return taximeter;
    }
}

