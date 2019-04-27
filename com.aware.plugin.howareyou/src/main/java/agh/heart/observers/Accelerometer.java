package agh.heart.observers;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

public class Accelerometer extends Observer {
    @Override
    public void register(Context context) {
        super.register(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.aware.Accelerometer.ACTION_AWARE_ACCELEROMETER);
        context.registerReceiver(this, intentFilter);
    }

    public String getObserverName(){
        return "Accelerometer";
    }
}
