package com.tastybug.timetracker.extension.reporting.controller.internal;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrackingRecordTimeFrameFilterTest {

    private TrackingRecordTimeFrameFilter filter = new TrackingRecordTimeFrameFilter();

    /*
        For the sake of legibility this test uses a time frame that covers christmas.
        The various test cases handle cases with dates in and around christmas time.
     */
    private Date day2212 = new DateTime(2016, 12, 23, 0, 0, 0).toDate();
    private Date day2312 = new DateTime(2016, 12, 23, 0, 0, 0).toDate();
    private Date day2412 = new DateTime(2016, 12, 24, 0, 0, 0).toDate();
    private Date day2512 = new DateTime(2016, 12, 25, 0, 0, 0).toDate();
    private Date day2612 = new DateTime(2016, 12, 26, 0, 0, 0).toDate();
    private Date day2712 = new DateTime(2016, 12, 27, 0, 0, 0).toDate();

    @Test(expected = IllegalArgumentException.class)
    public void throws_IAE_if_time_frame_is_broken() {
        filter.getRecordsWithinTimeFrame(Collections.<TrackingRecord>emptyList(), day2612, day2412);
    }

    @Test
    public void filtering_ignores_unfinished_tracking_records() {
        // given
        List<TrackingRecord> initialList = aListWithOneUnfinishedTrackingRecord();

        // when
        List<TrackingRecord> results = filter.getRecordsWithinTimeFrame(initialList, day2412, day2612);

        // when
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_returns_TR_ending_BEFORE_time_frame_end() {
        // given
        List<TrackingRecord> initialList = aTrackingRecord(day2412, day2512);

        // when
        List<TrackingRecord> results = filter.getRecordsWithinTimeFrame(initialList, day2412, day2612);

        // then
        assertEquals(results.get(0), initialList.get(0));

        // and
        assertEquals(results.size(), 1);
    }

    @Test
    public void build_ignores_TR_ending_RIGHT_ON_time_frame_end() {
        // given
        List<TrackingRecord> initialList = aTrackingRecord(day2412, day2612);

        // when
        List<TrackingRecord> results = filter.getRecordsWithinTimeFrame(initialList, day2412, day2612);

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_ignores_TR_ending_AFTER_time_frame_end() {
        // given
        List<TrackingRecord> initialList = aTrackingRecord(day2412, day2712);

        // when
        List<TrackingRecord> results = filter.getRecordsWithinTimeFrame(initialList, day2412, day2612);

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_ignores_TR_ending_BEFORE_time_frame_start() {
        // given
        List<TrackingRecord> initialList = aTrackingRecord(day2212, day2312);

        // when
        List<TrackingRecord> results = filter.getRecordsWithinTimeFrame(initialList, day2412, day2612);

        // then
        assertTrue(results.isEmpty());
    }

    private List<TrackingRecord> aListWithOneUnfinishedTrackingRecord() {
        TrackingRecord trackingRecord = new TrackingRecord("uuid", "project-uuid", Optional.of(new Date()), Optional.<Date>absent(), Optional.<String>absent(), Rounding.Strategy.NO_ROUNDING);
        return Collections.singletonList(trackingRecord);
    }

    private List<TrackingRecord> aTrackingRecord(Date start, Date end) {
        TrackingRecord trackingRecord = new TrackingRecord("uuid", "project-uuid", Optional.fromNullable(start), Optional.fromNullable(end), Optional.<String>absent(), Rounding.Strategy.NO_ROUNDING);
        return Collections.singletonList(trackingRecord);
    }
}