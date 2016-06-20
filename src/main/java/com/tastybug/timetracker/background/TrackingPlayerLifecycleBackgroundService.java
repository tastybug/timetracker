package com.tastybug.timetracker.background;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.gui.activity.ProjectDetailsActivity;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;

public class TrackingPlayerLifecycleBackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingKickStarted(KickStartedTrackingRecordEvent event) {
        startNotification(getProject(event.getTrackingRecord().getProjectUuid()));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        stopNotification();
    }

    private Project getProject(String uuid) {
        return new ProjectDAO(this).get(uuid).get();
    }

    private void startNotification(Project project) {

        Notification noti = new Notification.Builder(this)
                .setContentTitle(project.getTitle())
                .setContentText("Content Text hier")
                .setContentIntent(getOpenProjectDetailsPendingIntentForProjectUuid(project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop_tracking,
                        getString(R.string.tracking_player_stop_button),
                        getStopPendingIntentForProjectWithUuid(project))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);

        notificationManager.cancel(0);
        notificationManager.notify(0, noti);
    }

    private void stopNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);

        notificationManager.cancel(0);
    }

    private PendingIntent getOpenProjectDetailsPendingIntentForProjectUuid(Project project) {
        Intent intent = new Intent(this, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getStopPendingIntentForProjectWithUuid(Project project) {
        Intent intent2 = new Intent(this, TrackingPlayerCallbackBackgroundService.class)
                .putExtra(TrackingPlayerCallbackBackgroundService.PROJECT_UUID, project.getUuid())
                .putExtra(TrackingPlayerCallbackBackgroundService.OPERATION, TrackingPlayerCallbackBackgroundService.STOP);
        return PendingIntent.getService(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
