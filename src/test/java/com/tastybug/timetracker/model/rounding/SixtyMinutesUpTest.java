package com.tastybug.timetracker.model.rounding;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SixtyMinutesUpTest {

    private XMinutesUp SUBJECT = XMinutesUp.fullHoursUp();

    @Test
    public void willRound60Minutes1SecondUpTo60Minutes() {
        // given: a duration 60:01min
        Duration duration = get60Minutes1SecondDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: rounded to 2 hours
        assertEquals(120*60, seconds);
    }

    @Test
    public void willRound59MinutesUpTo60Minutes() {
        // given: a duration of 59 minutes
        Duration duration = get59MinutesDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its rounded to 1 hour
        assertEquals(60*60, seconds);
    }

    @Test
    public void exactly60MinutesLeadsToUnalteredResult() {
        // given: a duration of 60 minutes
        Duration duration = get60MinutesDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its not altered
        assertEquals(60*60, seconds);
    }

    protected Duration get59MinutesDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 15, 0);
        DateTime stop = new DateTime(2015, 1, 1, 14, 14, 0);

        return new Duration(start, stop);
    }

    protected Duration get60MinutesDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 15, 0);
        DateTime stop = new DateTime(2015, 1, 1, 14, 15, 0);

        return new Duration(start, stop);
    }

    protected Duration get60Minutes1SecondDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 15, 0);
        DateTime stop = new DateTime(2015, 1, 1, 14, 15, 1);

        return new Duration(start, stop);
    }
}