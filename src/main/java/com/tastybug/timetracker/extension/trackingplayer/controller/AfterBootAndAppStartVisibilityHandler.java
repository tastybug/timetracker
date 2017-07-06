package com.tastybug.timetracker.extension.trackingplayer.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.extension.trackingplayer.ui.NotificationManager;
import com.tastybug.timetracker.infrastructure.util.ConditionalLog;

public class AfterBootAndAppStartVisibilityHandler extends BroadcastReceiver {

    private static final String TAG = AfterBootAndAppStartVisibilityHandler.class.getSimpleName();
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String APP_STARTED = "com.tastybug.timetracker.APP_START";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isBootCompleted(intent)) {
            ConditionalLog.logInfo(TAG, "Device booted; checking tracking player visibility.");
            showTrackingPlayerIfNecessary(context);
        } else if (isAppStarted(intent)) {
            ConditionalLog.logInfo(TAG, "App started; checking tracking player visibility.");
            showTrackingPlayerIfNecessary(context);
        }
    }

    private boolean isAppStarted(Intent intent) {
        return APP_STARTED.equals(intent.getAction());
    }

    private boolean isBootCompleted(Intent intent) {
        return BOOT_COMPLETED.equals(intent.getAction());
    }

    private void showTrackingPlayerIfNecessary(Context context) {
        if (hasOngoingProjects(context)) {
            new NotificationManager(context).showSomeProjectOrHide();
        }
    }

    private boolean hasOngoingProjects(Context context) {
        return new NotificationModel(context).getOngoingProjects().size() > 0;
    }
}
