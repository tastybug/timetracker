package com.tastybug.timetracker.ui.trackingplayer;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.tracking.KickStartTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;
import com.tastybug.timetracker.ui.trackingrecordmodification.TrackingRecordModificationActivity;

/**
 * This service acts as the callback facade for the Tracking Player notification. Pressing
 * some button on the player will result in a call to this service.
 *
 * This background service only lives as long as the request!
 */
public class CallbackService extends IntentService {

    private static final String TAG = CallbackService.class.getSimpleName();

    protected static final String OPERATION                 = "OPERATION";
    protected static final String CYCLE_TO_NEXT_PROJECT     = "CYCLE_TO_NEXT_PROJECT";
    protected static final String START_TRACKING_PROJECT    = "START_TRACKING_PROJECT";
    protected static final String STOP_TRACKING_PROJECT     = "STOP_TRACKING_PROJECT";
    protected static final String DISMISS_PAUSED_PROJECT    = "DISMISS_PAUSED_PROJECT";
    protected static final String PAUSE_TRACKING_PROJECT    = "PAUSE_TRACKING_PROJECT";
    protected static final String UNPAUSE_TRACKING_PROJECT  = "UNPAUSE_TRACKING_PROJECT";
    protected static final String PROJECT_UUID              = "PROJECT_UUID";

    public CallbackService() {
        super(CallbackService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Preconditions.checkNotNull(intent.getExtras().getString(OPERATION));
        Preconditions.checkNotNull(intent.getExtras().getString(PROJECT_UUID));

        String operation = intent.getExtras().getString(OPERATION);
        String projectUuid = intent.getExtras().getString(PROJECT_UUID);

        if (STOP_TRACKING_PROJECT.equals(operation)) {
            handleStopTrackingRequested(projectUuid);
        } else if (START_TRACKING_PROJECT.equals(operation)) {
            handleStartTrackingRequest(projectUuid);
        } else if (CYCLE_TO_NEXT_PROJECT.equals(operation)) {
            handleCycleProjectRequested(projectUuid);
        } else if (PAUSE_TRACKING_PROJECT.equals(operation)) {
            handlePauseTrackingRequested(projectUuid);
        } else if (UNPAUSE_TRACKING_PROJECT.equals(operation)) {
            handleUnpauseTrackingRequest(projectUuid);
        } else if (DISMISS_PAUSED_PROJECT.equals(operation)) {
            handleDismissPausedProjectRequested(projectUuid);
        } else {
            Log.wtf(TAG, "Unexpected intent: " + intent);
        }
    }

    private void handleStartTrackingRequest(String projectUuid) {
        KickStartTrackingRecordTask.aTask(getApplicationContext()).withProjectUuid(projectUuid).execute();
    }

    private void handleStopTrackingRequested(String projectUuid) {
        Log.i(TAG, "Stopping tracking for project " + projectUuid);
        TrackingRecord runningTrackingRecord = new TrackingRecordDAO(this).getRunning(projectUuid).get();
        KickStopTrackingRecordTask.aTask(getApplicationContext()).withProjectUuid(projectUuid).execute();

        if (isProjectRequiringDescriptionPromptAfterTracking(projectUuid)) {
            showTrackingRecordEditingActivity(runningTrackingRecord);
        }
    }

    private void handlePauseTrackingRequested(String projectUuid) {
        handleStopTrackingRequested(projectUuid);
        //
        addProjectToPausedList(projectUuid);
        new TrackingPlayer(getApplicationContext()).showProject(projectUuid);
    }

    private void handleUnpauseTrackingRequest(String projectUuid) {
        removeProjectFromPausedList(projectUuid);
        KickStartTrackingRecordTask.aTask(getApplicationContext()).withProjectUuid(projectUuid).execute();
    }

    private void handleDismissPausedProjectRequested(String projectUuid) {
        removeProjectFromPausedList(projectUuid);
        new TrackingPlayer(getApplicationContext()).revalidateVisibility();
    }

    private void handleCycleProjectRequested(String currentProjectUuid) {
        Log.i(TAG, "Cycling to next project coming from " + currentProjectUuid);
        new TrackingPlayer(getApplicationContext()).showNextProject(currentProjectUuid);
    }

    private boolean isProjectRequiringDescriptionPromptAfterTracking(String projectUuid) {
        TrackingConfiguration trackingConfiguration = new TrackingConfigurationDAO(this).getByProjectUuid(projectUuid).get();
        return trackingConfiguration.isPromptForDescription();
    }

    private void showTrackingRecordEditingActivity(TrackingRecord trackingRecord) {
        Intent intent = new Intent(getApplicationContext(), TrackingRecordModificationActivity.class);
        intent.putExtra(TrackingRecordModificationActivity.TRACKING_RECORD_UUID, trackingRecord.getUuid());
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    private void addProjectToPausedList(String projectUuid) {
        new TrackingPlayerModel(getApplicationContext()).addPausedProject(projectUuid);
    }

    private void removeProjectFromPausedList(String projectUuid) {
        new TrackingPlayerModel(getApplicationContext()).removePausedProject(projectUuid);
    }
}
