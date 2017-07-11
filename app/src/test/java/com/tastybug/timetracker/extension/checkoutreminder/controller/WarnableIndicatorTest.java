package com.tastybug.timetracker.extension.checkoutreminder.controller;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;

import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WarnableIndicatorTest {

    private static final Duration BELOW_THRESHOLD_DURATION = new Duration(1000 * 60 * 60 * 12);
    private static final Duration THRESHOLD_DURATION = new Duration((1000 * 60 * 60 * 12)+1);

    private ReminderRepository reminderRepository = mock(ReminderRepository.class);
    private WarnableIndicator warnableIndicator = new WarnableIndicator(reminderRepository);

    @Test
    public void isWarnable_returns_false_if_TrackingRecord_is_too_short_for_warn() {
        TrackingRecord trackingRecord = mock(TrackingRecord.class);
        when(trackingRecord.toDuration()).thenReturn(Optional.of(BELOW_THRESHOLD_DURATION));

        boolean result = warnableIndicator.isWarnable(trackingRecord);

        assertFalse(result);
    }

    @Test
    public void isWarnable_returns_false_if_TrackingRecord_has_already_been_warned() {
        TrackingRecord trackingRecord = mock(TrackingRecord.class);
        when(trackingRecord.toDuration()).thenReturn(Optional.of(THRESHOLD_DURATION));
        when(reminderRepository.hasTrackingRecord(trackingRecord)).thenReturn(true);

        boolean result = warnableIndicator.isWarnable(trackingRecord);

        assertFalse(result);
    }

    @Test
    public void isWarnable_returns_true_if_TrackingRecord_hasnt_been_warned_yet_and_is_long_enough() {
        TrackingRecord trackingRecord = mock(TrackingRecord.class);
        when(trackingRecord.toDuration()).thenReturn(Optional.of(THRESHOLD_DURATION));
        when(reminderRepository.hasTrackingRecord(trackingRecord)).thenReturn(false);

        boolean result = warnableIndicator.isWarnable(trackingRecord);

        assertTrue(result);
    }
}