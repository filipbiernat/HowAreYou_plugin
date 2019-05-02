package agh.heart.callbacks;
import android.net.Uri;
import heart.Callback;

public class HowAreYou_Color extends GenericDbCallbackWithTimeout implements Callback {

    private static final String[] DB_COLUMNS = new String[]{
            com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_RED,
            com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_GREEN,
            com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_BLUE,
            com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_DROPPED,
            com.aware.plugin.howareyou.Provider.Table_Color_Data.TIMESTAMP
    };
    private static final Uri DB_URI = com.aware.plugin.howareyou.Provider.Table_Color_Data.CONTENT_URI;
    private static final String TIMESTAMP_DB_COLUMN = com.aware.plugin.howareyou.Provider.Table_Color_Data.TIMESTAMP;
    private static final String TIMEOUT_VARIABLE_NAME = "color_timeout";

    public HowAreYou_Color(){
        super(DB_COLUMNS, DB_URI, TIMESTAMP_DB_COLUMN, TIMEOUT_VARIABLE_NAME);
    }
}