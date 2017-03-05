package com.tastybug.timetracker.extensions.trackingplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.extensions.trackingplayer.internal.NotificationModel;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.task.project.config.ProjectConfiguredEvent;
import com.tastybug.timetracker.task.project.delete.ProjectDeletedEvent;
import com.tastybug.timetracker.task.tracking.checkin.CheckInEvent;
import com.tastybug.timetracker.task.tracking.checkout.CheckOutEvent;
import com.tastybug.timetracker.task.tracking.modify.ModifiedTrackingRecordEvent;

/**
 * This long running service manages the Tracking Player notification regarding:
 * * application startup: is the player to be displayed?
 * * otto events (starting and stopping of trackings)
 */
public class LifecycleService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);

        //
        new TrackingPlayer(this).showSomeProjectOrHide();
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
    public void handleCheckIn(CheckInEvent event) {
        // paused projects can be restarted from within the app -> these have to be removed from the
        // list of paused projects manually
        new NotificationModel(this).removePausedProject(event.getTrackingRecord().getProjectUuid());
        new TrackingPlayer(this).showProject(event.getTrackingRecord().getProjectUuid());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckOut(CheckOutEvent event) {
        handleProjectStopped(event.getTrackingRecord().getProjectUuid());
    }

    private void handleProjectStopped(String projectUuid) {
        TrackingPlayer player = new TrackingPlayer(this);

        if (new NotificationModel(this).isProjectPaused(projectUuid)) {
            player.showProject(projectUuid);
        } else {
            player.showSomeProjectOrHide();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordModified(ModifiedTrackingRecordEvent event) {
        if (event.wasStopped()) {
            handleProjectStopped(event.getTrackingRecord().getProjectUuid());
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectDeleted(ProjectDeletedEvent event) {
        removeProjectFromPlayer(event.getProjectUuid());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectModified(ProjectConfiguredEvent event) {
        Project project = new ProjectDAO(getApplicationContext()).get(event.getProjectUuid()).get();
        if (project.isClosed()) {
            removeProjectFromPlayer(event.getProjectUuid());
        }
    }

    private void removeProjectFromPlayer(String projectUuid) {
        new NotificationModel(this).removePausedProject(projectUuid);
        // we cannot be sure whether the deleted project is being displayed
        // so just to be in the clear we revalidate the TrackingPlayer
        new TrackingPlayer(this).showSomeProjectOrHide();
    }
}
