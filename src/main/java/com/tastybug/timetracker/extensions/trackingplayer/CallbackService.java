package com.tastybug.timetracker.extensions.trackingplayer;

import android.app.IntentService;
import android.content.Intent;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.extensions.trackingplayer.internal.NotificationModel;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.task.tracking.checkin.CheckInTask;
import com.tastybug.timetracker.task.tracking.checkout.CheckOutTask;
import com.tastybug.timetracker.ui.trackingrecordmodification.TrackingRecordModificationActivity;

import static com.tastybug.timetracker.util.ConditionalLog.logError;
import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

/**
 * This service acts as the callback facade for the Tracking Player notification. Pressing
 * some button on the player will result in a call to this service.
 * <p/>
 * This background service only lives as long as the request!
 */
public class CallbackService extends IntentService {

    public static final String OPERATION = "OPERATION";
    public static final String CYCLE_TO_NEXT_PROJECT = "CYCLE_TO_NEXT_PROJECT";
    public static final String STOP_TRACKING_PROJECT = "STOP_TRACKING_PROJECT";
    public static final String DISMISS_PAUSED_PROJECT = "DISMISS_PAUSED_PROJECT";
    public static final String PAUSE_TRACKING_PROJECT = "PAUSE_TRACKING_PROJECT";
    public static final String UNPAUSE_TRACKING_PROJECT = "UNPAUSE_TRACKING_PROJECT";
    public static final String PROJECT_UUID = "PROJECT_UUID";
    private static final String TAG = CallbackService.class.getSimpleName();

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
        } else if (CYCLE_TO_NEXT_PROJECT.equals(operation)) {
            handleCycleProjectRequested(projectUuid);
        } else if (PAUSE_TRACKING_PROJECT.equals(operation)) {
            handlePauseTrackingRequested(projectUuid);
        } else if (UNPAUSE_TRACKING_PROJECT.equals(operation)) {
            handleUnpauseTrackingRequest(projectUuid);
        } else if (DISMISS_PAUSED_PROJECT.equals(operation)) {
            handleDismissPausedProjectRequested(projectUuid);
        } else {
            logError(TAG, "Unexpected intent: " + intent);
        }
    }

    private void handleStopTrackingRequested(String projectUuid) {
        logInfo(TAG, "Stopping tracking for project " + projectUuid);
        TrackingRecord runningTrackingRecord = new TrackingRecordDAO(this).getRunning(projectUuid).get();
        new CheckOutTask(getApplicationContext()).withProjectUuid(projectUuid).run();

        if (isProjectRequiringDescriptionPromptAfterTracking(projectUuid)) {
            showTrackingRecordEditingActivity(runningTrackingRecord);
        }
    }

    private void handlePauseTrackingRequested(String projectUuid) {
        handleStopTrackingRequested(projectUuid);
        addProjectToPausedList(projectUuid);
        // now we wait for the otto event regarding the stopping
    }

    private void handleUnpauseTrackingRequest(String projectUuid) {
        removeProjectFromPausedList(projectUuid);
        new CheckInTask(getApplicationContext()).withProjectUuid(projectUuid).run();
    }

    private void handleDismissPausedProjectRequested(String projectUuid) {
        removeProjectFromPausedList(projectUuid);
        new TrackingPlayer(getApplicationContext()).showSomeProjectOrHide();
    }

    private void handleCycleProjectRequested(String currentProjectUuid) {
        logInfo(TAG, "Cycling to next project coming from " + currentProjectUuid);
        new TrackingPlayer(getApplicationContext()).cycleProject(currentProjectUuid);
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
        new NotificationModel(getApplicationContext()).addPausedProject(projectUuid);
    }

    private void removeProjectFromPausedList(String projectUuid) {
        new NotificationModel(getApplicationContext()).removePausedProject(projectUuid);
    }
}
