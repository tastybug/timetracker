package com.tastybug.timetracker.model.statistics;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.rounding.RoundingFactory;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class StatisticProjectCompletionTest {

    @Test public void handlesProjectWithoutRecordsCorrectly() {
        // given
        ArrayList<TrackingRecord> trackingRecords = new ArrayList<TrackingRecord>();
        StatisticProjectCompletion subject = new StatisticProjectCompletion(aTrackingConfigurationWithLimit(),
                trackingRecords,
                true);

        // when
        Optional<Double> completion = subject.getCompletionPercent();

        // then
        assertTrue(completion.isPresent());

        // and
        assertSame(0, completion.get().intValue());

        // and:
        assertFalse(subject.isOverbooked());

        // when
        Optional<Duration> remainder = subject.getRemainingDuration();

        // and
        assertEquals(Duration.standardHours(aTrackingConfigurationWithLimit().getHourLimit().get()), remainder.get());
    }

    @Test public void handlesProjectWithoutHourLimitCorrectly() {
        // given
        ArrayList<TrackingRecord> trackingRecords = aTrackingRecordsList(aTrackingRecordWith10MinDuration());
        StatisticProjectCompletion subject = new StatisticProjectCompletion(aTrackingConfigurationWithoutLimit(),
                trackingRecords,
                true);

        // when
        Optional<Double> completion = subject.getCompletionPercent();

        // then
        assertFalse(completion.isPresent());

        // and:
        assertFalse(subject.isOverbooked());

        // when
        Optional<Duration> remainder = subject.getRemainingDuration();
        // and
        assertFalse(remainder.isPresent());
    }

    @Test public void handlesProjectWithLimitAndExistingTrackingRecordsCorrectly() {
        // given
        ArrayList<TrackingRecord> trackingRecords = aTrackingRecordsList(
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration()
        );
        StatisticProjectCompletion subject = new StatisticProjectCompletion(
                aTrackingConfigurationWithLimit(),
                trackingRecords,
                true);

        // when
        Optional<Double> completion = subject.getCompletionPercent();

        // then
        assertTrue(completion.isPresent());

        // and: 20mins of a max of 60mins means 33% completion
        assertSame(33, completion.get().intValue());

        // and:
        assertFalse(subject.isOverbooked());

        // when
        Optional<Duration> remainder = subject.getRemainingDuration();
        // and
        assertEquals(40, remainder.get().getStandardMinutes());
    }

    @Test public void handlesOverbookedProjectCorrectly() {
        // given
        ArrayList<TrackingRecord> trackingRecords = aTrackingRecordsList(
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration()
        );
        TrackingConfiguration configuration = aTrackingConfigurationWithLimit();
        StatisticProjectCompletion subject = new StatisticProjectCompletion(configuration,
                trackingRecords,
                true);

        // when
        Optional<Double> completion = subject.getCompletionPercent();

        // then
        assertTrue(completion.isPresent());

        // and: 90mins of a max of 60mins means 150% completion
        assertEquals(150, completion.get().intValue());

        // and:
        assertTrue(subject.isOverbooked());

        // when
        Optional<Duration> remainder = subject.getRemainingDuration();
        // and
        assertEquals(0, remainder.get().getStandardMinutes());
    }

    private TrackingConfiguration aTrackingConfigurationWithLimit() {
        return new TrackingConfiguration("uuid",
                "some project uuid",
                1,
                null,
                null,
                true,
                RoundingFactory.Strategy.NO_ROUNDING);
    }

    private TrackingConfiguration aTrackingConfigurationWithoutLimit() {
        return new TrackingConfiguration("uuid",
                "some project uuid",
                null,
                null,
                null,
                true,
                RoundingFactory.Strategy.NO_ROUNDING);
    }


    private ArrayList<TrackingRecord> aTrackingRecordsList(TrackingRecord... trackingRecords) {

        return new ArrayList<TrackingRecord>(Arrays.asList(trackingRecords));
    }

    private TrackingRecord aTrackingRecordWith10MinDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 40, 0);

        return new TrackingRecord("uuid", "some project uuid", start.toDate(), stop.toDate(), "some description");
    }
}