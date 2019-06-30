package agh.heart.actions;
import android.util.Log;

import com.aware.plugin.howareyou.PluginManager;

import heart.Action;
import heart.State;

public class HowAreYou_NoAction implements Action {

    private static final String TAG = "AWARE::HowAreYou::HeaRT";

    @Override
    public void execute(State state) {
        Log.d(TAG, "Execute action: NoAction");
        PluginManager.appendToActivityLog("NO_ACTION");
    }
}
