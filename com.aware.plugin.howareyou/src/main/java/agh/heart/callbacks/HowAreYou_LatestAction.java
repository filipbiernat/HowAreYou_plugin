package agh.heart.callbacks;

import android.util.Log;

import com.aware.plugin.howareyou.HowAreYouApp;

import heart.Callback;
import heart.WorkingMemory;
import heart.alsvfd.SimpleSymbolic;
import heart.exceptions.AttributeNotRegisteredException;
import heart.exceptions.NotInTheDomainException;
import heart.xtt.Attribute;

public class HowAreYou_LatestAction extends HeaRTCallback implements Callback {

    private static final String LATEST_ACTION = "howareyou_latest_action";
    @Override
    public void execute(Attribute attribute, WorkingMemory workingMemory) {
        try {
            String latestPluginAction = HowAreYouApp.getLatestPluginAction().getAction().toLowerCase();
            Log.d(TAG, "Callback for latestPluginAction. Status: " + latestPluginAction);

            workingMemory.setAttributeValue(LATEST_ACTION, new SimpleSymbolic(latestPluginAction));
        } catch (AttributeNotRegisteredException | NotInTheDomainException e) {
            e.printStackTrace();
        }
    }
}
