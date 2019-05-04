package com.aware.plugin.howareyou.plugin;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DbInitializer {

    private static final Uri[] URIS_TO_INITIALIZE = new Uri[]{
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.CONTENT_URI,
            com.aware.plugin.howareyou.Provider.Table_Color_Data.CONTENT_URI,
            com.aware.plugin.howareyou.Provider.Table_Emotion_Data.CONTENT_URI,
            com.aware.providers.Accelerometer_Provider.Accelerometer_Data.CONTENT_URI,
            com.aware.providers.Gyroscope_Provider.Gyroscope_Data.CONTENT_URI,
            com.aware.providers.Significant_Provider.Significant_Data.CONTENT_URI,
            com.aware.providers.Locations_Provider.Locations_Data.CONTENT_URI,
            com.aware.providers.Screen_Provider.Screen_Data.CONTENT_URI
    };

    public void initialize(Context context) {
        for (Uri uri : URIS_TO_INITIALIZE){
            initializeTable(context, uri);
        }
    }

    private void initializeTable(Context context, Uri uri){
        if (isTableEmpty(context, uri)){
            context.getContentResolver().insert(uri, new ContentValues());
        }
    }

    private boolean isTableEmpty(Context context, Uri uri){
        int numRecords = 0;

        Cursor counter = context.getContentResolver().query(uri,
                new String[]{"count(*) as entries"},
                null,
                null,
                "_id ASC");
        if (counter != null && counter.moveToFirst()) {
                numRecords = counter.getInt(0);
                counter.close();
        }
        if (counter != null && !counter.isClosed()) {
            counter.close();
        }

        return numRecords == 0;
    }
}
