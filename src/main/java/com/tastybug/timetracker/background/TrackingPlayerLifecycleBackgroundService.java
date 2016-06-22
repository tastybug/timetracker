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
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.activity.ProjectDetailsActivity;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TrackingPlayerLifecycleBackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);

        //
        startTrackingPlayerIfNecessary();
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
        startTrackingPlayerForProjectAndRecord(project, event.getTrackingRecord());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        if (getRunningProjects().isEmpty()) {
            stopTrackingPlayer();
        } else {
            ArrayList<Project> runningProjects = getRunningProjects();
            Project nextProject = getNextProject(runningProjects, event.getTrackingRecord().getProjectUuid());
            startTrackingPlayerForProjectAndRecord(nextProject, new TrackingRecordDAO(getApplicationContext()).getRunning(nextProject.getUuid()).get());
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleSwitchProjectRequest(TrackingPlayerCallbackBackgroundService.SwitchProjectEvent event) {
        ArrayList<Project> runningProjects = getRunningProjects();
        Project nextProject = getNextProject(runningProjects, event.getCurrentProjectUuid());
        startTrackingPlayerForProjectAndRecord(nextProject, new TrackingRecordDAO(getApplicationContext()).getRunning(nextProject.getUuid()).get());
    }

    private void startTrackingPlayerIfNecessary() {
        if (!getRunningProjects().isEmpty()) {
            ArrayList<Project> runningProjects = getRunningProjects();
            startTrackingPlayerForProjectAndRecord(runningProjects.get(0),
                    new TrackingRecordDAO(getApplicationContext()).getRunning(runningProjects.get(0).getUuid()).get());
        }
    }

    private void startTrackingPlayerForProjectAndRecord(Project project, TrackingRecord trackingRecord) {
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
        Notification.Builder notificatonBuilder = new Notification.Builder(this)
                .setContentTitle(project.getTitle())
                .setContentText(getString(R.string.tracking_player_tracking_since_X,
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT).format(trackingRecord.getStart().get())))
                .setContentIntent(createOpenProjectDetailsPendingIntentForProjectUuid(project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop_tracking,
                        getString(R.string.tracking_player_stop_button),
                        createStopPendingIntentForProjectWithUuid(project));

        if (getRunningProjects().size() > 1) {
            notificatonBuilder.addAction(R.drawable.ic_switch_project,
                    getString(R.string.tracking_player_switch_project),
                    createSwitchFromCurrentProjectIntent(project));
        }

        return notificatonBuilder.build();

    }

    private PendingIntent createOpenProjectDetailsPendingIntentForProjectUuid(Project project) {
        Intent intent = new Intent(this, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createStopPendingIntentForProjectWithUuid(Project project) {
        Intent intent = new Intent(this, TrackingPlayerCallbackBackgroundService.class)
                .putExtra(TrackingPlayerCallbackBackgroundService.PROJECT_UUID, project.getUuid())
                .putExtra(TrackingPlayerCallbackBackgroundService.OPERATION, TrackingPlayerCallbackBackgroundService.STOP);
        return PendingIntent.getService(this, TrackingPlayerCallbackBackgroundService.STOP.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createSwitchFromCurrentProjectIntent(Project project) {
        Intent intent = new Intent(this, TrackingPlayerCallbackBackgroundService.class)
                .putExtra(TrackingPlayerCallbackBackgroundService.PROJECT_UUID, project.getUuid())
                .putExtra(TrackingPlayerCallbackBackgroundService.OPERATION, TrackingPlayerCallbackBackgroundService.SWITCH);
        return PendingIntent.getService(this, TrackingPlayerCallbackBackgroundService.SWITCH.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private ArrayList<Project> getRunningProjects() {
        ProjectDAO projectDAO = new ProjectDAO(getApplicationContext());
        ArrayList<TrackingRecord> runningRecords = new TrackingRecordDAO(getApplicationContext()).getRunning();
        ArrayList<Project> runningProjects = new ArrayList<>();
        for (TrackingRecord record : runningRecords) {
            runningProjects.add(projectDAO.get(record.getProjectUuid()).get());
        }

        Collections.sort(runningProjects);
        return runningProjects;
    }

    private Project getNextProject(ArrayList<Project> projects, String previousProjectUuid) {
        for (Iterator<Project> i = projects.iterator();i.hasNext();) {
            if (i.next().getUuid().equals(previousProjectUuid)) {
                return i.hasNext() ? i.next() : projects.get(0);
            }
        }
        // if the previous project is not in the list, just return the very first entry
        return projects.get(0);
    }
}
