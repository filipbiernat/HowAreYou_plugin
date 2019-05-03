package com.aware.plugin.howareyou.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import agh.heart.callbacks.HeaRTCallback;
import agh.heart.observers.Observer;

public class HeaRTAwareObserverManager  {

    private static final String TAG = "AWARE::HowAreYou::HeaRT";

    private List<Observer> observers = new ArrayList<>();

    public void create(Context context) {
        Log.d(TAG, "Creating observers.");
        createObservers();
        registerObservers(context);
    }

    private void createObservers() {
        observers.add(new agh.heart.observers.Screen());
        //TODO uncomment when model
        //observers.add(new agh.heart.observers.HowAreYou());

        //Add new observers here
    }

    private void registerObservers(Context context) {
        HeaRTCallback.register(context.getContentResolver());
        for (Observer observer : observers) {
            observer.register(context);
        }
    }

    public void destroy(Context context) {
        Log.d(TAG, "Destroying observers.");
        unregisterObservers(context);
    }

    private void unregisterObservers(Context context) {
        for (Observer observer : observers) {
            observer.unregister(context);
        }
        HeaRTCallback.unregister();
    }
}