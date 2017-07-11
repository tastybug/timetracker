package com.tastybug.timetracker.extension.checkoutreminder.controller;

import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;

import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CheckoutReminderServiceTest {

    private ReminderRepository reminderRepository = mock(ReminderRepository.class);
    private ReminderNotification reminderNotification = mock(ReminderNotification.class);
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    private WarnableIndicator warnableIndicator = mock(WarnableIndicator.class);
    private CheckoutReminderService checkoutReminderService
            = new CheckoutReminderService(reminderRepository,
            reminderNotification,
            trackingRecordDAO,
            warnableIndicator);

    @Test
    public void perform_checks_all_ongoing_TRs_and_notifies_when_warnable() {
        TrackingRecord warnable = mock(TrackingRecord.class);
        TrackingRecord nonWarnable = mock(TrackingRecord.class);
        TrackingRecord warnable2 = mock(TrackingRecord.class);
        when(trackingRecordDAO.getRunning()).thenReturn(Arrays.asList(warnable, nonWarnable, warnable2));
        when(warnableIndicator.isWarnable(warnable)).thenReturn(true);
        when(warnableIndicator.isWarnable(warnable2)).thenReturn(true);
        when(warnableIndicator.isWarnable(nonWarnable)).thenReturn(false);

        checkoutReminderService.perform();

        // warnings correct?
        verify(reminderNotification).showWarning(warnable);
        verify(reminderNotification).showWarning(warnable2);
        verifyNoMoreInteractions(reminderNotification);
        // made sure those are only warned once?
        verify(reminderRepository).addTrackingRecord(warnable);
        verify(reminderRepository).addTrackingRecord(warnable2);
        verifyNoMoreInteractions(reminderRepository);
    }
}