package com.tastybug.timetracker.extension.checkoutreminder.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import org.joda.time.Duration;

import java.util.List;

public class ReminderIntentService {

    private static final Duration THRESHOLD_DURATION = new Duration(1000 * 60 * 60 * 12);

    private ReminderRepository reminderRepository;
    private ReminderNotification reminderNotification;
    private TrackingRecordDAO trackingRecordDAO;

    public ReminderIntentService(Context context) {
        this(new ReminderRepository(context),
                new ReminderNotification(context),
                new TrackingRecordDAO(context));
    }

    private ReminderIntentService(ReminderRepository reminderRepository,
                                  ReminderNotification reminderNotification,
                                  TrackingRecordDAO trackingRecordDAO) {
        this.reminderRepository = reminderRepository;
        this.reminderNotification = reminderNotification;
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public void perform() {
        List<TrackingRecord> runningTrackingSessions = getRunningTrackingSessions();
        for (TrackingRecord trackingRecord : runningTrackingSessions) {
            if (isToBeWarned(reminderRepository, trackingRecord)) {
                performWarning(reminderRepository, trackingRecord);
            }
        }
    }

    private boolean isToBeWarned(ReminderRepository reminderRepository, TrackingRecord trackingRecord) {
        boolean exceeds = trackingRecord.toDuration().get().isLongerThan(THRESHOLD_DURATION);
        return exceeds && !reminderRepository.hasTrackingRecord(trackingRecord);
    }

    private void performWarning(ReminderRepository reminderRepository, TrackingRecord trackingRecord) {
        reminderRepository.addTrackingRecord(trackingRecord);
        reminderNotification.showWarning(trackingRecord);
    }

    private List<TrackingRecord> getRunningTrackingSessions() {
        return trackingRecordDAO.getRunning();
    }
}
