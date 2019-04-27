package agh.heart.observers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import agh.heart.HeaRTService;


public abstract class Observer extends BroadcastReceiver {

    private static final String TAG = "AWARE::HowAreYou::HeaRT";
    private static final int MAX_JOB_TIMEOUT = 10;

    FirebaseJobDispatcher dispatcher = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Scheduling job for observer " + getObserverName() + ": " + intent.getAction());

        setupDispatcher(context);
        dispatcher.schedule(prepareJob());
    }

    private Job prepareJob() {
        return dispatcher.newJobBuilder()
                .setService(HeaRTService.class)
                .setTag("HeaRTService")
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT) // will not trigger after phone reboot
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setReplaceCurrent(true) // will trigger once for many schedules
                .setTrigger(Trigger.executionWindow(0, MAX_JOB_TIMEOUT))
                .build();
    }

    private void setupDispatcher(Context context) {
        if (dispatcher == null) {
            dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        }
    }

    public void register(Context context) {
        Log.d(TAG, "Registering observer: " + getObserverName());
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    public abstract String getObserverName();
}
