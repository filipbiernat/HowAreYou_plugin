package com.aware.plugin.howareyou.question;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.howareyou.PluginActions;
import com.aware.plugin.howareyou.Provider;
import com.aware.plugin.howareyou.R;

public class Question_Emoji extends SlidableActivity {

    protected static final String TAG = "AWARE::HowAreYou_Color::Qstn";
    protected static final Boolean DEBUG = true;
    public static final int ACTIVITY_TIMEOUT_SECONDS = 10;

    private static final String EMOTION_HAPPY   = "happy";
    private static final String EMOTION_EXCITED = "excited";
    private static final String EMOTION_TENDER  = "tender";
    private static final String EMOTION_SCARED  = "scared";
    private static final String EMOTION_ANGRY   = "angry";
    private static final String EMOTION_SAD     = "sad";
    private static final String EMOTION_DROPPED = "dropped";

    private boolean inProgress = true;
    private boolean savedResponse = false;
    private TimeoutMonitor activityTimeoutMonitor;

    @Override
    protected void onResume() {
        super.onResume();
        inProgress = true;
        savedResponse = false;

        setContentView(R.layout.question_emoji);

        configureViewButtons();

        activityTimeoutMonitor = new TimeoutMonitor(System.currentTimeMillis(), ACTIVITY_TIMEOUT_SECONDS);
        activityTimeoutMonitor.execute();
    }
    private void configureViewButtons() {
        configureViewButton(R.id.button_happy,   EMOTION_HAPPY);
        configureViewButton(R.id.button_excited, EMOTION_EXCITED);
        configureViewButton(R.id.button_tender,  EMOTION_TENDER);
        configureViewButton(R.id.button_scared,  EMOTION_SCARED);
        configureViewButton(R.id.button_angry,   EMOTION_ANGRY);
        configureViewButton(R.id.button_sad,     EMOTION_SAD);
    }

    private void configureViewButton(int buttonId, final String emotion) {
        ImageView button = (ImageView) findViewById(buttonId);
        button.setClickable(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inProgress = false;
                closeDialog();
                saveResponse(emotion);
            }
        });
    }


    private void saveResponse(String emotion) {
        if (!savedResponse) {
            savedResponse = true;
            logDebug("Save response: Selected emotion: " + emotion + ".");

            insertTheAnswer(emotion);

            Intent broadcastIntent = new Intent(PluginActions.ACTION_ON_FINISHED_QUESTION_EMOJI);
            sendBroadcast(broadcastIntent);

            closeDialog();

        } else {
            logDebug("Save response: Color already selected.");
        }
    }

    private void closeDialog() {
        finish();
        moveTaskToBack(true);
    }

    private void insertTheAnswer(String emotion) {
        ContentValues answer = new ContentValues();
        answer.put(Provider.Table_Emotion_Data.DEVICE_ID, Aware.getSetting(getApplicationContext(),
                Aware_Preferences.DEVICE_ID));
        answer.put(Provider.Table_Emotion_Data.TIMESTAMP, System.currentTimeMillis());


        answer.put(Provider.Table_Emotion_Data.EMOTION_HAPPY,   emotion.equals(EMOTION_HAPPY  ) ? 1 : 0);
        answer.put(Provider.Table_Emotion_Data.EMOTION_EXCITED, emotion.equals(EMOTION_EXCITED) ? 1 : 0);
        answer.put(Provider.Table_Emotion_Data.EMOTION_TENDER,  emotion.equals(EMOTION_TENDER ) ? 1 : 0);
        answer.put(Provider.Table_Emotion_Data.EMOTION_SCARED,  emotion.equals(EMOTION_SCARED ) ? 1 : 0);
        answer.put(Provider.Table_Emotion_Data.EMOTION_ANGRY,   emotion.equals(EMOTION_ANGRY  ) ? 1 : 0);
        answer.put(Provider.Table_Emotion_Data.EMOTION_SAD,     emotion.equals(EMOTION_SAD    ) ? 1 : 0);
        answer.put(Provider.Table_Emotion_Data.EMOTION_DROPPED, emotion.equals(EMOTION_DROPPED) ? 1 : 0);

        getContentResolver().insert(Provider.Table_Emotion_Data.CONTENT_URI, answer);
    }

    protected void onSlide(){
        saveResponse(EMOTION_DROPPED);
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

            Intent broadcastIntent = new Intent(PluginActions.ACTION_ON_FINISHED_QUESTION_EMOJI);
            sendBroadcast(broadcastIntent);

            closeDialog();
            return null;
        }
    }

    protected void logDebug(String debugString) {
        if (DEBUG) {
            Log.d(TAG, debugString);
        }
    }
}
