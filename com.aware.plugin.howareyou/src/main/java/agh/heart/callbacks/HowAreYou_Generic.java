package agh.heart.callbacks;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Map;

//Note! Timeout in minutes!
public class HowAreYou_Generic extends GenericDbCallback {

    private String timestampDbColumn; //key of db Column that should be treated differently
    private String timeoutVariableName; //name of the heartdroid variable that should represent timeout

    protected HowAreYou_Generic(String[] dbColumns, Uri dbUri, String timestampDbColumn, String timeoutVariableName) {
        super(dbColumns, dbUri);
        this.timestampDbColumn = timestampDbColumn;
        this.timeoutVariableName = timeoutVariableName;
    }

    protected String getVariableName(Map.Entry<String, Double> entry) {
        if (entry.getKey() == timestampDbColumn) {
            return timeoutVariableName;
        }
        return super.getVariableName(entry);
    }

    protected double getVariableValue(Map.Entry<String, Double> entry) {
        if (entry.getKey() == timestampDbColumn) {
            return calculateTimeout(entry.getValue());
        }
        return super.getVariableValue(entry);
    }

    private double calculateTimeout(Double value) {
        double timeDelta_ms = System.currentTimeMillis() - value;
        double timeDelta_min = timeDelta_ms/1000/60;
        return timeDelta_min;
    }

    @NonNull
    protected String getCallbackPrefix() {
        return "howareyou_";
    }
}
