package com.tastybug.timetracker.model;


import com.google.common.base.Optional;
import com.tastybug.timetracker.util.DateProvider;

import org.apache.commons.lang3.SerializationUtils;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackingRecordTest {

    private static final int EXPECTED_VERYSHORT_LIMIT_IN_MINUTES = 2;

    @Test
    public void can_start_and_stop() {
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
    public void can_set_description() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        assertFalse(trackingRecord.getDescription().isPresent());

        // when
        trackingRecord.setDescription(Optional.of("bla"));

        // then
        assertEquals("bla", trackingRecord.getDescription().orNull());
    }

    @Test(expected = IllegalStateException.class)
    public void starting_twice_yields_IllegalStateException() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.start();

        // when
        trackingRecord.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stopping_twice_yields_IllegalStateException() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.start();
        trackingRecord.stop();

        // when
        trackingRecord.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void stopping_when_not_running_yields_IllegalStateException() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecord.stop();
    }

    @Test(expected = NullPointerException.class)
    public void setProjectUuid_with_null_uuid_yields_NPE() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecord.setProjectUuid(null);
    }

    @Test(expected = NullPointerException.class)
    public void setStart_with_null_argument_yields_NPE() {
        // expect
        new TrackingRecord().setStart(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStart_yields_IllegalArgument_when_start_is_not_before_end() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setEnd(new Date(1));

        // when
        trackingRecord.setStart(new Date(1));
    }

    @Test(expected = NullPointerException.class)
    public void setEnd_with_null_argument_yields_NPE() {
        // expect
        new TrackingRecord().setEnd(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEnd_yields_IllegalArgument_when_end_not_after_start() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setStart(new Date(1));

        // when
        trackingRecord.setEnd(new Date(1));
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
    public void toDuration_returns_nothing_when_not_started() {
        // expect
        assertFalse(new TrackingRecord().toDuration().isPresent());
    }

    @Test
    public void toDuration_returns_correctly_when_running() {
        // given
        DateProvider dateProvider = mock(DateProvider.class);
        when(dateProvider.getCurrentDate()).thenReturn(new Date(1000), new Date(2000));
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setDateProvider(dateProvider);

        // when
        trackingRecord.start();

        // then
        assertEquals(1000, trackingRecord.toDuration().get().getMillis());
    }

    @Test
    public void toDuration_returns_non_null_when_finished() {
        // given
        DateProvider dateProvider = mock(DateProvider.class);
        when(dateProvider.getCurrentDate()).thenReturn(new Date(1000), new Date(2000));
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.setDateProvider(dateProvider);

        // when
        trackingRecord.start();
        trackingRecord.stop();

        // then
        assertEquals(1000, trackingRecord.toDuration().get().getMillis());
    }

    @Test
    public void setDescription_stores_empty_strings_as_non_existent_descriptions() {
        // given
        TrackingRecord record = new TrackingRecord();
        record.setDescription(Optional.of(""));

        // expect
        assertFalse(record.getDescription().isPresent());
    }

    @Test
    public void isVeryShort_returns_false_when_TR_is_finished_but_overly_long() {
        // given
        DateProvider dateProvider = mock(DateProvider.class);
        when(dateProvider.getCurrentDate()).thenReturn(new LocalDateTime(2016, 12, 24, 12, 1, 0).toDate());
        TrackingRecord record = new TrackingRecord();
        record.setDateProvider(dateProvider);
        record.setStart(new LocalDateTime(2016, 12, 24, 12, 0, 0).toDate());

        // when
        record.setEnd(new LocalDateTime(2016, 12, 24, 12, 5, 0).toDate());

        // expect
        assertFalse(record.isVeryShort());
    }

    @Test
    public void isVeryShort_returns_true_for_ongoing_when_current_duration_is_short_enough() {
        // given
        DateProvider dateProvider = mock(DateProvider.class);
        when(dateProvider.getCurrentDate()).thenReturn(new LocalDateTime(2016, 12, 24, 12, EXPECTED_VERYSHORT_LIMIT_IN_MINUTES - 1, 0).toDate());
        TrackingRecord record = new TrackingRecord();
        record.setDateProvider(dateProvider);

        // expect: not started yet, so no duration to check for
        assertFalse(record.isVeryShort());

        // when
        record.setStart(new LocalDateTime(2016, 12, 24, 12, 0, 0).toDate());

        // and
        record.setEnd(new LocalDateTime(2016, 12, 24, 12, EXPECTED_VERYSHORT_LIMIT_IN_MINUTES, 0).toDate());

        // expect
        assertTrue(record.isVeryShort());
    }

    @Test
    public void isVeryShort_returns_true_when_TR_is_not_finished_but_current_date_would_count_as_short() {
        // given
        DateProvider dateProvider = mock(DateProvider.class);
        when(dateProvider.getCurrentDate()).thenReturn(new LocalDateTime(2016, 12, 24, 12, 1, 0).toDate());
        TrackingRecord record = new TrackingRecord();
        record.setDateProvider(dateProvider);

        // when
        record.setStart(new LocalDateTime(2016, 12, 24, 12, 0, 0).toDate());

        // expect: its not finished yet...
        assertTrue(record.isVeryShort());
    }

    @Test
    public void can_serialize() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord("proj", "uuid", Optional.of(new Date(1)), Optional.of(new Date(2)), Optional.of(""));

        // when: this is supposed to cause no exception
        SerializationUtils.serialize(trackingRecord);
    }
}