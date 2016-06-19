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

public class NotificationManagerBackgroundService extends Service {

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
        Intent intent = new Intent(this, ProjectDetailsActivity.class);
        intent.putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification noti = new Notification.Builder(this)
                .setWhen(System.currentTimeMillis() + 2000)
                .setContentTitle(project.getTitle())
                .setContentText("Content Text hier")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setOngoing(false) //TODO needs to be true
                .addAction(R.drawable.ic_modify, "Stop&Log", pendingIntent)
                .addAction(R.drawable.ic_stop_tracking, "Stop", null)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);

        notificationManager.notify(0, noti);
    }

    private void stopNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);

        notificationManager.cancel(0);
    }

}
