package agh.heart.callbacks;

import android.net.Uri;

import heart.Callback;

public class HowAreYou_Emotion extends HowAreYou_Generic implements Callback {

    private static final String[] DB_COLUMNS = new String[]{
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.EMOTION_HAPPY,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.EMOTION_EXCITED,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.EMOTION_TENDER,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.EMOTION_SCARED,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.EMOTION_ANGRY,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.EMOTION_SAD,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.EMOTION_DROPPED,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.TIMESTAMP
    };
    private static final Uri DB_URI = com.aware.plugin.howareyou.Provider.Table_Emotion_Data.CONTENT_URI;
    private static final String TIMESTAMP_DB_COLUMN = com.aware.plugin.howareyou.Provider.Table_Emotion_Data.TIMESTAMP;
    private static final String TIMEOUT_VARIABLE_NAME = "emotion_timeout";

    public HowAreYou_Emotion(){
        super(DB_COLUMNS, DB_URI, TIMESTAMP_DB_COLUMN, TIMEOUT_VARIABLE_NAME);
    }
}