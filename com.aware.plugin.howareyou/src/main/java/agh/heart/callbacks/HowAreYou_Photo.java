package agh.heart.callbacks;

import android.net.Uri;

import heart.Callback;

public class HowAreYou_Photo extends GenericDbCallback implements Callback {

    private static final String[] DB_COLUMNS = new String[]{
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.ANGER,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.CONTEMPT,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.DISGUST,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.FEAR,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.HAPPINESS,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.NEUTRAL,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.SADNESS,
            com.aware.plugin.howareyou.Provider.Table_Photo_Data.SURPRISE};
    private static final Uri DB_URI = com.aware.plugin.howareyou.Provider.Table_Photo_Data.CONTENT_URI;

    public HowAreYou_Photo(){
        super(DB_COLUMNS, DB_URI);
    }
}