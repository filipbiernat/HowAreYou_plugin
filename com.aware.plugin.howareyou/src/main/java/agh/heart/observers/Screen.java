package agh.heart.observers;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;


public class Screen extends Observer {

    @Override
    public void register(Context context) {
        super.register(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.aware.Screen.ACTION_AWARE_SCREEN_LOCKED);
        intentFilter.addAction(com.aware.Screen.ACTION_AWARE_SCREEN_OFF);
        intentFilter.addAction(com.aware.Screen.ACTION_AWARE_SCREEN_ON);
        intentFilter.addAction(com.aware.Screen.ACTION_AWARE_SCREEN_UNLOCKED);
        context.registerReceiver(this, intentFilter);
    }

    public String getObserverName(){
        return "Screen";
    }
}
