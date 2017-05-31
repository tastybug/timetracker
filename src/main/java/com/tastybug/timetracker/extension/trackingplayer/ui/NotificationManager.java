package com.tastybug.timetracker.extension.trackingplayer.ui;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.trackingplayer.controller.NotificationModel;

import java.util.ArrayList;

public class NotificationManager {

    // the ID of the tracking player as the OS' notification manager requires it
    private static final int TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID
            = "TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID".hashCode();

    private NotificationModel model;
    private NotificationBuilder builder;
    private android.app.NotificationManager notificationManager;

    public NotificationManager(Context context) {
        model = new NotificationModel(context);
        builder = new NotificationBuilder(context);
        notificationManager = (android.app.NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    public void showProject(String projectUuid) {
        Notification notification;
        Project project = model.getProject(projectUuid);
        if (model.isProjectPaused(projectUuid)) {
            if (model.getOngoingProjects().size() > 1) {
                notification = builder.forProject(project).withSwitchFromCurrentProject(project).showNotificationForPaused(project).build();
            } else {
                notification = builder.forProject(project).showNotificationForPaused(project).build();
            }
        } else {
            if (model.getOngoingProjects().size() > 1) {
                notification = builder.forProject(project).withSwitchFromCurrentProject(project).forRunningProject(project, model.getRunningTrackingRecord(projectUuid)).build();
            } else {
                notification = builder.forProject(project).forRunningProject(project, model.getRunningTrackingRecord(projectUuid)).build();
            }
        }
        showTrackingPlayer(notification);
    }

    public void cycleProject(String currentProjectUuid) {
        Project nextProject = model.getNextProject(currentProjectUuid);
        showProject(nextProject.getUuid());
    }

    public void showSomeProjectOrHide() {
        ArrayList<Project> runningAndPausedProjectList = model.getOngoingProjects();
        if (runningAndPausedProjectList.isEmpty()) {
            hideTrackingPlayer();
        } else {
            showProject(runningAndPausedProjectList.get(0).getUuid());
        }
    }

    private void showTrackingPlayer(Notification notification) {
        notificationManager.notify(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID, notification);
    }

    private void hideTrackingPlayer() {
        notificationManager.cancel(TRACKING_PLAYER_INTERNAL_NOTIFICATION_ID);
    }
}
