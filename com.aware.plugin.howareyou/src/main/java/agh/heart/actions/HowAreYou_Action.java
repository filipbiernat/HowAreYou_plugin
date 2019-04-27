package agh.heart.actions;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aware.plugin.howareyou.HowAreYouApp;

import heart.Action;
import heart.State;

public abstract class HowAreYou_Action implements Action {

    private static final String TAG = "AWARE::HowAreYou::HeaRT";

    @Override
    public void execute(State state) {
        Log.d(TAG, "Execute action: " + getActionName());

        final Intent intent = new Intent();
        intent.setAction(getActionName());
        Context appContext = HowAreYouApp.getAppContext();
        appContext.sendBroadcast(intent);
    }

    protected abstract String getActionName();
}
