package com.aware.plugin.howareyou.plugin;

public class LatestPluginAction {
    private String latestPluginAction = com.aware.plugin.howareyou.PluginActions.ACTION_START_QUESTION_COLOR;

    public String getAction() {
        return latestPluginAction;
    }

    public void setAction(String latestPluginAction) {
        this.latestPluginAction = latestPluginAction;
    }
}
