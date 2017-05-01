package com.tastybug.timetracker.extension.reporting.controller.internal.nonaggregated;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TrackingRecordReportItemTest {

    @Test(expected = IllegalStateException.class)
    public void constructor_throws_IllegalState_on_null_start_date_in_TrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("", "", Optional.<java.util.Date>absent(), Optional.of(new Date()), Optional.<String>absent(), Rounding.Strategy.NO_ROUNDING);

        // expect
        new TrackingRecordReportItem(trackingRecord);
    }

    @Test(expected = IllegalStateException.class)
    public void constructor_throws_IllegalState_on_null_end_date_in_TrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("", "", Optional.of(new Date()), Optional.<java.util.Date>absent(), Optional.<String>absent(), Rounding.Strategy.NO_ROUNDING);

        // expect
        new TrackingRecordReportItem(trackingRecord);
    }

    @Test
    public void constructor_stores_start_end_description_and_duration_from_TrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("", "", Optional.of(new Date(1)), Optional.of(new Date(5)), Optional.<String>absent(), Rounding.Strategy.NO_ROUNDING);

        // when
        TrackingRecordReportItem trackingRecordReportItem = new TrackingRecordReportItem(trackingRecord);

        // then
        assertEquals(trackingRecord.getDescription(), trackingRecordReportItem.getDescription());
        assertEquals(trackingRecord.getStart().get(), trackingRecordReportItem.getStartDate());
        assertEquals(trackingRecord.getEnd().get(), trackingRecordReportItem.getEndDate());
        assertEquals(trackingRecord.toEffectiveDuration().get(), trackingRecordReportItem.getDuration());

    }

}