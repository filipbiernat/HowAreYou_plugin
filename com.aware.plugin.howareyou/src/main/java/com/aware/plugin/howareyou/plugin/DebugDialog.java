package com.aware.plugin.howareyou.plugin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.aware.Aware;
import com.aware.plugin.howareyou.R;
import com.aware.plugin.howareyou.Settings;

public class DebugDialog extends Activity {
    private AlertDialog alertDialog = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Intent intent = getIntent();
            boolean runAlways = intent.getBooleanExtra("RUN_ALWAYS", false);
            if(Aware.getSetting(this, Settings.SETTINGS_DEBUG_MODE).equals("true") || runAlways) {
                String message = intent.getStringExtra("MESSAGE_CONTENT");


                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("HowAreYou Debug");
                alertDialog.setIcon(R.drawable.ic_launcher);
                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();

                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                if(textView != null) {
                    textView.setTextSize(12);
                }
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (alertDialog != null){
            alertDialog.dismiss();
        }
    }
}