package com.aware.plugin.howareyou;

import android.app.Application;
import android.content.Context;

import com.aware.plugin.howareyou.plugin.CommunicationObserver;
import com.aware.plugin.howareyou.plugin.LatestPluginAction;

public class HowAreYouApp extends Application {

    private static Context context;

    private static CommunicationObserver communicationObserver = new CommunicationObserver();

    private static LatestPluginAction latestPluginAction = new LatestPluginAction();

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

    public static LatestPluginAction getLatestPluginAction() {
        return latestPluginAction;
    }
}