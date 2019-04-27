package agh.heart.callbacks;

import android.database.Cursor;
import android.util.Log;

import com.aware.providers.Locations_Provider;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class Location extends HeaRTCallback implements Callback {
    private double latitude;
    private double longitude;


    @Override
    public void execute(Attribute xttModel, WorkingMemory workingMemory) {
        try {
            updateFields();

            workingMemory.setAttributeValue("longitude", new SimpleNumeric(longitude));
            workingMemory.setAttributeValue("latitude", new SimpleNumeric(latitude));
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }

    private void updateFields() {
        if (resolver == null) {
            return;
        }

        String latitude_column = "double_latitude";
        String longitude_column = "double_longitude";

        Cursor values = getValues(new String[]{latitude_column, longitude_column});
        if (values.getCount() == 0) {
            return;
        }
        values.moveToFirst();

        latitude = values.getDouble(values.getColumnIndex(latitude_column));
        longitude = values.getDouble(values.getColumnIndex(longitude_column));
    }

    private Cursor getValues(String[] columns) {
        return resolver.query(Locations_Provider.Locations_Data.CONTENT_URI, columns, null, null, "timestamp DESC");
    }
}
