package com.tastybug.timetracker.extension.trackingplayer.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.project.ProjectChangeIntent;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;
import com.tastybug.timetracker.extension.trackingplayer.ui.NotificationManager;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class DomainEventBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo(getClass().getSimpleName(), "Received Event %s", intent.getAction());

        if (ProjectChangeIntent.ACTION.equals(intent.getAction())) {
            ProjectChangeIntent pci = new ProjectChangeIntent(intent);
            switch(pci.getChangeType()) {
                case DELETE:
                    handleProjectDeleted(context, pci.getProjectUuid());
                    return;
                case UPDATE:
                    handleProjectUpdate(context, pci.getProjectUuid());
            }

        } else if (TrackingRecordChangeIntent.ACTION.equals(intent.getAction())) {
            TrackingRecordChangeIntent trci = new TrackingRecordChangeIntent(intent);
            switch (trci.getChangeType()) {
                case CHECKIN:
                    handleCheckIn(context, trci.getProjectUuid());
                    return;
                case CHECKOUT:
                    handleCheckOut(context, trci.getProjectUuid());
                    return;
                case UPDATE:
                    handleTrackingRecordUpdate(context, trci.getProjectUuid(), trci.wasStopped());
            }
        }
    }

    private void handleCheckIn(Context context, String projectUuid) {
        // paused projects can be restarted from within the app -> these have to be removed from the
        // list of paused projects manually
        new NotificationModel(context).removePausedProject(projectUuid);
        new NotificationManager(context).showProject(projectUuid);
    }

    private void handleCheckOut(Context context, String projectUuid) {
        handleProjectStopped(context, projectUuid);
    }

    private void handleTrackingRecordUpdate(Context context, String projectUuid, boolean wasStopped) {
        if (wasStopped) {
            handleProjectStopped(context, projectUuid);
        } else if (isProjectRunning(context, projectUuid)) {
            // the project is ongoing and there has been a change ..
            // so lets update the tracking player
            new NotificationManager(context).showProject(projectUuid);
        }
    }

    public void handleProjectDeleted(Context context, String projectUuid) {
        removeProjectFromPlayer(context, projectUuid);
    }

    public void handleProjectUpdate(Context context, String projectUuid) {
        Project project = new ProjectDAO(context).get(projectUuid).get();
        if (project.isClosed()) {
            removeProjectFromPlayer(context, projectUuid);
        }
    }

    private void removeProjectFromPlayer(Context context, String projectUuid) {
        new NotificationModel(context).removePausedProject(projectUuid);
        // we cannot be sure whether the deleted project is being displayed
        // so just to be in the clear we revalidate the TrackingPlayer
        new NotificationManager(context).showSomeProjectOrHide();
    }

    private void handleProjectStopped(Context context, String projectUuid) {
        NotificationManager player = new NotificationManager(context);

        if (new NotificationModel(context).isProjectPaused(projectUuid)) {
            player.showProject(projectUuid);
        } else {
            player.showSomeProjectOrHide();
        }
    }

    private boolean isProjectRunning(Context context, String projectUuid) {
        return new TrackingRecordDAO(context).getRunning(projectUuid).isPresent();
    }
}
