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

    @Test(expected = NullPointerException.class)
    public void withFirstDay_throws_NPE_on_null_date() {
        filter.withFirstDay(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withFirstDay_throws_IAE_on_start_date_after_end_date() {
        filter.withLastDayExclusive(new Date(4)).withFirstDay(new Date(5));
    }

    @Test(expected = NullPointerException.class)
    public void withTimeFrameEndingAt_throws_NPE_on_null_date() {
        filter.withLastDayExclusive(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withLastDayExclusive_throws_IAE_on_start_date_after_end_date() {
        filter.withFirstDay(new Date(5)).withLastDayExclusive(new Date(4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void withFirstDay_throws_IAE_on_start_date_equals_end_date() {
        filter.withFirstDay(new Date(5)).withLastDayExclusive(new Date(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void withLastDayExclusive_throws_IAE_on_start_date_equals_end_date() {
        filter.withLastDayExclusive(new Date(5)).withFirstDay(new Date(5));
    }

    @Test(expected = NullPointerException.class)
    public void withTrackingRecordList_throws_NPE_on_null_list() {
        filter.withTrackingRecordList(null);
    }

    @Test
    public void withLastDayInclusive_sets_last_day_correctly() {
        // given
        Date firstDay = new Date(1);
        Date lastDayInclusive = new Date(5);
        DateTime expectedLastDayExclusive = new DateTime(lastDayInclusive).plusDays(1);
        filter.withFirstDay(firstDay);
        filter.withTrackingRecordList(aListWithTR(new Date(1), expectedLastDayExclusive.minusSeconds(1).toDate()));

        // when
        filter.withLastDayInclusive(lastDayInclusive);

        // then
        assertEquals(filter.build().size(), 1);
    }

    @Test
    public void build_ignores_unfinished_tracking_records() {
        // given
        filter.withTrackingRecordList(aListWithOneUnfinishedTrackingRecord());

        // when
        List<TrackingRecord> results = filter.build();

        // when
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_returns_TR_ending_BEFORE_time_frame_end() {
        // given
        filter.withFirstDay(new Date(1));
        filter.withLastDayExclusive(new Date(4));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> results = filter.build();

        // then
        assertEquals(results.get(0), aListWithTRStartingAt2EndingAt3().get(0));

        // and
        assertEquals(results.size(), 1);
    }

    @Test
    public void build_ignores_TR_ending_RIGHT_ON_time_frame_end() {
        // given
        filter.withFirstDay(new Date(1));
        filter.withLastDayExclusive(new Date(3));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> results = filter.build();

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_ignores_TR_ending_AFTER_time_frame_end() {
        // given
        filter.withFirstDay(new Date(1));
        filter.withLastDayExclusive(new Date(2));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> results = filter.build();

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_ignores_TR_ending_BEFORE_time_frame_start() {
        // given
        filter.withFirstDay(new Date(4));
        filter.withLastDayExclusive(new Date(5));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> results = filter.build();

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void buildEdges_returns_TRs_ending_in_time_frame_but_starting_before_time_frame() {
        // given
        filter.withFirstDay(new Date(3));
        filter.withLastDayExclusive(new Date(4));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> edgeResults = filter.buildEdges();

        // then
        assertEquals(1, edgeResults.size());
    }

    @Test
    public void buildEdges_ignores_TRs_starting_and_ending_in_time_frame() {
        // given
        filter.withFirstDay(new Date(2));
        filter.withLastDayExclusive(new Date(4));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> edgeResults = filter.buildEdges();

        // then
        assertTrue(edgeResults.isEmpty());
    }

    private List<TrackingRecord> aListWithOneUnfinishedTrackingRecord() {
        TrackingRecord trackingRecord = new TrackingRecord("uuid", "project-uuid", Optional.of(new Date()), Optional.<Date>absent(), Optional.<String>absent(), Rounding.Strategy.NO_ROUNDING);
        return Collections.singletonList(trackingRecord);
    }

    private List<TrackingRecord> aListWithTRStartingAt2EndingAt3() {
        return aListWithTR(new Date(2), new Date(3));
    }

    private List<TrackingRecord> aListWithTR(Date start, Date end) {
        TrackingRecord trackingRecord = new TrackingRecord("uuid", "project-uuid", Optional.of(start), Optional.of(end), Optional.<String>absent(), Rounding.Strategy.NO_ROUNDING);
        return Collections.singletonList(trackingRecord);
    }
}