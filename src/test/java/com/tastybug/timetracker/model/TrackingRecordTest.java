package com.tastybug.timetracker.model;


import com.google.common.base.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TrackingRecordTest {

    @Test
    public void canStartATrackingRecordAndStopItLater() {
        // given:
        TrackingRecord trackingRecord = new TrackingRecord();
        assertFalse(trackingRecord.getStart().isPresent());
        assertFalse(trackingRecord.getEnd().isPresent());

        // when
        trackingRecord.start();

        // then
        assertTrue(trackingRecord.getStart().isPresent());
        assertFalse(trackingRecord.getEnd().isPresent());

        // when
        trackingRecord.stop();

        // then
        assertTrue(trackingRecord.getStart().isPresent());
        assertTrue(trackingRecord.getEnd().isPresent());
    }

    @Test
    public void canSetDescriptionAtTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        assertFalse(trackingRecord.getDescription().isPresent());

        // when
        trackingRecord.setDescription(Optional.of("bla"));

        // then
        assertEquals("bla", trackingRecord.getDescription().orNull());
    }

    @Test(expected = IllegalStateException.class)
    public void startingAnAlreadyStartedTrackingRecordYieldsException() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.start();

        // when
        trackingRecord.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stoppingAnAlreadyStoppedTrackingRecordYieldsException() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.start();
        trackingRecord.stop();

        // when
        trackingRecord.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void canNotStopBeforeStarting() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecord.stop();
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectUuid() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecord.setProjectUuid(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetNullStartDate() {
        // expect
        new TrackingRecord().setStart(Optional.<Date>absent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetNoEndDate() {
        // expect
        new TrackingRecord().setEnd(Optional.<Date>absent());
    }

    @Test
    public void canTellIfIsRunningOrFinished() {
        // given:
        TrackingRecord trackingRecord;

        // when
        trackingRecord = new TrackingRecord();

        // then
        assertFalse(trackingRecord.isRunning());
        assertFalse(trackingRecord.isFinished());

        // when
        trackingRecord.start();

        // then
        assertTrue(trackingRecord.isRunning());
        assertFalse(trackingRecord.isFinished());

        // when
        trackingRecord.stop();

        // then
        assertFalse(trackingRecord.isRunning());
        assertTrue(trackingRecord.isFinished());
    }

    @Test
    public void canJodaDurationWhenRunningOrFinished() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        assertFalse(trackingRecord.toDuration().isPresent());

        // when
        trackingRecord.start();

        // then
        assertTrue(trackingRecord.toDuration().isPresent());

        // when
        trackingRecord.stop();

        // then
        assertTrue(trackingRecord.toDuration().isPresent());
    }

    @Test
    public void anEmptyDescriptionIsLikeANullDescription() {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setDescription(Optional.of(""));

        // expect
        assertFalse(record.getDescription().isPresent());
    }

    @Test
    public void canTellWhetherItsVeryShort() {
        // given
        TrackingRecord record = new TrackingRecord();

        // expect: not started yet, so no duration to check for
        assertFalse(record.isVeryShort());

        // when
        record.setStart(Optional.of(new LocalDateTime(2016, 12, 24, 12, 0, 0).toDate()));

        // expect: even when not finished yet, it counts as very short
        assertTrue(record.isVeryShort());

        // when: its very short
        record.setEnd(Optional.of(new LocalDateTime(2016, 12, 24, 12, TrackingRecord.MINUTES_LIMIT_FOR_TINY_RECORDS, 0).toDate()));

        // expect
        assertTrue(record.isVeryShort());

        // when: its not very short
        record.setEnd(Optional.of(new LocalDateTime(2016, 12, 24, 12, 1 + TrackingRecord.MINUTES_LIMIT_FOR_TINY_RECORDS, 0).toDate()));

        // expect
        assertFalse(record.isVeryShort());
    }

    @Test
    public void can_serialize() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("proj", "uuid", Optional.of(new Date(1)), Optional.of(new Date(2)), Optional.of(""));

        // when: this is supposed to cause no exception
        SerializationUtils.serialize(trackingRecord);
    }
}