package com.aware.plugin.howareyou;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.aware.utils.IContextCard;

import uk.me.berndporr.iirj.Butterworth;

public class ContextCard implements IContextCard {

    private Butterworth butterworth0, butterworth1, butterworth2;

    //Constructor used to instantiate this card
    public ContextCard() {
        butterworth0 = new Butterworth();
        butterworth0.lowPass(4, 250, 50);
        butterworth1 = new Butterworth();
        butterworth1.lowPass(4, 250, 50);
        butterworth2 = new Butterworth();
        butterworth2.lowPass(4, 250, 50);
    }

    private TextView hello = null;

    @Override
    public View getContextCard(Context context) {
        //Load card layout
        View card = LayoutInflater.from(context).inflate(R.layout.card, null);
        hello = card.findViewById(R.id.hello);

        //Register the broadcast receiver that will update the UI from the background service (Plugin)
        IntentFilter filter = new IntentFilter("ACCELEROMETER_DATA");
        context.registerReceiver(accelerometerObserver, filter);

        //Return the card to AWARE/apps
        return card;
    }

    //This broadcast receiver is auto-unregistered because it's not static.
    private AccelerometerObserver accelerometerObserver = new AccelerometerObserver();
    public class AccelerometerObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("ACCELEROMETER_DATA")) {
                ContentValues data = intent.getParcelableExtra("data");

                StringBuilder sb = new StringBuilder();
                sb.append(data.toString()).append("\n\n");

                sb.append("Butterworth low-pass:\n");

                double data0 = data.getAsDouble("double_values_0");
                data0 = butterworth0.filter(data0);
                sb.append("data0=").append(data0).append("\n");

                double data1 = data.getAsDouble("double_values_1");
                data1 = butterworth0.filter(data1);
                sb.append("data1=").append(data1).append("\n");

                double data2 = data.getAsDouble("double_values_2");
                data2 = butterworth0.filter(data2);
                sb.append("data2=").append(data2).append("\n");

                hello.setText(sb.toString());
            }
        }
    }
}
