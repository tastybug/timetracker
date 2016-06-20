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
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;

import java.text.SimpleDateFormat;

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
        Project project = new ProjectDAO(this).get(event.getTrackingRecord().getProjectUuid()).get();
        startTrackingPlayer(project, event.getTrackingRecord());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        stopTrackingPlayer();
    }

    private void startTrackingPlayer(Project project, TrackingRecord trackingRecord) {
        Notification notification = createTrackingPlayerNotification(project, trackingRecord);
        getSystemNotificationManager().notify(0, notification);
    }

    private void stopTrackingPlayer() {
        getSystemNotificationManager().cancel(0);
    }

    private NotificationManager getSystemNotificationManager() {
        return (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    private Notification createTrackingPlayerNotification(Project project, TrackingRecord trackingRecord) {
        return new Notification.Builder(this)
                .setContentTitle(project.getTitle())
                .setContentText(getString(R.string.tracking_player_tracking_since_X,
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT).format(trackingRecord.getStart().get())))
                .setContentIntent(createOpenProjectDetailsPendingIntentForProjectUuid(project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop_tracking,
                        getString(R.string.tracking_player_stop_button),
                        createStopPendingIntentForProjectWithUuid(project))
                .build();
    }

    private PendingIntent createOpenProjectDetailsPendingIntentForProjectUuid(Project project) {
        Intent intent = new Intent(this, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createStopPendingIntentForProjectWithUuid(Project project) {
        Intent intent2 = new Intent(this, TrackingPlayerCallbackBackgroundService.class)
                .putExtra(TrackingPlayerCallbackBackgroundService.PROJECT_UUID, project.getUuid())
                .putExtra(TrackingPlayerCallbackBackgroundService.OPERATION, TrackingPlayerCallbackBackgroundService.STOP);
        return PendingIntent.getService(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
