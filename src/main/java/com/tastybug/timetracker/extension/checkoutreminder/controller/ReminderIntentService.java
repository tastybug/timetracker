package com.tastybug.timetracker.extension.checkoutreminder.controller;

import android.app.IntentService;
import android.content.Intent;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import org.joda.time.Duration;

import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class ReminderIntentService extends IntentService {

    private static final String TAG = ReminderIntentService.class.getSimpleName();
    private static final Duration THRESHOLD_DURATION = new Duration(1000 * 60 * 60 * 12);

    public ReminderIntentService() {
        super(ReminderIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isEnabled()) {
            logInfo(TAG, "Disabled, bailing now.");
            return;
        }
        logInfo(TAG, "Checking for stale tracking sessions now.");

        ReminderRepository reminderRepository = new ReminderRepository(getApplicationContext());
        List<TrackingRecord> runningTrackingSessions = getRunningTrackingSessions();
        for (TrackingRecord trackingRecord : runningTrackingSessions) {
            if (isToBeWarned(reminderRepository, trackingRecord)) {
                performWarning(reminderRepository, trackingRecord);
            }
        }
        logInfo(TAG, "Check completed.");
    }

    private boolean isEnabled() {
        return new ReminderSettings(getApplicationContext()).isEnabled();
    }

    private boolean isToBeWarned(ReminderRepository reminderRepository, TrackingRecord trackingRecord) {
        boolean exceeds = trackingRecord.toDuration().get().isLongerThan(THRESHOLD_DURATION);
        return exceeds && !reminderRepository.hasTrackingRecord(trackingRecord);
    }

    private void performWarning(ReminderRepository reminderRepository, TrackingRecord trackingRecord) {
        reminderRepository.addTrackingRecord(trackingRecord);
        new ReminderNotification(getApplicationContext()).showWarning(trackingRecord);
    }

    private List<TrackingRecord> getRunningTrackingSessions() {
        return new TrackingRecordDAO(getApplicationContext()).getRunning();
    }
}
