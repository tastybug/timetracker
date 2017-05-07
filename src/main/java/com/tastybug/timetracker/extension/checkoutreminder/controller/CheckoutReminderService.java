package com.tastybug.timetracker.extension.checkoutreminder.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import java.util.List;

public class CheckoutReminderService {

    private ReminderRepository reminderRepository;
    private ReminderNotification reminderNotification;
    private TrackingRecordDAO trackingRecordDAO;
    private WarnableIndicator warnableIndicator;

    public CheckoutReminderService(Context context) {
        this(new ReminderRepository(context),
                new ReminderNotification(context),
                new TrackingRecordDAO(context),
                new WarnableIndicator(context));
    }

    CheckoutReminderService(ReminderRepository reminderRepository,
                            ReminderNotification reminderNotification,
                            TrackingRecordDAO trackingRecordDAO,
                            WarnableIndicator warnableIndicator) {
        this.reminderRepository = reminderRepository;
        this.reminderNotification = reminderNotification;
        this.trackingRecordDAO = trackingRecordDAO;
        this.warnableIndicator = warnableIndicator;
    }

    public void perform() {
        List<TrackingRecord> runningTrackingSessions = trackingRecordDAO.getRunning();
        for (TrackingRecord trackingRecord : runningTrackingSessions) {
            if (warnableIndicator.isWarnable(trackingRecord)) {
                performWarning(trackingRecord);
            }
        }
    }

    private void performWarning(TrackingRecord trackingRecord) {
        reminderRepository.addTrackingRecord(trackingRecord);
        reminderNotification.showWarning(trackingRecord);
    }

}
