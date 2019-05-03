package com.aware.plugin.howareyou.plugin;

import android.content.ContentValues;
import com.aware.Communication;

public class CommunicationObserver implements Communication.AWARESensorObserver {

    private boolean calling = false;
    @Override
    public void onCall(ContentValues data) {

    }

    @Override
    public void onMessage(ContentValues data) {
    }

    @Override
    public void onRinging(String number) {
        calling = true;
    }

    @Override
    public void onBusy(String number) {
        calling = true;
    }

    @Override
    public void onFree(String number) {
        calling = false;
    }

    public boolean isCalling(){
        return calling;
    }
}
