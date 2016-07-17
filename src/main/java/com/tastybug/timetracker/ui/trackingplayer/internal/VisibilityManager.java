package com.tastybug.timetracker.ui.trackingplayer.internal;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class VisibilityManager {

    // the ID of the tracking player as the OS' notification manager requires it
    private static final int TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID
            = "TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID".hashCode();

    private Context context;

    public VisibilityManager(Context context) {
        this.context = context;
    }

    public void showTrackingPlayer(Notification notification) {
        getSystemNotificationManager().notify(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID, notification);
    }

    public void hideTrackingPlayer() {
        getSystemNotificationManager().cancel(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID);
    }

    private NotificationManager getSystemNotificationManager() {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }
}
