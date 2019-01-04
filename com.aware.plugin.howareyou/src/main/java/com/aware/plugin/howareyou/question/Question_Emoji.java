package com.aware.plugin.howareyou.question;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.howareyou.PluginActions;
import com.aware.plugin.howareyou.Provider;
import com.aware.plugin.howareyou.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

public class Question_Emoji extends AppCompatActivity {

    protected static final String TAG = "AWARE::HowAreYou::Qstn";
    protected static final Boolean DEBUG = true;
    private static final int COLOR_WHITE = 0xFFFFFF;
    public static final int ACTIVITY_TIMEOUT_SECONDS = 10;
    public static final int USER_RESPONSE_TIMEOUT_SECONDS = 3;

    private int selectedColor = COLOR_WHITE;
    private boolean inProgress = true;
    private boolean savedResponse = false;
    private TimeoutMonitor activityTimeoutMonitor;
    private TimeoutMonitor userResponseTimeoutMonitor;

    @Override
    protected void onResume() {
        super.onResume();
        inProgress = true;
        savedResponse = false;

        setContentView(R.layout.question_color);
        configureColorPicker();
        configureViewButtons();

        activityTimeoutMonitor = new TimeoutMonitor(System.currentTimeMillis(), ACTIVITY_TIMEOUT_SECONDS);
        activityTimeoutMonitor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        userResponseTimeoutMonitor = null;
    }

    private void configureViewButtons() {
        Button cancel = (Button) findViewById(R.id.question_color_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inProgress = false;
                closeDialog();
            }
        });
        Button submit = (Button) findViewById(R.id.question_color_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inProgress = false;
                saveResponse();
            }
        });
    }

    private void configureColorPicker() {
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.color_picker_view);
        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int selectedColor) {
                setSelectedColor(selectedColor);
            }
        });
        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                setSelectedColor(selectedColor);
            }
        });
    }

    private void setSelectedColor(int selectedColor)
    {
        this.selectedColor = selectedColor & COLOR_WHITE;
        resetUserResponseTimeoutMonitor();
    }

    private int getSelectedColor()
    {
        return selectedColor & COLOR_WHITE;
    }

    private void saveResponse() {
        if (!savedResponse) {
            savedResponse = true;
            logDebug("Save response: Selected color: " + Integer.toHexString(getSelectedColor()));

            insertTheAnswers();

            Intent broadcastIntent = new Intent(PluginActions.ACTION_ON_FINISHED_QUESTION_EMOJI);
            sendBroadcast(broadcastIntent);

            closeDialog();

        } else {
            logDebug("Save response: Color already selected.");
        }
    }

    private void closeDialog() {
        userResponseTimeoutMonitor = null;
        finish();
        moveTaskToBack(true);
    }

    private void resetUserResponseTimeoutMonitor() {
        if (userResponseTimeoutMonitor == null) {
            userResponseTimeoutMonitor = new TimeoutMonitor(System.currentTimeMillis(), USER_RESPONSE_TIMEOUT_SECONDS);
            userResponseTimeoutMonitor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            userResponseTimeoutMonitor.setDisplayTimestamp(System.currentTimeMillis());
        }
    }

    private void insertTheAnswers() {
        final int MASK = 0xFF;
        int colorRed   = (selectedColor >> 16) & MASK;
        int colorGreen = (selectedColor >>  8) & MASK;
        int colorBlue  = (selectedColor      ) & MASK;

        ContentValues answer = new ContentValues();
        answer.put(Provider.Table_Color_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(),
                Aware_Preferences.DEVICE_ID));
        answer.put(Provider.Table_Color_Data.TIMESTAMP, System.currentTimeMillis());

        answer.put(Provider.Table_Color_Data.COLOR_RED,   colorRed);
        answer.put(Provider.Table_Color_Data.COLOR_GREEN, colorGreen);
        answer.put(Provider.Table_Color_Data.COLOR_BLUE,  colorBlue);

        getContentResolver().insert(Provider.Table_Color_Data.CONTENT_URI, answer);
    }

    class TimeoutMonitor extends AsyncTask<Void, Void, Void> {
        private long displayTimestamp = 0;
        private int expiresInSeconds = 0;
        public TimeoutMonitor(long displayTimestamp, int expiresInSeconds) {
            this.displayTimestamp = displayTimestamp;
            this.expiresInSeconds = expiresInSeconds;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while ((System.currentTimeMillis() - displayTimestamp) / 1000 <= expiresInSeconds) {
                if (!inProgress) {
                    return null;
                }
            }
            logDebug("Activity timer has expired. Timeout: " + expiresInSeconds + " sec.");
            saveResponse();
            return null;
        }

        public void setDisplayTimestamp(long displayTimestamp) {
            this.displayTimestamp = displayTimestamp;
        }
    }

    protected void logDebug(String debugString) {
        if (DEBUG) {
            Log.d(TAG, debugString);
        }
    }
}
