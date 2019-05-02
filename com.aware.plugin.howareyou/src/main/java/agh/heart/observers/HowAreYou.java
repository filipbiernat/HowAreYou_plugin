package agh.heart.observers;

import android.content.Context;
import android.content.IntentFilter;



public class HowAreYou extends Observer {

    @Override
    public void register(Context context) {
        super.register(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.aware.plugin.howareyou.PluginActions.ACTION_ON_FINISHED_QUESTION_COLOR);
        intentFilter.addAction(com.aware.plugin.howareyou.PluginActions.ACTION_ON_FINISHED_QUESTION_EMOJI);
        intentFilter.addAction(com.aware.plugin.howareyou.PluginActions.ACTION_ON_FINISHED_PHOTO_EMOTION_RECOGNITION);
        context.registerReceiver(this, intentFilter);
    }

    public String getObserverName(){
        return "HowAreYou";
    }
}
