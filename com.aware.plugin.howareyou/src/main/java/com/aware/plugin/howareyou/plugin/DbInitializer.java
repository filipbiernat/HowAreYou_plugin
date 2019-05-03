package com.aware.plugin.howareyou.plugin;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.howareyou.Provider;

public class DbInitializer {
    public void initialize(Context context) {
        //TODO make below generic
        initializeTable(context, Provider.Table_Photo_Data.CONTENT_URI);
    }

    protected void initializeTable(Context context, Uri uri){
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
