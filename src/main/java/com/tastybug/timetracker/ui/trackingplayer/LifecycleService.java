package com.tastybug.timetracker.ui.trackingplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.task.project.ProjectDeletedEvent;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;

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
    public void handleTrackingKickStarted(KickStartedTrackingRecordEvent event) {
        // paused projects can be restarted from within the app -> these have to be removed from the
        // list of paused projects manually
        new TrackingPlayerModel(this).removePausedProject(event.getTrackingRecord().getProjectUuid());
        new TrackingPlayer(this).showProject(event.getTrackingRecord().getProjectUuid());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        String projectUuid = event.getTrackingRecord().getProjectUuid();
        TrackingPlayer player = new TrackingPlayer(this);

        if (new TrackingPlayerModel(this).isProjectPaused(projectUuid)) {
            player.showProject(projectUuid);
        } else {
            player.showSomeProjectOrHide();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectDeleted(ProjectDeletedEvent event) {
        new TrackingPlayerModel(this).removePausedProject(event.getProjectUuid());
        // we cannot be sure whether the deleted project is being displayed
        // so just to be in the clear we revalidate the TrackingPlayer
        new TrackingPlayer(this).showSomeProjectOrHide();
    }
}
