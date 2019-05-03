package agh.heart.callbacks;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Map;

import heart.Callback;

public class Application_Generic extends GenericDbCallback implements Callback {

    private static final String[] DB_COLUMNS = new String[]{
            com.aware.providers.Applications_Provider.Applications_Foreground.PACKAGE_NAME,
    };
    private static final Uri DB_URI = com.aware.providers.Applications_Provider.Applications_Foreground.CONTENT_URI;

    private String applicationName;
    private String applicationPackage;

    public Application_Generic(String applicationName, String applicationPackage){
        super(DB_COLUMNS, DB_URI);
        this.applicationName = applicationName;
        this.applicationPackage = applicationPackage;
    }

    @NonNull
    protected String getCallbackPrefix() {
        return "application_";
    }

    protected String getVariableName(Map.Entry<String, Double> entry) {
        return applicationName;
    }

    protected double getADoubleFromDb(Cursor values, String dbColumn) {
        return values.getString(values.getColumnIndex(dbColumn)).equals(applicationPackage) ? 1.0 : 0.0;
    }
}