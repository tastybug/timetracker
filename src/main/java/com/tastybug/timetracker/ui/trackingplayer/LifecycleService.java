package com.tastybug.timetracker.ui.trackingplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
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
        new TrackingPlayer(this).revalidateVisibility();
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
        new TrackingPlayer(this).revalidateVisibility();
    }
}
