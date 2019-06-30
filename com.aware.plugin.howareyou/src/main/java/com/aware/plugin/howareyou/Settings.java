package com.aware.plugin.howareyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.howareyou.photo.PhotoNotificationDisplayService;
import com.aware.plugin.howareyou.plugin.DebugDialog;
import com.aware.plugin.howareyou.plugin.LogsUtil;

import java.util.Date;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //TODO hardcode true for basic model
    private static final boolean USE_BASIC_MODEL = false;

    //Plugin settings in XML @xml/preferences
    public static final String SETTINGS_PLUGIN_HOWAREYOU = "settings_plugin_howareyou";
    public static final String SETTINGS_PHOTO = "settings_photo";
    public static final String SETTINGS_QUESTION_EMOJI = "settings_question_emoji";
    public static final String SETTINGS_QUESTION_COLOR = "settings_question_color";
    public static final String SETTINGS_USE_BASIC_MODEL = "settings_use_basic_model";
    public static final String SETTINGS_PHOTO_NOTIFICATION = "settings_photo_notification";
    public static final String SETTINGS_DEBUG_MODE = "settings_debug_mode";
    public static final String SETTINGS_SYNC_WIFI_ONLY = "settings_sync_wifi_only";


    //Pro tip: Don't forget to add also to the preferences.xml! Also insert to SETTINGS_ARRAY below!

    public static final String[] SETTINGS_ARRAY = new String[]{
            SETTINGS_PLUGIN_HOWAREYOU,
            SETTINGS_PHOTO,
            SETTINGS_QUESTION_EMOJI,
            SETTINGS_QUESTION_COLOR,
            //SETTINGS_USE_BASIC_MODEL  <- Commented out as not used by reasoning engine
            SETTINGS_PHOTO_NOTIFICATION
            //SETTINGS_DEBUG_MODE       <- Commented out as not used by reasoning engine
            //SETTINGS_SYNC_WIFI_ONLY   <- Commented out as not used by reasoning engine
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
        resumeCheckBoxPreference(SETTINGS_PLUGIN_HOWAREYOU,   true);
        resumeCheckBoxPreference(SETTINGS_PHOTO,              true);
        resumeCheckBoxPreference(SETTINGS_QUESTION_EMOJI,     true);
        resumeCheckBoxPreference(SETTINGS_QUESTION_COLOR,     true);
        resumeCheckBoxPreference(SETTINGS_USE_BASIC_MODEL,    true);
        resumeCheckBoxPreference(SETTINGS_PHOTO_NOTIFICATION, false);
        resumeCheckBoxPreference(SETTINGS_SYNC_WIFI_ONLY    , true);
        resumeCheckBoxPreference(SETTINGS_DEBUG_MODE,         false);
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
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, true));
        }
        if (Aware.getSetting(this, SETTINGS_PLUGIN_HOWAREYOU).equals("true")) {
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        } else {
            Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.howareyou");
        }

        if( setting.getKey().equals(SETTINGS_PHOTO) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, true));

            //Launch photo notification if necessary
            Intent serviceIntent = new Intent(this, PhotoNotificationDisplayService.class);
            startService(serviceIntent);
        }

        if( setting.getKey().equals(SETTINGS_QUESTION_EMOJI) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, true));
        }

        if( setting.getKey().equals(SETTINGS_QUESTION_COLOR) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, true));
        }

        if( setting.getKey().equals(SETTINGS_USE_BASIC_MODEL) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, USE_BASIC_MODEL));
        }

        if( setting.getKey().equals(SETTINGS_PHOTO_NOTIFICATION) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));

            //Launch photo notification if necessary
            Intent serviceIntent = new Intent(this, PhotoNotificationDisplayService.class);
            startService(serviceIntent);
        }

        if( setting.getKey().equals(SETTINGS_DEBUG_MODE) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
        }

        if( setting.getKey().equals(SETTINGS_SYNC_WIFI_ONLY) ) {
            Aware.setSetting(this, Aware_Preferences.WEBSERVICE_WIFI_ONLY,
                    sharedPreferences.getBoolean(key, true));
        }
    }

    private void registerButtonListeners() {
        registerButtonListener(R.string.settings_force_photo_emotion_recognition, PluginActions.ACTION_START_PHOTO_EMOTION_RECOGNITION);
        registerButtonListener(R.string.settings_force_question_color,            PluginActions.ACTION_START_QUESTION_COLOR);
        registerButtonListener(R.string.settings_force_question_emoji,            PluginActions.ACTION_START_QUESTION_EMOJI);
        registerButtonListener(R.string.settings_force_sync,                      Aware.ACTION_AWARE_SYNC_DATA);
        registerButtonListener(R.string.settings_force_reasoning_log,             new HowareyouForceReasoningLogButtonListener());
        registerButtonListener(R.string.settings_force_application_log,           new HowareyouForceApplicationLogButtonListener());
        registerButtonListener(R.string.settings_force_action_log,                new HowareyouForceActionLogButtonListener());
    }

    private void registerButtonListener(int resId, String action) {
        Preference button = findPreference(getString(resId));
        Intent intent = new Intent(action);
        button.setOnPreferenceClickListener(new HowareyouOnPreferenceClickListener(intent));
    }

    private void registerButtonListener(int resId, Preference.OnPreferenceClickListener listener) {
        Preference button = findPreference(getString(resId));
        button.setOnPreferenceClickListener(listener);
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

    class HowareyouForceReasoningLogButtonListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            StringBuilder stringBuilder = LogsUtil.readReasoningLogs();
            Intent intent = new Intent(Settings.this, DebugDialog.class);
            String logContents = stringBuilder.toString();
            String message = "Latest heartdroid reasoning log:\n\n" + (logContents.length()>0 ? logContents : "<No entries>");
            intent.putExtra("MESSAGE_CONTENT", message);
            intent.putExtra("RUN_ALWAYS", true);
            startActivity(intent);
            return true;
        }
    }

    class HowareyouForceApplicationLogButtonListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            StringBuilder stringBuilder = LogsUtil.readApplicationLogs();
            Intent intent = new Intent(Settings.this, DebugDialog.class);
            String logContents = stringBuilder.toString();
            String message = "Latest application log:\n\n" + (logContents.length()>0 ? logContents : "<No entries>");
            intent.putExtra("MESSAGE_CONTENT", message);
            intent.putExtra("RUN_ALWAYS", true);
            startActivity(intent);
            return true;
        }
    }

    class HowareyouForceActionLogButtonListener implements Preference.OnPreferenceClickListener {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            String message = "Latest photo emotion recognition: \n";
            message += getFromDb(com.aware.plugin.howareyou.Provider.Table_Photo_Data.CONTENT_URI,
                    com.aware.plugin.howareyou.Provider.Table_Photo_Data.TIMESTAMP);

            message += "\n\nLatest question about emotions: \n";
            message += getFromDb(com.aware.plugin.howareyou.Provider.Table_Emotion_Data.CONTENT_URI,
                    com.aware.plugin.howareyou.Provider.Table_Emotion_Data.TIMESTAMP);

            message += "\n\nLatest question about colors: \n";
            message += getFromDb(com.aware.plugin.howareyou.Provider.Table_Color_Data.CONTENT_URI,
                    com.aware.plugin.howareyou.Provider.Table_Color_Data.TIMESTAMP);

            String logContents = PluginManager.getActivityLog();
            message += "\n\n---\n\nLatest HowAreYou actions:\n\n" + (logContents.length()>0 ? logContents : "<No entries>");

            Intent intent = new Intent(Settings.this, DebugDialog.class);
            intent.putExtra("MESSAGE_CONTENT", message);
            intent.putExtra("RUN_ALWAYS", true);
            startActivity(intent);
            return true;
        }

        @NonNull
        protected String getFromDb(Uri uri, String column) {
            String message;
            Cursor cursor = getContentResolver().query(uri, new String[]{column}, null, null, "timestamp DESC");
            if (cursor.getCount() == 0) {
                message = "none";
            } else {
                cursor.moveToFirst();
                long timestamp = cursor.getLong(cursor.getColumnIndex(column));
                Date date = (new Date(timestamp));
                message = date.toString();
            }
            return message;
        }
    }
}
