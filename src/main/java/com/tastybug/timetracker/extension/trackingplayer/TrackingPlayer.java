package com.tastybug.timetracker.extension.trackingplayer;

import android.app.Notification;
import android.content.Context;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.extension.trackingplayer.internal.NotificationBuilder;
import com.tastybug.timetracker.extension.trackingplayer.internal.NotificationModel;
import com.tastybug.timetracker.extension.trackingplayer.internal.VisibilityManager;

import java.util.ArrayList;

public class TrackingPlayer {


    private NotificationModel model;
    private VisibilityManager visibilityManager;
    private NotificationBuilder builder;

    public TrackingPlayer(Context context) {
        model = new NotificationModel(context);
        visibilityManager = new VisibilityManager(context);
        builder = new NotificationBuilder(context);
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
        visibilityManager.showTrackingPlayer(notification);
    }

    public void cycleProject(String currentProjectUuid) {
        Project nextProject = model.getNextProject(currentProjectUuid);
        showProject(nextProject.getUuid());
    }

    public void showSomeProjectOrHide() {
        ArrayList<Project> runningAndPausedProjectList = model.getOngoingProjects();
        if (runningAndPausedProjectList.isEmpty()) {
            visibilityManager.hideTrackingPlayer();
        } else {
            showProject(runningAndPausedProjectList.get(0).getUuid());
        }
    }

}
