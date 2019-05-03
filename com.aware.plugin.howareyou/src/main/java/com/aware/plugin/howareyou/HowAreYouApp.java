package com.aware.plugin.howareyou;

import android.app.Application;
import android.content.Context;

import com.aware.plugin.howareyou.utils.CommunicationObserver;

public class HowAreYouApp extends Application {

    private static Context context;

    private static CommunicationObserver communicationObserver = new CommunicationObserver();

    public void onCreate() {
        super.onCreate();
        HowAreYouApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return HowAreYouApp.context;
    }

    public static CommunicationObserver getCommunicationObserver() {
        return communicationObserver;
    }
}