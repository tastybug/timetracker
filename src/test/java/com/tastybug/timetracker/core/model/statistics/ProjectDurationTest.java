package com.tastybug.timetracker.core.model.statistics;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class ProjectDurationTest {

    @Test(expected = NullPointerException.class)
    public void creatingStatisticsForNullTrackingConfigurationYieldsException() {
        new ProjectDuration(null, new ArrayList<TrackingRecord>());
    }

    @Test(expected = NullPointerException.class)
    public void creatingStatisticsForNullTrackingRecordArrayYieldsException() {
        new ProjectDuration(new TrackingConfiguration("some project uuid"), null);
    }

    @Test
    public void canCreateStatisticForEmptyListOfTrackingRecords() {
        // given
        ArrayList<TrackingRecord> list = new ArrayList<TrackingRecord>();
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        org.joda.time.Duration duration = new ProjectDuration(configuration, list).getDuration();

        // then
        assertEquals(0, duration.getStandardSeconds());
    }

    @Test
    public void canCalculateWhenNoRoundingIsConfigured() {
        // given
        ArrayList<TrackingRecord> list = new ArrayList<TrackingRecord>();
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        org.joda.time.Duration duration = new ProjectDuration(configuration, list).getDuration();

        // then
        assertEquals(0, duration.getStandardSeconds());
    }

    @Test
    public void canCalculateEffectiveProjectDurationWithNoRounding() {
        // given
        ArrayList<TrackingRecord> trackingRecords = aTrackingRecordsList(aTrackingRecordWith10Min10SecsDuration(), aTrackingRecordWith10Min10SecsDuration());
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        org.joda.time.Duration duration = new ProjectDuration(configuration, trackingRecords).getDuration();

        // then: 2 * 10:10min
        assertEquals(2 * (10 * 60 + 10), duration.getStandardSeconds());
    }

    @Test
    public void canCalculateEffectiveProjectDurationWith10erRounding() {
        // given
        ArrayList<TrackingRecord> trackingRecords = aTrackingRecordsList(aTrackingRecordWith10Min10SecsDuration(), aTrackingRecordWith10Min10SecsDuration());
        TrackingConfiguration configuration = aTrackingConfigurationWith10erRounding();

        // when
        org.joda.time.Duration duration = new ProjectDuration(configuration, trackingRecords).getDuration();

        // then: 2 * 20 minutes
        assertEquals(2 * 20 * 60, duration.getStandardSeconds());
    }

    @Test
    public void calculationIgnoresIncompleteTrackingRecordsWhenTold() {
        // given
        ArrayList<TrackingRecord> trackingRecords = aTrackingRecordsList(aTrackingRecordWith10Min10SecsDuration(), anOngoingTrackingRecord(), aTrackingRecordWith10Min10SecsDuration());
        TrackingConfiguration configuration = aTrackingConfigurationWithNoRounding();

        // when
        org.joda.time.Duration duration = new ProjectDuration(configuration, trackingRecords, false).getDuration();

        // then
        assertEquals(2 * (10 * 60 + 10), duration.getStandardSeconds());
    }

    private TrackingConfiguration aTrackingConfigurationWithNoRounding() {
        return new TrackingConfiguration("some project uuid", Rounding.Strategy.NO_ROUNDING);
    }

    private TrackingConfiguration aTrackingConfigurationWith10erRounding() {
        return new TrackingConfiguration("some project uuid", Rounding.Strategy.TEN_MINUTES_UP);
    }

    private ArrayList<TrackingRecord> aTrackingRecordsList(TrackingRecord... trackingRecords) {

        return new ArrayList<>(Arrays.asList(trackingRecords));
    }

    private TrackingRecord aTrackingRecordWith10Min10SecsDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 40, 10);

        return new TrackingRecord("uuid", "some project uuid", Optional.of(start.toDate()), Optional.of(stop.toDate()), Optional.of("some description"));
    }

    private TrackingRecord anOngoingTrackingRecord() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);

        return new TrackingRecord("uuid", "some project uuid", Optional.of(start.toDate()), Optional.<Date>absent(), Optional.of("some description"));
    }
}