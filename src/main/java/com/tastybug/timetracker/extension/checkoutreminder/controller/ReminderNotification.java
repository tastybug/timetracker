package com.tastybug.timetracker.extension.checkoutreminder.controller;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.infrastructure.util.DefaultLocaleDateFormatter;

import static com.tastybug.timetracker.extension.trackingplayer.internal.CallbackIntentFactory.createOpenProjectDetailsActivityIntent;
import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

class ReminderNotification {

    private static final String TAG = ReminderNotification.class.getSimpleName();

    private Context context;
    private Notification.Builder notificationBuilder;

    ReminderNotification(Context context) {
        this.context = context;
        this.notificationBuilder = new Notification.Builder(context);
    }

    void showWarning(TrackingRecord trackingRecord) {
        logInfo(TAG, "Issuing checkout warning for: " + trackingRecord.getUuid());
        Project project = new ProjectDAO(context).get(trackingRecord.getProjectUuid()).get();

        Notification notification = notificationBuilder.setContentTitle(getNotificationTitle())
                .setContentText(getNotificationText(trackingRecord, project))
                .setLargeIcon(getLargeIcon())
                .setSmallIcon(getSmallIcon())
                .setContentIntent(createOpenProjectDetailsActivityIntent(context, project))
                .build();

        getNotificationManager()
                .notify("CHECKOUT_REMINDER_INTERNAL_NOTIFICATION_ID".hashCode(), notification);
    }

    private int getSmallIcon() {
        return R.drawable.ic_notification_warning;
    }

    private Bitmap getLargeIcon() {
        return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    @NonNull
    private String getNotificationTitle() {
        return context.getString(R.string.checkout_reminder_notification_title);
    }

    @NonNull
    private String getNotificationText(TrackingRecord trackingRecord, Project project) {
        return context.getString(
                R.string.project_X_is_tracking_since_Y,
                project.getTitle(),
                DefaultLocaleDateFormatter.dateTime().format(trackingRecord.getStart().get()));
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }
}
