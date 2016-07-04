package com.tastybug.timetracker.trackingplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TrackingPlayer {

    // the ID of the tracking player as the OS' notfication manager requires it
    private static final int TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID = 1;

    private CallbackIntentFactory intentFactory = new CallbackIntentFactory();

    public TrackingPlayer() {}

    public void showProject(Context context, TrackingRecord trackingRecord) {
        Project project = new ProjectDAO(context).get(trackingRecord.getProjectUuid()).get();
        showProject(context, project, trackingRecord);
    }

    private void showProject(Context context, Project project, TrackingRecord runningTrackingRecord) {
        showNotification(context, getNotificationBuilderForRecord(context, project, runningTrackingRecord).build());
    }

    public void showNextProject(Context context, String currentProjectUuid) {
        ArrayList<Project> runningProjects = getRunningProjects(context);
        Project nextProject = getNextRunningProject(runningProjects, currentProjectUuid);
        showProject(context, nextProject, new TrackingRecordDAO(context).getRunning(nextProject.getUuid()).get());
    }

    public void revalidateVisibility(Context context) {
        if (getRunningProjects(context).isEmpty()) {
            dismissNotification(context);
        } else {
            ArrayList<Project> runningProjects = getRunningProjects(context);
            showProject(context, runningProjects.get(0), new TrackingRecordDAO(context).getRunning(runningProjects.get(0).getUuid()).get());
        }
    }

    private Notification.Builder getNotificationBuilderForRecord(Context context, Project project, TrackingRecord trackingRecord) {
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(project.getTitle())
                .setContentText(context.getString(R.string.tracking_player_tracking_since_X,
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT).format(trackingRecord.getStart().get())))
                .setContentIntent(intentFactory.createOpenProjectDetailsActivityIntent(context, project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_stop_button),
                        intentFactory.createStopTrackingIntent(context, project))
                .addAction(R.drawable.ic_pause_tracking,
                        context.getString(R.string.tracking_player_pause_button),
                        intentFactory.createStopTrackingIntent(context, project));

        if (getRunningProjects(context).size() > 1) {
            notificationBuilder.addAction(R.drawable.ic_switch_project,
                    context.getString(R.string.tracking_player_switch_project),
                    intentFactory.createCycleProjectIntent(context, project));
        }
        return notificationBuilder;
    }

    private Notification.Builder getNotificationBuilderForPausedProject(Context context, Project project) {
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentTitle(project.getTitle())
                .setContentText(context.getString(R.string.paused_project))
                .setContentIntent(intentFactory.createOpenProjectDetailsActivityIntent(context, project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_start_tracking,
                        context.getString(R.string.tracking_player_resume_button),
                        null)
                .addAction(R.drawable.ic_start_tracking,
                        context.getString(R.string.tracking_player_dismiss_paused_button),
                        null);

        if (getRunningProjects(context).size() > 1) {
            notificationBuilder.addAction(R.drawable.ic_switch_project,
                    context.getString(R.string.tracking_player_switch_project),
                    intentFactory.createCycleProjectIntent(context, project));
        }
        return notificationBuilder;
    }

    private void showNotification(Context context, Notification notification) {
        getSystemNotificationManager(context).notify(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID, notification);
    }

    private void dismissNotification(Context context) {
        getSystemNotificationManager(context).cancel(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID);
    }

    private NotificationManager getSystemNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    private ArrayList<Project> getRunningProjects(Context context) {
        ArrayList<Project> runningProjects = new TrackingPlayerModel(context).getRunningProjectList();
        Collections.sort(runningProjects);
        return runningProjects;
    }

    private Project getNextRunningProject(ArrayList<Project> projects, String previousProjectUuid) {
        for (Iterator<Project> i = projects.iterator(); i.hasNext();) {
            if (i.next().getUuid().equals(previousProjectUuid)) {
                return i.hasNext() ? i.next() : projects.get(0);
            }
        }
        // if the previous project is not in the list, just return the very first entry
        return projects.get(0);
    }
}
