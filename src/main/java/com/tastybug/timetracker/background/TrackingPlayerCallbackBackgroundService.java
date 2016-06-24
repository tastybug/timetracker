package com.tastybug.timetracker.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.notification.TrackingPlayer;
import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;

/**
 * This service acts as the callback facade for the Tracking Player notification. Pressing
 * some button on the player will result in a call to this service.
 *
 * This background service only lives as long as the request!
 */
public class TrackingPlayerCallbackBackgroundService extends IntentService {

    private static final String TAG = TrackingPlayerCallbackBackgroundService.class.getSimpleName();

    public static final String OPERATION             = "OPERATION";
    public static final String CYCLE_TO_NEXT_PROJECT = "CYCLE_TO_NEXT_PROJECT";
    public static final String STOP_TRACKING_PROJECT = "STOP_TRACKING_PROJECT";
    public static final String PROJECT_UUID          = "PROJECT_UUID";

    public TrackingPlayerCallbackBackgroundService() {
        super(TrackingPlayerCallbackBackgroundService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Preconditions.checkNotNull(intent.getExtras().getString(OPERATION));
        if (STOP_TRACKING_PROJECT.equals(intent.getExtras().getString(OPERATION))) {
            handleStopTrackingRequested(intent.getExtras().getString(PROJECT_UUID));
        } else if (CYCLE_TO_NEXT_PROJECT.equals(intent.getExtras().getString(OPERATION))) {
            handleCycleProjectRequested(intent.getExtras().getString(PROJECT_UUID));
        } else {
            Log.wtf(TAG, "Unexpected intent: " + intent);
        }
    }

    private void handleStopTrackingRequested(String projectUuid) {
        Log.i(TAG, "Stopping tracking for project " + projectUuid);
        KickStopTrackingRecordTask.aTask(getApplicationContext()).withProjectUuid(projectUuid).execute();
    }

    private void handleCycleProjectRequested(String currentProjectUuid) {
        Log.i(TAG, "Cycling to next project coming from " + currentProjectUuid);
        new TrackingPlayer().showNextProject(this, currentProjectUuid);
    }
}
