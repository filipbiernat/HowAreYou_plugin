package agh.heart.callbacks;

import android.database.Cursor;

import com.aware.providers.Accelerometer_Provider;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class Accelerometer extends HeaRTCallback implements Callback {
    private double accelerometer_1 = 0;
    private double accelerometer_2 = 0;
    private double accelerometer_3 = 0;

    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            updateFields();

            workingMemory.setAttributeValue("accelerometer_res_1", new SimpleNumeric(accelerometer_1));
            workingMemory.setAttributeValue("accelerometer_res_2", new SimpleNumeric(accelerometer_2));
            workingMemory.setAttributeValue("accelerometer_res_3", new SimpleNumeric(accelerometer_3));
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }

    private void updateFields() {
        if (resolver == null) {
            return;
        }

        String accelerometer_1_column = "double_values_0";
        String accelerometer_2_column = "double_values_1";
        String accelerometer_3_column = "double_values_2";

        Cursor values = getValues(new String[]{
                accelerometer_1_column,
                accelerometer_2_column,
                accelerometer_3_column});
        if (values.getCount() == 0) {
            return;
        }
        values.moveToFirst();

        accelerometer_1 = values.getDouble(values.getColumnIndex(accelerometer_1_column));
        accelerometer_2 = values.getDouble(values.getColumnIndex(accelerometer_2_column));
        accelerometer_3 = values.getDouble(values.getColumnIndex(accelerometer_3_column));
    }

    private Cursor getValues(String[] columns) {
        return resolver.query(Accelerometer_Provider.Accelerometer_Data.CONTENT_URI, columns, null, null, "timestamp DESC");
    }

}
