package com.tastybug.timetracker.core.model.statistics;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CompletionStatisticTest {

    @Test
    public void handlesProjectWithoutRecordsCorrectly() {
        // given
        ArrayList<TrackingRecord> trackingRecords = new ArrayList<>();
        Completion subject = new Completion(aTrackingConfigurationWithLimit(),
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

    @Test
    public void handlesProjectWithoutHourLimitCorrectly() {
        // given
        List<TrackingRecord> trackingRecords = aTrackingRecordsList(aTrackingRecordWith10MinDuration());
        Completion subject = new Completion(aTrackingConfigurationWithoutLimit(),
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

    @Test
    public void handlesProjectWithLimitAndExistingTrackingRecordsCorrectly() {
        // given
        List<TrackingRecord> trackingRecords = aTrackingRecordsList(
                aTrackingRecordWith10MinDuration(),
                aTrackingRecordWith10MinDuration()
        );
        Completion subject = new Completion(
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

    @Test
    public void handlesOverbookedProjectCorrectly() {
        // given
        List<TrackingRecord> trackingRecords = aTrackingRecordsList(
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
        Completion subject = new Completion(configuration,
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
                Optional.of(1),
                Optional.<Date>absent(),
                Optional.<Date>absent(),
                true,
                Rounding.Strategy.NO_ROUNDING);
    }

    private TrackingConfiguration aTrackingConfigurationWithoutLimit() {
        return new TrackingConfiguration("uuid",
                "some project uuid",
                Optional.<Integer>absent(),
                Optional.<Date>absent(),
                Optional.<Date>absent(),
                true,
                Rounding.Strategy.NO_ROUNDING);
    }


    private List<TrackingRecord> aTrackingRecordsList(TrackingRecord... trackingRecords) {
        return Arrays.asList(trackingRecords);
    }

    private TrackingRecord aTrackingRecordWith10MinDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 40, 0);

        return new TrackingRecord(
                "uuid",
                "some project uuid",
                Optional.of(start.toDate()),
                Optional.of(stop.toDate()),
                Optional.of("some description"),
                Rounding.Strategy.NO_ROUNDING
        );
    }
}