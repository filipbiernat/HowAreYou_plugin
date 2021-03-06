package com.aware.plugin.howareyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aware.Aware;
import com.aware.plugin.howareyou.photo.EmotionRecognitionService;
import com.aware.plugin.howareyou.question.Question_Color;
import com.aware.plugin.howareyou.question.Question_Emoji;

import java.util.Calendar;
import java.util.Date;

import static com.aware.plugin.howareyou.Settings.SETTINGS_PHOTO;
import static com.aware.plugin.howareyou.Settings.SETTINGS_QUESTION_EMOJI;
import static com.aware.plugin.howareyou.Settings.SETTINGS_QUESTION_COLOR;

public class PluginManager extends BroadcastReceiver {
    private static final int MAX_ACTIVITY_LOG_ENTRIES = 100;
    private static int activityLogEntries = 0;
    private static String activityLog = new String();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        HowAreYouApp.getLatestPluginAction().setAction(action);//store value for callback
        Log.d(Plugin.TAG, "PluginManager: " + action);
        appendToActivityLog(action);

        switch (action) {
            case PluginActions.ACTION_START_QUESTION_COLOR:
                startQuestion_Color(context);
                break;
            case PluginActions.ACTION_ON_FINISHED_QUESTION_COLOR:
                onFinishedQuestion_Color(context);
                break;
            case PluginActions.ACTION_START_QUESTION_EMOJI:
                startQuestion_Emoji(context);
                break;
            case PluginActions.ACTION_ON_FINISHED_QUESTION_EMOJI:
                onFinishedQuestion_Emoji(context);
                break;
            case PluginActions.ACTION_START_PHOTO_EMOTION_RECOGNITION:
                startPhotoEmotionRecognition(context);
                break;
            case PluginActions.ACTION_ON_FINISHED_PHOTO_EMOTION_RECOGNITION:
                onFinishedPhotoEmotionRecognition(context);
                break;

            default:
                throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    private void startQuestion_Color(Context context) {
        if (Aware.getSetting(context, SETTINGS_QUESTION_COLOR).equals("true")) {
            Intent question_ColorIntent = new Intent(context, Question_Color.class);
            question_ColorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(question_ColorIntent);
        } else {
            onFinishedQuestion_Color(context);
        }
    }

    private void onFinishedQuestion_Color(Context context) {
        //Intent broadcastIntent = new Intent(PluginActions.ACTION_START_QUESTION_EMOJI);
        //context.sendBroadcast(broadcastIntent);
    }

    private void startQuestion_Emoji(Context context) {
        if (Aware.getSetting(context, SETTINGS_QUESTION_EMOJI).equals("true")) {
            Intent question_EmojiIntent = new Intent(context, Question_Emoji.class);
            question_EmojiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(question_EmojiIntent);
        } else {
            onFinishedQuestion_Emoji(context);
        }
    }

    private void onFinishedQuestion_Emoji(Context context) {
        //Intent broadcastIntent = new Intent(PluginActions.ACTION_START_PHOTO_EMOTION_RECOGNITION);
        //context.sendBroadcast(broadcastIntent);
    }

    private void startPhotoEmotionRecognition(Context context) {
        if (Aware.getSetting(context, SETTINGS_PHOTO).equals("true")) {
            Intent serviceIntent = new Intent(context, EmotionRecognitionService.class);
            context.startService(serviceIntent);
        } else {
            onFinishedPhotoEmotionRecognition(context);
        }
    }

    private void onFinishedPhotoEmotionRecognition(Context context) {
    }

    public static final String getActivityLog(){
        return activityLog;
    }

    public static void appendToActivityLog(String activity)
    {
        if (activityLogEntries < MAX_ACTIVITY_LOG_ENTRIES) {
            ++activityLogEntries;
        } else {
            activityLog = activityLog.substring(activityLog.indexOf("\n\n")+1);
        }
        Date currentTime = Calendar.getInstance().getTime();
        activityLog = activityLog + currentTime + "\n" + activity + "\n\n";
    }
}
