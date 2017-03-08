package com.tastybug.timetracker.extension.reporting.controller.internal.nonaggregated;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;

import org.joda.time.Duration;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackingRecordReportItemTest {

    private TrackingConfiguration trackingConfiguration = new TrackingConfiguration("");

    @Test(expected = IllegalStateException.class)
    public void constructor_throws_IllegalState_on_null_start_date_in_TrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("", "", Optional.<java.util.Date>absent(), Optional.of(new Date()), Optional.<String>absent());

        // expect
        new TrackingRecordReportItem(trackingRecord, trackingConfiguration);
    }

    @Test(expected = IllegalStateException.class)
    public void constructor_throws_IllegalState_on_null_end_date_in_TrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("", "", Optional.of(new Date()), Optional.<java.util.Date>absent(), Optional.<String>absent());

        // expect
        new TrackingRecordReportItem(trackingRecord, trackingConfiguration);
    }

    @Test
    public void constructor_uses_correct_TrackingConfiguration_when_asking_for_effective_duration_at_TrackingRecord() {
        // given
        Duration expectedDuration = new Duration(1);
        TrackingRecord trackingRecord = mock(TrackingRecord.class);
        when(trackingRecord.getStart()).thenReturn(Optional.of(new Date(1)));
        when(trackingRecord.getEnd()).thenReturn(Optional.of(new Date(2)));
        when(trackingRecord.toEffectiveDuration(trackingConfiguration)).thenReturn(Optional.of(expectedDuration)); // <- the important part
        when(trackingRecord.getDescription()).thenReturn(Optional.of(""));

        // when
        TrackingRecordReportItem trackingRecordReportItem = new TrackingRecordReportItem(trackingRecord, trackingConfiguration);

        // then
        assertEquals(expectedDuration, trackingRecordReportItem.getDuration());
    }

    @Test
    public void constructor_stores_start_end_description_and_duration_from_TrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("", "", Optional.of(new Date(1)), Optional.of(new Date(5)), Optional.<String>absent());

        // when
        TrackingRecordReportItem trackingRecordReportItem = new TrackingRecordReportItem(trackingRecord, trackingConfiguration);

        // then
        assertEquals(trackingRecord.getDescription(), trackingRecordReportItem.getDescription());
        assertEquals(trackingRecord.getStart().get(), trackingRecordReportItem.getStartDate());
        assertEquals(trackingRecord.getEnd().get(), trackingRecordReportItem.getEndDate());
        assertEquals(trackingRecord.toEffectiveDuration(trackingConfiguration).get(), trackingRecordReportItem.getDuration());

    }

}