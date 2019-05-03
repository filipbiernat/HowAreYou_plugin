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

public class Communication extends HeaRTCallback implements Callback {

    private static String IS_CALLING = "is_calling";
    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            fillWorkingMemory(workingMemory);
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }

    private void fillWorkingMemory(WorkingMemory workingMemory)
            throws AttributeNotRegisteredException, NotInTheDomainException {
        double value = HowAreYouApp.getCommunicationObserver().isCalling() ? 1.0 : 0.0;
        workingMemory.setAttributeValue(IS_CALLING, new SimpleNumeric(value));
    }
}
