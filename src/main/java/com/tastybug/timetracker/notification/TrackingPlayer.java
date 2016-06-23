package com.tastybug.timetracker.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.background.TrackingPlayerCallbackBackgroundService;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.activity.ProjectDetailsActivity;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class TrackingPlayer {

    // the ID of the tracking player as the OS' notfication manager requires it
    private static final int TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID = 1;

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
                        SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.SHORT).format(trackingRecord.getStart().get())))
                .setContentIntent(createOpenProjectDetailsPendingIntentForProjectUuid(context, project))
                .setSmallIcon(R.drawable.ic_notification_ongoing)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop_tracking,
                        context.getString(R.string.tracking_player_stop_button),
                        createStopPendingIntentForProjectWithUuid(context, project));

        if (getRunningProjects(context).size() > 1) {
            notificationBuilder.addAction(R.drawable.ic_switch_project,
                    context.getString(R.string.tracking_player_switch_project),
                    createSwitchFromCurrentProjectIntent(context, project));
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
        ProjectDAO projectDAO = new ProjectDAO(context);
        ArrayList<TrackingRecord> runningRecords = new TrackingRecordDAO(context).getRunning();
        ArrayList<Project> runningProjects = new ArrayList<>();
        for (TrackingRecord record : runningRecords) {
            runningProjects.add(projectDAO.get(record.getProjectUuid()).get());
        }

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

    private PendingIntent createOpenProjectDetailsPendingIntentForProjectUuid(Context context, Project project) {
        Intent intent = new Intent(context, ProjectDetailsActivity.class)
                .putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createStopPendingIntentForProjectWithUuid(Context context, Project project) {
        Intent intent = new Intent(context, TrackingPlayerCallbackBackgroundService.class)
                .putExtra(TrackingPlayerCallbackBackgroundService.PROJECT_UUID, project.getUuid())
                .putExtra(TrackingPlayerCallbackBackgroundService.OPERATION, TrackingPlayerCallbackBackgroundService.STOP_TRACKING_PROJECT);
        return PendingIntent.getService(context, TrackingPlayerCallbackBackgroundService.STOP_TRACKING_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createSwitchFromCurrentProjectIntent(Context context, Project project) {
        Intent intent = new Intent(context, TrackingPlayerCallbackBackgroundService.class)
                .putExtra(TrackingPlayerCallbackBackgroundService.PROJECT_UUID, project.getUuid())
                .putExtra(TrackingPlayerCallbackBackgroundService.OPERATION, TrackingPlayerCallbackBackgroundService.CYCLE_TO_NEXT_PROJECT);
        return PendingIntent.getService(context, TrackingPlayerCallbackBackgroundService.CYCLE_TO_NEXT_PROJECT.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
