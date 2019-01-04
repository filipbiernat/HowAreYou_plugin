package com.aware.plugin.howareyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.aware.Aware;
import com.aware.plugin.howareyou.photo.PhotoNotificationDisplayService;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Plugin settings in XML @xml/preferences
    public static final String STATUS_PLUGIN_HOWAREYOU = "status_plugin_howareyou";
    public static final String STATUS_PHOTO = "status_photo";
    public static final String STATUS_QUESTION_EMOJI = "status_question_emoji";
    public static final String STATUS_QUESTION_COLOR = "status_question_color";
    public static final String STATUS_PHOTO_NOTIFICATION = "status_photo_notification";
    //Pro tip: Don't forget to add also to the preferences.xml!

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
        resumeCheckBoxPreference(STATUS_PLUGIN_HOWAREYOU, true);
        resumeCheckBoxPreference(STATUS_PHOTO, true);
        resumeCheckBoxPreference(STATUS_QUESTION_EMOJI, true);
        resumeCheckBoxPreference(STATUS_QUESTION_COLOR, true);
        resumeCheckBoxPreference(STATUS_PHOTO_NOTIFICATION, true);
    }

    private void resumeCheckBoxPreference(String preference, boolean defValue) {
        status = (CheckBoxPreference) findPreference(preference);
        if( Aware.getSetting(this, preference).length() == 0 ) {
            Aware.setSetting( this, preference, defValue );
        }
        status.setChecked(Aware.getSetting(getApplicationContext(), preference).equals("true"));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference setting = findPreference(key);

        if( setting.getKey().equals(STATUS_PLUGIN_HOWAREYOU) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
        }
        if (Aware.getSetting(this, STATUS_PLUGIN_HOWAREYOU).equals("true")) {
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        } else {
            Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        }

        if( setting.getKey().equals(STATUS_PHOTO) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));

            //Launch photo notification if necessary
            Intent serviceIntent = new Intent(this, PhotoNotificationDisplayService.class);
            startService(serviceIntent);
        }

        if( setting.getKey().equals(STATUS_QUESTION_EMOJI) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
        }

        if( setting.getKey().equals(STATUS_QUESTION_COLOR) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
        }

        if( setting.getKey().equals(STATUS_PHOTO_NOTIFICATION) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));

            //Launch photo notification if necessary
            Intent serviceIntent = new Intent(this, PhotoNotificationDisplayService.class);
            startService(serviceIntent);
        }
    }
}
