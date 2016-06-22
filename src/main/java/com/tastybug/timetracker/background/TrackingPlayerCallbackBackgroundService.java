package com.tastybug.timetracker.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.task.OttoEvent;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;

public class TrackingPlayerCallbackBackgroundService extends IntentService {

    private static final String TAG = TrackingPlayerCallbackBackgroundService.class.getSimpleName();

    public static final String OPERATION = "OPERATION";
    public static final String SWITCH = "SWITCH";
    public static final String STOP = "STOP";
    public static final String PROJECT_UUID = "PROJECT_UUID";

    private OttoProvider ottoProvider = new OttoProvider();


    public TrackingPlayerCallbackBackgroundService() {
        super(TrackingPlayerCallbackBackgroundService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Preconditions.checkNotNull(intent.getExtras().getString(OPERATION));
        if (STOP.equals(intent.getExtras().getString(OPERATION))) {
            stopTracking(intent.getExtras().getString(PROJECT_UUID));
        } else if (SWITCH.equals(intent.getExtras().getString(OPERATION))) {
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
