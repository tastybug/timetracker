package com.tastybug.timetracker.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;

public class NotificationFacadeBackgroundService extends IntentService {

    private static final String TAG = NotificationFacadeBackgroundService.class.getSimpleName();

    public static final String OPERATION = "OPERATION";
    public static final String START = "START";
    public static final String STOP = "STOP";
    public static final String PROJECT_UUID = "PROJECT_UUID";

    public NotificationFacadeBackgroundService() {
        super(NotificationFacadeBackgroundService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        stopTracking(intent.getExtras().getString(PROJECT_UUID));
    }

    private void stopTracking(String projectUuid) {
        Log.i(TAG, "Stopping tracking for project " + projectUuid);
        KickStopTrackingRecordTask.aTask(getApplicationContext()).withProjectUuid(projectUuid).execute();
    }
}
