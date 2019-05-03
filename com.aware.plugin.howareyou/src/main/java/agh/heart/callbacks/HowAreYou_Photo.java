package agh.heart.callbacks;

import android.net.Uri;

import heart.Callback;

public class HowAreYou_Photo extends HowAreYou_Generic implements Callback {

    private static final String[] DB_COLUMNS = new String[]{
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.ANGER,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.CONTEMPT,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.DISGUST,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.FEAR,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.HAPPINESS,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.NEUTRAL,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.SADNESS,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.SURPRISE,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.TIMESTAMP
    };
    private static final Uri DB_URI = com.aware.plugin.howareyou.Provider.Table_Photo_Data.CONTENT_URI;
    private static final String TIMESTAMP_DB_COLUMN = com.aware.plugin.howareyou.Provider.Table_Photo_Data.TIMESTAMP;
    private static final String TIMEOUT_VARIABLE_NAME = "photo_timeout";

    public HowAreYou_Photo(){
        super(DB_COLUMNS, DB_URI, TIMESTAMP_DB_COLUMN, TIMEOUT_VARIABLE_NAME);
    }
}