package agh.heart.callbacks;

import android.database.Cursor;
import com.aware.providers.Gyroscope_Provider;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class Gyroscope extends HeaRTCallback implements Callback {
    private double gyroscope_x = 20;
    private double gyroscope_y;
    private double gyroscope_z = -40;

    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            updateFields();

            workingMemory.setAttributeValue("gyroscope_res_x", new SimpleNumeric(gyroscope_x));
            workingMemory.setAttributeValue("gyroscope_res_y", new SimpleNumeric(gyroscope_y));
            workingMemory.setAttributeValue("gyroscope_res_z", new SimpleNumeric(gyroscope_z));
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }

    private void updateFields() {
        if (resolver == null) {
            return;
        }

        String gyroscope_x_column = "double_values_0";
        String gyroscope_y_column = "double_values_1";
        String gyroscope_z_column = "double_values_2";

        Cursor values = getValues(new String[]{
                gyroscope_x_column,
                gyroscope_y_column,
                gyroscope_z_column});
        if (values.getCount() == 0) {
            return;
        }
        values.moveToFirst();

        gyroscope_x = values.getDouble(values.getColumnIndex(gyroscope_x_column));
        gyroscope_y = values.getDouble(values.getColumnIndex(gyroscope_y_column));
        gyroscope_z = values.getDouble(values.getColumnIndex(gyroscope_z_column));
    }

    private Cursor getValues(String[] columns) {
        return resolver.query(Gyroscope_Provider.Gyroscope_Data.CONTENT_URI, columns, null, null, "timestamp DESC");
    }

}
