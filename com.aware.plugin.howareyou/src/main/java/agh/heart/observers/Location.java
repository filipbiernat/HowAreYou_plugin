package agh.heart.observers;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import com.aware.Locations;


public class Location extends Observer {

    @Override
    public void register(Context context) {
        super.register(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Locations.ACTION_AWARE_LOCATIONS);
        context.registerReceiver(this, intentFilter);
    }

    public String getObserverName(){
        return "Location";
    }
}
