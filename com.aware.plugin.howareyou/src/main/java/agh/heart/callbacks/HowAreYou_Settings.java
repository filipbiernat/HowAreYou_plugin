package agh.heart.callbacks;

import android.content.Context;
import android.util.Log;

import com.aware.Aware;
import com.aware.plugin.howareyou.HowAreYouApp;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleNumeric;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class HowAreYou_Settings extends HeaRTCallback implements Callback {

    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            Context context = HowAreYouApp.getAppContext();
            for (String settingName : com.aware.plugin.howareyou.Settings.SETTINGS_ARRAY)
            {
                fillWorkingMemory(workingMemory, settingName, context);
            }
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }

    private void fillWorkingMemory(WorkingMemory workingMemory, String attributeName, Context context)
            throws AttributeNotRegisteredException, NotInTheDomainException {
        final String HOWAREYOU_PREFIX = "howareyou_";

        double value = Aware.getSetting(context, attributeName).equals("true") ? 1.0 : 0.0;
        Log.d("FILIP", "1: " + HOWAREYOU_PREFIX + attributeName + ", 2: " + value);
        workingMemory.setAttributeValue(HOWAREYOU_PREFIX + attributeName, new SimpleNumeric(value));
    }
}
