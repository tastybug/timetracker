package com.tastybug.timetracker.extension.checkoutreminder.controller;

import android.content.Context;

import com.tastybug.timetracker.core.model.TrackingRecord;

import org.joda.time.Duration;

class WarnableIndicator {

    private static final Duration THRESHOLD_DURATION = new Duration(1000 * 60 * 60 * 12);

    private ReminderRepository reminderRepository;

    WarnableIndicator(Context context) {
        this(new ReminderRepository(context));
    }

    WarnableIndicator(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    boolean isWarnable(TrackingRecord trackingRecord) {
        boolean exceeds = trackingRecord.toDuration().get().isLongerThan(THRESHOLD_DURATION);
        return exceeds && !reminderRepository.hasTrackingRecord(trackingRecord);
    }
}
