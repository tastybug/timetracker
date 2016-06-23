package com.tastybug.timetracker.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.task.OttoEvent;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;

/**
 * This background service only lives as long as the request!
 */
public class TrackingPlayerCallbackBackgroundService extends IntentService {

    private static final String TAG = TrackingPlayerCallbackBackgroundService.class.getSimpleName();

    public static final String OPERATION             = "OPERATION";
    public static final String CYCLE_TO_NEXT_PROJECT = "CYCLE_TO_NEXT_PROJECT";
    public static final String STOP_TRACKING_PROJECT = "STOP_TRACKING_PROJECT";
    public static final String PROJECT_UUID          = "PROJECT_UUID";

    private OttoProvider ottoProvider = new OttoProvider();


    public TrackingPlayerCallbackBackgroundService() {
        super(TrackingPlayerCallbackBackgroundService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Preconditions.checkNotNull(intent.getExtras().getString(OPERATION));
        if (STOP_TRACKING_PROJECT.equals(intent.getExtras().getString(OPERATION))) {
            stopTracking(intent.getExtras().getString(PROJECT_UUID));
        } else if (CYCLE_TO_NEXT_PROJECT.equals(intent.getExtras().getString(OPERATION))) {
            publishSwitchProjectInTrackingPlayer(intent.getExtras().getString(PROJECT_UUID));
        } else {
            Log.wtf(TAG, "Unexpected intent: " + intent);
        }
    }

    private void stopTracking(String projectUuid) {
        Log.i(TAG, "Stopping tracking for project " + projectUuid);
        KickStopTrackingRecordTask.aTask(getApplicationContext()).withProjectUuid(projectUuid).execute();
    }

    private void publishSwitchProjectInTrackingPlayer(String currentProjectUuid) {
        Log.i(TAG, "Cycling to next project coming from " + currentProjectUuid);
        ottoProvider.getSharedBus().post(new SwitchProjectEvent(currentProjectUuid));
    }

    public static class SwitchProjectEvent implements OttoEvent {

        private String currentProjectUuid;

        public SwitchProjectEvent(String currentProjectUuid) {
            this.currentProjectUuid = currentProjectUuid;
        }

        public String getCurrentProjectUuid() {
            return currentProjectUuid;
        }
    }
}
