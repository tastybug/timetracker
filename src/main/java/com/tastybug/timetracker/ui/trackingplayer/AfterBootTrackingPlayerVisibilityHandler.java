package com.tastybug.timetracker.ui.trackingplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.ui.trackingplayer.internal.NotificationModel;
import com.tastybug.timetracker.util.ConditionalLog;

public class AfterBootTrackingPlayerVisibilityHandler extends BroadcastReceiver {

    private static final String TAG = AfterBootTrackingPlayerVisibilityHandler.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            ConditionalLog.logInfo(TAG, "Boot up completed, checking tracking player visibility.");
            if (hasOngoingProjects(context)) {
                Intent pushIntent = new Intent(context, LifecycleService.class);
                context.startService(pushIntent);
            }
        }
    }

    private boolean hasOngoingProjects(Context context) {
        return new NotificationModel(context).getOngoingProjects().size() > 0;
    }
}