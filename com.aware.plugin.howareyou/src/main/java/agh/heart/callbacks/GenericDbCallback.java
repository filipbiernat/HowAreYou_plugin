package agh.heart.callbacks;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class GenericDbCallback extends HeaRTCallback implements Callback {

    private String[] dbColumns;
    private Uri dbUri;
    Map<String,Double> dbValues;

    protected GenericDbCallback(String[] dbColumns, Uri dbUri){
        this.dbColumns = dbColumns;
        this.dbUri = dbUri;
        dbValues = new HashMap<>();
    }

    //Protect from calling default constructor
    private GenericDbCallback(){
        Log.e("TAG", "private GenericDbCallback() - this should not be executed!");
    }

    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            updateFields();

            //Fill working memory with updated fields
            for (Map.Entry<String, Double> entry : dbValues.entrySet()) {
                fillWorkingMemory(workingMemory, entry.getKey(), entry.getValue());
            }

        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }

    private void fillWorkingMemory(WorkingMemory workingMemory, String attributeName, double value)
            throws AttributeNotRegisteredException, NotInTheDomainException {
        final String HOWAREYOU_PREFIX = "howareyou_";
        workingMemory.setAttributeValue(HOWAREYOU_PREFIX + attributeName, new SimpleNumeric(value));
    }

    private void updateFields() {
        if (resolver == null) {
            return;
        }

        Cursor values = getValues(dbColumns);
        if (values.getCount() == 0) {
            return;
        }
        values.moveToFirst();

        //Update map instead of separate fields
        for (String dbColumn : dbColumns){
            dbValues.put(dbColumn, values.getDouble(values.getColumnIndex(dbColumn)));
        }
    }

    private Cursor getValues(String[] columns) {
        return resolver.query(dbUri, columns, null, null, "timestamp DESC");
    }

}
