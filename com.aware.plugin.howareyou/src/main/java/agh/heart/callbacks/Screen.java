package agh.heart.callbacks;

import android.database.Cursor;
import android.util.Log;

import com.aware.providers.Screen_Provider;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleSymbolic;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class Screen extends HeaRTCallback implements Callback {
    private int screen_status;
    private String[] VALUES = {"off", "on", "locked", "unlocked"};

    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            updateFields();

            Log.d(TAG, "Callback for screen. Status: " + VALUES[screen_status]);

            workingMemory.setAttributeValue("screen_status", new SimpleSymbolic(VALUES[screen_status]));
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }

    private void updateFields() {
        if (resolver == null) {
            return;
        }

        String screen_status_column = "screen_status";

        Cursor values = getValues(new String[]{screen_status_column});
        if (values.getCount() == 0) {
            return;
        }
        values.moveToFirst();

        screen_status = values.getInt(values.getColumnIndex(screen_status_column));
    }

    private Cursor getValues(String[] columns) {
        return resolver.query(Screen_Provider.Screen_Data.CONTENT_URI, columns, null, null, "timestamp DESC");
    }
}
