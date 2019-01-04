package com.aware.plugin.howareyou.photo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.aware.Aware;
import com.aware.plugin.howareyou.PluginActions;
import com.aware.plugin.howareyou.R;
import com.aware.plugin.howareyou.question.Question_Emoji;

import static com.aware.plugin.howareyou.Settings.STATUS_PHOTO;
import static com.aware.plugin.howareyou.Settings.STATUS_PHOTO_NOTIFICATION;

public class PhotoNotificationDisplayService extends Service {
    public PhotoNotificationDisplayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean cameraUsageEnabled = sharedPreferences.getBoolean(STATUS_PHOTO, true);
        boolean photoNotificationEnabled = sharedPreferences.getBoolean(STATUS_PHOTO_NOTIFICATION, true);

        boolean launchNotification = cameraUsageEnabled && photoNotificationEnabled;
        showPhotoNotification(launchNotification);
        return START_NOT_STICKY;
    }

    public void showPhotoNotification(boolean launchNotification) {
        if (launchNotification) {
            Notification notification = buildNotification();
            startForeground(Aware.AWARE_FOREGROUND_SERVICE, notification);
        } else {
            stopForeground(true);
        }
    }

    private Notification buildNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Aware.AWARE_NOTIFICATION_ID);
        mBuilder.setSmallIcon(R.drawable.camera_notification_icon);
        mBuilder.setContentTitle(getApplicationContext().getResources().getString(R.string.photo_notification_text_line1));
        mBuilder.setContentText(getApplicationContext().getResources().getString(R.string.photo_notification_text_line2));
        mBuilder.setOngoing(true);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MIN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mBuilder.setChannelId(Aware.AWARE_NOTIFICATION_ID);
        return mBuilder.build();

    }
}
