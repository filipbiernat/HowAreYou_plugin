package com.aware.plugin.howareyou;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.aware.Accelerometer;
import com.aware.Aware;
import com.aware.Screen;

public class SensorsManager {

    private static final String TAG = "AWARE::HowAreYou:Sensor";

    public void initialiseSensors(final Context context){
        startAccelerometer(context);

        Aware.startSignificant(context);
        Aware.startTemperature(context);
        if(false){
            Aware.startScheduler(context);
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
            Aware.startScreen(context);
            Aware.startBattery(context);
            Aware.startNetwork(context);
            Aware.startTraffic(context);
            Aware.startTimeZone(context);
            Aware.startCommunication(context);
            //Aware.startESM(context);
            //Aware.startMQTT(context);
        }
    }

    private void startAccelerometer(final Context context) {
        Aware.startAccelerometer(context);
        Accelerometer.setSensorObserver(new Accelerometer.AWARESensorObserver() {
            @Override
            public void onAccelerometerChanged(ContentValues contentValues) {
                context.sendBroadcast(new Intent("ACCELEROMETER_DATA").putExtra("data", contentValues));
            }
        });
    }
}