package com.tastybug.timetracker.model;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.filter.TrackingRecordTimeFrameFilter;

import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TrackingRecordTimeFrameFilterTest {

    private TrackingRecordTimeFrameFilter filter = new TrackingRecordTimeFrameFilter();

    @Test(expected = NullPointerException.class)
    public void withTimeFrameStartingAt_throws_NPE_on_null_date() {
        filter.withTimeFrameStartingAt(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withTimeFrameStartingAt_throws_IAE_on_start_date_after_end_date() {
        filter.withTimeFrameEndingAt(new Date(4)).withTimeFrameStartingAt(new Date(5));
    }

    @Test(expected = NullPointerException.class)
    public void withTimeFrameEndingAt_throws_NPE_on_null_date() {
        filter.withTimeFrameEndingAt(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withTimeFrameEndingAt_throws_IAE_on_start_date_after_end_date() {
        filter.withTimeFrameStartingAt(new Date(5)).withTimeFrameEndingAt(new Date(4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void withTimeFrameStartingAt_throws_IAE_on_start_date_equals_end_date() {
        filter.withTimeFrameStartingAt(new Date(5)).withTimeFrameEndingAt(new Date(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void withTimeFrameEndingAt_throws_IAE_on_start_date_equals_end_date() {
        filter.withTimeFrameEndingAt(new Date(5)).withTimeFrameStartingAt(new Date(5));
    }

    @Test(expected = NullPointerException.class)
    public void withTrackingRecordList_throws_NPE_on_null_list() {
        filter.withTrackingRecordList(null);
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
        filter.withTimeFrameStartingAt(new Date(1));
        filter.withTimeFrameEndingAt(new Date(4));
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
        filter.withTimeFrameStartingAt(new Date(1));
        filter.withTimeFrameEndingAt(new Date(3));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> results = filter.build();

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_ignores_TR_ending_AFTER_time_frame_end() {
        // given
        filter.withTimeFrameStartingAt(new Date(1));
        filter.withTimeFrameEndingAt(new Date(2));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> results = filter.build();

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void build_ignores_TR_ending_BEFORE_time_frame_start() {
        // given
        filter.withTimeFrameStartingAt(new Date(4));
        filter.withTimeFrameEndingAt(new Date(5));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> results = filter.build();

        // then
        assertTrue(results.isEmpty());
    }

    @Test
    public void buildEdges_returns_TRs_ending_in_time_frame_but_starting_before_time_frame() {
        // given
        filter.withTimeFrameStartingAt(new Date(3));
        filter.withTimeFrameEndingAt(new Date(4));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> edgeResults = filter.buildEdges();

        // then
        assertEquals(1, edgeResults.size());
    }

    @Test
    public void buildEdges_ignores_TRs_starting_and_ending_in_time_frame() {
        // given
        filter.withTimeFrameStartingAt(new Date(2));
        filter.withTimeFrameEndingAt(new Date(4));
        filter.withTrackingRecordList(aListWithTRStartingAt2EndingAt3());

        // when
        List<TrackingRecord> edgeResults = filter.buildEdges();

        // then
        assertTrue(edgeResults.isEmpty());
    }

    private List<TrackingRecord> aListWithOneUnfinishedTrackingRecord() {
        TrackingRecord trackingRecord = new TrackingRecord("uuid", "project-uuid", Optional.of(new Date()), Optional.<Date>absent(), Optional.<String>absent());
        return Collections.singletonList(trackingRecord);
    }

    private List<TrackingRecord> aListWithTRStartingAt2EndingAt3() {
        TrackingRecord trackingRecord = new TrackingRecord("uuid", "project-uuid", Optional.of(new Date(2)), Optional.of(new Date(3)), Optional.<String>absent());
        return Collections.singletonList(trackingRecord);
    }
}