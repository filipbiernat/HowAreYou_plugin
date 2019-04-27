package com.aware.plugin.howareyou;

import android.app.Application;
import android.content.Context;

public class HowAreYouApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        HowAreYouApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return HowAreYouApp.context;
    }
}