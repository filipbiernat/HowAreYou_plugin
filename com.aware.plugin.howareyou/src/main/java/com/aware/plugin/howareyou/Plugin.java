package com.aware.plugin.howareyou;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.howareyou.photo.PhotoNotificationDisplayService;
import com.aware.plugin.howareyou.plugin.DbInitializer;
import com.aware.plugin.howareyou.plugin.HeaRTAwareObserverManager;
import com.aware.plugin.howareyou.plugin.SensorsManager;
import com.aware.utils.Aware_Plugin;

public class Plugin extends Aware_Plugin {

    public static final String TAG = "AWARE::HowAreYou";

    private HeaRTAwareObserverManager observerManager = new HeaRTAwareObserverManager();
    private SensorsManager sensorsManager = new SensorsManager();
    private DbInitializer dbInitializer = new DbInitializer();

    @Override
    public void onCreate() {
        super.onCreate();

        //This allows plugin data to be synced on demand from broadcast Aware#ACTION_AWARE_SYNC_DATA
        AUTHORITY = Provider.getAuthority(this);

        /**
         * Plugins share their current status, i.e., context using this method.
         * This method is called automatically when triggering
         * {@link Aware#ACTION_AWARE_CURRENT_CONTEXT}
         **/
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                //Broadcast your context here
            }
        };

        //Add permissions you need (Android M+).
        //By default, AWARE asks access to the #Manifest.permission.WRITE_EXTERNAL_STORAGE
        boolean permissionsOk = sensorsManager.addPermissions(this);

        if (permissionsOk) {
            //heart-aware: If database empty, add one dummy record as a starting point for reasoning.
            dbInitializer.initialize(this);
            //heart-aware: Create and register observers
            observerManager.create(getApplicationContext());

            //Make picture - uncomment to debug
            //final Intent intent = new Intent();
            //intent.setAction(PluginActions.ACTION_START_PHOTO_EMOTION_RECOGNITION);
            //Context appContext = HowAreYouApp.getAppContext();
            //appContext.sendBroadcast(intent);
        } else {
            Log.d(TAG, "Permissions not granted. Skipping initialization.");
        }
    }

    /**
     * Allow callback to other applications when data is stored in provider
     */
    private static AWARESensorObserver awareSensor;
    public static void setSensorObserver(AWARESensorObserver observer) {
        awareSensor = observer;
    }
    public static AWARESensorObserver getSensorObserver() {
        return awareSensor;
    }

    public interface AWARESensorObserver {
        void onDataChanged(ContentValues data);
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        //FIXME FB todo
        //Add permissions you need (Android M+).
        //By default, AWARE asks access to the #Manifest.permission.WRITE_EXTERNAL_STORAGE
        boolean permissionsOk = sensorsManager.addPermissions(this);

        if (permissionsOk) {
            //heart-aware: If database empty, add one dummy record as a starting point for reasoning.
            dbInitializer.initialize(this);
            //heart-aware: Create and register observers
            observerManager.create(getApplicationContext());

            //Make picture - uncomment to debug
            //final Intent intent = new Intent();
            //intent.setAction(PluginActions.ACTION_START_PHOTO_EMOTION_RECOGNITION);
            //Context appContext = HowAreYouApp.getAppContext();
            //appContext.sendBroadcast(intent);
        } else {
            Log.d(TAG, "Permissions not granted. Skipping initialization.");
        }



        if (PERMISSIONS_OK) {

            Log.d(TAG, "Plugin started.");
            DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

            //Initialize our plugin's settings
            Aware.setSetting(this, Settings.SETTINGS_PLUGIN_HOWAREYOU,   true);
            Aware.setSetting(this, Settings.SETTINGS_PHOTO,              true);
            Aware.setSetting(this, Settings.SETTINGS_QUESTION_EMOJI,     true);
            Aware.setSetting(this, Settings.SETTINGS_QUESTION_COLOR,     true);
            Aware.setSetting(this, Settings.SETTINGS_PHOTO_NOTIFICATION, false);

            Aware.setSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE, 1);//FIXME FB 30

            if (!Aware.isStudy(this)) {
                Log.d(TAG, "Joining study.");
                String studyUrl = "https://api.awareframework.com/index.php/webservice/index/2415/4a13qF3BHs8y";
                Aware.joinStudy(this, studyUrl);
                //Enable our plugin's sync-adapter to upload the data to the server if part of a study
                //if (Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE).length() >= 0 &&
                //        !Aware.isSyncEnabled(this, Provider.getAuthority(this)) &&
                //        Aware.isStudy(this) && getApplicationContext().getPackageName().equalsIgnoreCase("com.aware.phone") ||
                //        getApplicationContext().getResources().getBoolean(R.bool.standalone)) {
                ContentResolver.setIsSyncable(Aware.getAWAREAccount(this), Provider.getAuthority(this), 1);
                ContentResolver.addPeriodicSync(
                        Aware.getAWAREAccount(this),
                        Provider.getAuthority(this),
                        Bundle.EMPTY,
                        Long.parseLong(Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE)) * 60
                );
                //}
            } else {
                Log.d(TAG, "Already a member of study. Skipping.");
            }

            sensorsManager.initialiseSensors(this);

            //Initialise AWARE instance in plugin
            Aware.startAWARE(this);

            //Launch photo notification if necessary
            Intent serviceIntent = new Intent(this, PhotoNotificationDisplayService.class);
            startService(serviceIntent);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        observerManager.destroy(getApplicationContext());

        //Turn off the sync-adapter if part of a study
        if (Aware.isStudy(this) && (getApplicationContext().getPackageName().equalsIgnoreCase("com.aware.phone") || getApplicationContext().getResources().getBoolean(R.bool.standalone))) {
            ContentResolver.removePeriodicSync(
                    Aware.getAWAREAccount(this),
                    Provider.getAuthority(this),
                    Bundle.EMPTY
            );
        }

        Aware.setSetting(this, Settings.SETTINGS_PLUGIN_HOWAREYOU, false);

        //Stop AWARE instance in plugin
        Aware.stopAWARE(this);
    }
}
