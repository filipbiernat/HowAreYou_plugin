package com.aware.plugin.howareyou.plugin;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.PermissionChecker;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Communication;
import com.aware.plugin.howareyou.HowAreYouApp;
import com.aware.utils.Aware_Plugin;

public class SensorsManager {

    private static final String TAG = "AWARE::HowAreYou:Sensor";

    private static final String[] SENSORS_PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE
            //Note! Add here and also to the manifest (section uses-permission)
    };

    public boolean addPermissions(Aware_Plugin plugin) {
        for (String permission : SENSORS_PERMISSIONS){
            plugin.REQUIRED_PERMISSIONS.add(permission);
        }

        boolean PERMISSIONS_OK = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : SENSORS_PERMISSIONS) {
                if (PermissionChecker.checkSelfPermission(plugin, p) != PermissionChecker.PERMISSION_GRANTED) {
                    PERMISSIONS_OK = false;
                    break;
                }
            }
        }
        return PERMISSIONS_OK;
    }

    public void initialiseSensors(final Context context) {
        initialiseSensorsNecessaryForCallbacksAndObservers(context);
        //TODO uncomment below when needed
        //make sure there are no problems with permissions
        //initialiseSensorsNotNecessaryForCallbacksAndObservers(context);
    }

    public void initialiseSensorsNecessaryForCallbacksAndObservers(final Context context) {
        startApplications(context);
        startCommunication(context);

        Aware.startSignificant(context);
        Aware.startScreen(context);

    }

    public void initialiseSensorsNotNecessaryForCallbacksAndObservers(final Context context){
        Aware.startAccelerometer(context);
        Aware.startKeyboard(context);
        Aware.startInstallations(context);
        Aware.startLinearAccelerometer(context);
        Aware.startGravity(context);
        Aware.startBarometer(context);
        Aware.startMagnetometer(context);
        Aware.startProximity(context);
        Aware.startLight(context);
        Aware.startRotation(context);
        Aware.startTelephony(context);
        Aware.startWiFi(context);
        Aware.startGyroscope(context);
        Aware.startAccelerometer(context);
        Aware.startProcessor(context);
        Aware.startLocations(context);
        Aware.startBluetooth(context);
        Aware.startBattery(context);
        Aware.startNetwork(context);
        Aware.startTraffic(context);
        Aware.startTimeZone(context);
        //Aware.startESM(context);
        //Aware.startMQTT(context);
        //Aware.startTemperature(context); Not supported
    }

    private void startApplications(Context context) {
        Aware.setSetting(context, Aware_Preferences.STATUS_APPLICATIONS, true);
    }

    private void startCommunication(Context context) {
        Aware.startCommunication(context);
        Aware.setSetting(context, Aware_Preferences.STATUS_CALLS, true);
        Aware.setSetting(context, Aware_Preferences.STATUS_MESSAGES, true);
        Communication.setSensorObserver(HowAreYouApp.getCommunicationObserver());
    }
}