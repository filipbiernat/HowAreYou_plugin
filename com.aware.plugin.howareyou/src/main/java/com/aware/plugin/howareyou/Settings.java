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
    public static final String SETTINGS_PLUGIN_HOWAREYOU = "settings_plugin_howareyou";
    public static final String SETTINGS_PHOTO = "settings_photo";
    public static final String SETTINGS_QUESTION_EMOJI = "settings_question_emoji";
    public static final String SETTINGS_QUESTION_COLOR = "settings_question_color";
    public static final String SETTINGS_PHOTO_NOTIFICATION = "settings_photo_notification";
    //Pro tip: Don't forget to add also to the preferences.xml! Also insert to SETTINGS_ARRAY below!

    public static final String[] SETTINGS_ARRAY = new String[]{
            SETTINGS_PLUGIN_HOWAREYOU,
            SETTINGS_PHOTO,
            SETTINGS_QUESTION_EMOJI,
            SETTINGS_QUESTION_COLOR,
            SETTINGS_PHOTO_NOTIFICATION
    };

    //Plugin settings UI elements
    private static CheckBoxPreference status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        registerButtonListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeCheckBoxPreference(SETTINGS_PLUGIN_HOWAREYOU, true);
        resumeCheckBoxPreference(SETTINGS_PHOTO, true);
        resumeCheckBoxPreference(SETTINGS_QUESTION_EMOJI, true);
        resumeCheckBoxPreference(SETTINGS_QUESTION_COLOR, true);
        resumeCheckBoxPreference(SETTINGS_PHOTO_NOTIFICATION, false);
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

        if( setting.getKey().equals(SETTINGS_PLUGIN_HOWAREYOU) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
        }
        if (Aware.getSetting(this, SETTINGS_PLUGIN_HOWAREYOU).equals("true")) {
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        } else {
            Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        }

        if( setting.getKey().equals(SETTINGS_PHOTO) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));

            //Launch photo notification if necessary
            Intent serviceIntent = new Intent(this, PhotoNotificationDisplayService.class);
            startService(serviceIntent);
        }

        if( setting.getKey().equals(SETTINGS_QUESTION_EMOJI) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
        }

        if( setting.getKey().equals(SETTINGS_QUESTION_COLOR) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
        }

        if( setting.getKey().equals(SETTINGS_PHOTO_NOTIFICATION) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));

            //Launch photo notification if necessary
            Intent serviceIntent = new Intent(this, PhotoNotificationDisplayService.class);
            startService(serviceIntent);
        }
    }

    private void registerButtonListeners() {
        registerButtonListener(R.string.settings_force_photo_emotion_recognition, PluginActions.ACTION_START_PHOTO_EMOTION_RECOGNITION);
        registerButtonListener(R.string.settings_force_question_color,            PluginActions.ACTION_START_QUESTION_COLOR);
        registerButtonListener(R.string.settings_force_question_emoji,            PluginActions.ACTION_START_QUESTION_EMOJI);
        registerButtonListener(R.string.settings_force_sync,                      Aware.ACTION_AWARE_SYNC_DATA);
    }

    private void registerButtonListener(int resId, String action) {
        Preference button = findPreference(getString(resId));
        Intent intent = new Intent(action);
        button.setOnPreferenceClickListener(new HowareyouOnPreferenceClickListener(intent));
    }

    class HowareyouOnPreferenceClickListener implements Preference.OnPreferenceClickListener {
        private Intent intent;

        public HowareyouOnPreferenceClickListener(Intent intent) {
            this.intent = intent;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            sendBroadcast(intent);
            return true;
        }
    }
}
