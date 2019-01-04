package com.aware.plugin.howareyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aware.Aware;
import com.aware.plugin.howareyou.photo.EmotionRecognitionService;
import com.aware.plugin.howareyou.question.Question_Color;
import com.aware.plugin.howareyou.question.Question_Emoji;

import static com.aware.plugin.howareyou.Settings.STATUS_PHOTO;
import static com.aware.plugin.howareyou.Settings.STATUS_QUESTION_EMOJI;
import static com.aware.plugin.howareyou.Settings.STATUS_QUESTION_COLOR;

public class PluginManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(Plugin.TAG, "OnReceive: " + action);

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
        if (Aware.getSetting(context, STATUS_QUESTION_COLOR).equals("true")) {
            Intent question_ColorIntent = new Intent(context, Question_Color.class);
            question_ColorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(question_ColorIntent);
        } else {
            onFinishedQuestion_Color(context);
        }
    }

    private void onFinishedQuestion_Color(Context context) {
        Intent broadcastIntent = new Intent(PluginActions.ACTION_START_QUESTION_EMOJI);
        context.sendBroadcast(broadcastIntent);
    }

    private void startQuestion_Emoji(Context context) {
        if (Aware.getSetting(context, STATUS_QUESTION_EMOJI).equals("true")) {
            Intent question_EmojiIntent = new Intent(context, Question_Emoji.class);
            question_EmojiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(question_EmojiIntent);
        } else {
            onFinishedQuestion_Emoji(context);
        }
    }

    private void onFinishedQuestion_Emoji(Context context) {
        Intent broadcastIntent = new Intent(PluginActions.ACTION_START_PHOTO_EMOTION_RECOGNITION);
        context.sendBroadcast(broadcastIntent);
    }

    private void startPhotoEmotionRecognition(Context context) {
        if (Aware.getSetting(context, STATUS_PHOTO).equals("true")) {
            Intent serviceIntent = new Intent(context, EmotionRecognitionService.class);
            context.startService(serviceIntent);
        } else {
            onFinishedPhotoEmotionRecognition(context);
        }
    }

    private void onFinishedPhotoEmotionRecognition(Context context) {
        //TODO
    }


}
