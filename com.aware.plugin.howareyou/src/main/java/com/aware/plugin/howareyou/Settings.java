package com.aware.plugin.howareyou;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aware.Aware;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Plugin settings in XML @xml/preferences
    public static final String STATUS_PLUGIN_HOWAREYOU = "status_plugin_howareyou";
    public static final String STATUS_PHOTO = "status_photo";

    //Plugin settings UI elements
    private static CheckBoxPreference status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AWARE FILIP" , "onResume");

        status = (CheckBoxPreference) findPreference(STATUS_PLUGIN_HOWAREYOU);
        if( Aware.getSetting(this, STATUS_PLUGIN_HOWAREYOU).length() == 0 ) {
            Aware.setSetting( this, STATUS_PLUGIN_HOWAREYOU, true ); //by default, the setting is true on install
        }
        status.setChecked(Aware.getSetting(getApplicationContext(), STATUS_PLUGIN_HOWAREYOU).equals("true"));

        status = (CheckBoxPreference) findPreference(STATUS_PHOTO);
        if( Aware.getSetting(this, STATUS_PHOTO).length() == 0 ) {
            Aware.setSetting( this, STATUS_PHOTO, true ); //by default, the setting is true on install
        }
        status.setChecked(Aware.getSetting(getApplicationContext(), STATUS_PHOTO).equals("true"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference setting = findPreference(key);

        if( setting.getKey().equals(STATUS_PLUGIN_HOWAREYOU) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
            status.setChecked(sharedPreferences.getBoolean(key, false));
        }
        if (Aware.getSetting(this, STATUS_PLUGIN_HOWAREYOU).equals("true")) {
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        } else {
            Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        }

        if( setting.getKey().equals(STATUS_PHOTO) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
            status.setChecked(sharedPreferences.getBoolean(key, false));
        }
        if (Aware.getSetting(this, STATUS_PHOTO).equals("true")) {
            //Aware.startPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
            Log.d("AWARE FILIP" , "startPhoto");

        } else {
            //Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
            Log.d("AWARE FILIP" , "stopPhoto");
        }
    }
}
