package agh.heart.callbacks;

import android.database.Cursor;
import android.util.Log;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class HowAreYou_Color extends HeaRTCallback implements Callback {

    private double howareyou_color_red = 0;
    private double howareyou_color_green = 0;
    private double howareyou_color_blue = 0;
    private double howareyou_color_dropped = 0;

    private String howareyou_color_red_column;
    private String howareyou_color_green_column;
    private String howareyou_color_blue_column;
    private String howareyou_color_dropped_column;

    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            updateFields();

            fillWorkingMemory(workingMemory, howareyou_color_red_column,     howareyou_color_red);
            fillWorkingMemory(workingMemory, howareyou_color_green_column,   howareyou_color_green);
            fillWorkingMemory(workingMemory, howareyou_color_blue_column,    howareyou_color_blue);
            fillWorkingMemory(workingMemory, howareyou_color_dropped_column, howareyou_color_dropped);
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }

        Log.d("FILIP", "r:" + howareyou_color_red +
                ", g:" + howareyou_color_green +
                ", b:" + howareyou_color_blue +
                ", d:" + howareyou_color_dropped);
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

        howareyou_color_red_column      = com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_RED;
        howareyou_color_green_column    = com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_GREEN;
        howareyou_color_blue_column     = com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_BLUE;
        howareyou_color_dropped_column  = com.aware.plugin.howareyou.Provider.Table_Color_Data.COLOR_DROPPED;

        Cursor values = getValues(new String[]{
                howareyou_color_red_column,
                howareyou_color_green_column,
                howareyou_color_blue_column,
                howareyou_color_dropped_column});
        if (values.getCount() == 0) {
            return;
        }
        values.moveToFirst();

        howareyou_color_red     = values.getDouble(values.getColumnIndex(howareyou_color_red_column));
        howareyou_color_green   = values.getDouble(values.getColumnIndex(howareyou_color_green_column));
        howareyou_color_blue    = values.getDouble(values.getColumnIndex(howareyou_color_blue_column));
        howareyou_color_dropped = values.getDouble(values.getColumnIndex(howareyou_color_dropped_column));
    }

    private Cursor getValues(String[] columns) {
        return resolver.query(com.aware.plugin.howareyou.Provider.Table_Color_Data.CONTENT_URI, columns, null, null, "timestamp DESC");
    }

}
