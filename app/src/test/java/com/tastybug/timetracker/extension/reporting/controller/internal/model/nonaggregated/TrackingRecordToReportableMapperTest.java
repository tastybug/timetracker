package com.tastybug.timetracker.extension.reporting.controller.internal.model.nonaggregated;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;
import com.tastybug.timetracker.extension.reporting.controller.internal.ReportableItem;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrackingRecordToReportableMapperTest {

    private TrackingRecordToReportableMapper subject = new TrackingRecordToReportableMapper();


    @Test(expected = NullPointerException.class)
    public void mapRecords_throws_NPE_when_given_null_TrackingRecord_list() {
        // expect
        subject.mapRecords(null);
    }

    @Test
    public void mapRecords_returns_empty_result_list_for_empty_tracking_record_list() {
        // when
        List<ReportableItem> reportables = subject.mapRecords(new ArrayList<TrackingRecord>());

        // then
        assertTrue(reportables.isEmpty());
    }

    @Test
    public void mapRecords_returns_mapped_instances() {
        // given
        TrackingRecord trackingRecord1 = new TrackingRecord("uuid",
                "projectUuid",
                Optional.of(new DateTime(2016, 12, 24, 0, 0).toDate()),
                Optional.of(new DateTime(2016, 12, 24, 2, 0).toDate()),
                Optional.of("desc1"),
                Rounding.Strategy.NO_ROUNDING);
        TrackingRecord trackingRecord2 = new TrackingRecord("uuid",
                "projectUuid",
                Optional.of(new DateTime(2016, 12, 25, 12, 0).toDate()),
                Optional.of(new DateTime(2016, 12, 25, 14, 0).toDate()),
                Optional.of("desc2"),
                Rounding.Strategy.NO_ROUNDING);
        List<TrackingRecord> trackingRecords = Arrays.asList(trackingRecord1, trackingRecord2);

        // when
        List<ReportableItem> reportables = subject.mapRecords(trackingRecords);

        // then
        assertEquals(2, reportables.size());

        // and
        assertEquals(reportables.get(0).getStartDate(), trackingRecord1.getStart().get());
        assertEquals(reportables.get(0).getEndDate(), trackingRecord1.getEnd().get());
        assertEquals(reportables.get(0).getDescription(), trackingRecord1.getDescription());
        assertEquals(reportables.get(0).getDuration(), trackingRecord1.toEffectiveDuration().get());

        // and
        assertEquals(reportables.get(1).getStartDate(), trackingRecord2.getStart().get());
        assertEquals(reportables.get(1).getEndDate(), trackingRecord2.getEnd().get());
        assertEquals(reportables.get(1).getDescription(), trackingRecord2.getDescription());
        assertEquals(reportables.get(1).getDuration(), trackingRecord2.toEffectiveDuration().get());
    }
}