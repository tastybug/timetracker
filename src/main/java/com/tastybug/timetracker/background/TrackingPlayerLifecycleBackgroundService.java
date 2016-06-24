package com.tastybug.timetracker.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.notification.TrackingPlayer;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;

/**
 * This long running service manages the Tracking Player notification regarding:
 * * application startup: is the player to be displayed?
 * * otto events (starting and stopping of trackings)
 */
public class TrackingPlayerLifecycleBackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);

        //
        new TrackingPlayer().revalidateVisibility(this);
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
        new TrackingPlayer().showProject(this, event.getTrackingRecord());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        new TrackingPlayer().revalidateVisibility(this);
    }
}
