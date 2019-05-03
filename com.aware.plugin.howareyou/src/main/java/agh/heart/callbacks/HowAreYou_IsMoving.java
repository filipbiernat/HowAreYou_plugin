package agh.heart.callbacks;

import android.net.Uri;

import heart.Callback;

public class HowAreYou_IsMoving extends HowAreYou_Generic implements Callback {

    private static final String[] DB_COLUMNS = new String[]{
            com.aware.providers.Significant_Provider.Significant_Data.IS_MOVING,
            com.aware.providers.Significant_Provider.Significant_Data.TIMESTAMP
    };
    private static final Uri DB_URI = com.aware.providers.Significant_Provider.Significant_Data.CONTENT_URI;
    private static final String TIMESTAMP_DB_COLUMN = com.aware.providers.Significant_Provider.Significant_Data.TIMESTAMP;
    private static final String TIMEOUT_VARIABLE_NAME =
            com.aware.providers.Significant_Provider.Significant_Data.IS_MOVING + "_timeout";

    public HowAreYou_IsMoving(){
        super(DB_COLUMNS, DB_URI, TIMESTAMP_DB_COLUMN, TIMEOUT_VARIABLE_NAME);
    }
}