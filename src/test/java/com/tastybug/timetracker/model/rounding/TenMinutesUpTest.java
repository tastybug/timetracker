package com.tastybug.timetracker.model.rounding;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TenMinutesUpTest {

    private XMinutesUp SUBJECT = XMinutesUp.tenMinutesUp();

    @Test
    public void willRound12MinutesUpTo20Minutes() {
        // given: a duration 12:10min
        Duration duration = get12Minutes10SecondsDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its rounded to 20 minutes
        assertEquals(20*60, seconds);
    }

    @Test
    public void willRound9MinutesUpTo10Minutes() {
        // given: a duration of 9 minutes
        Duration duration = get9MinutesDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its rounded to 10 minutes
        assertEquals(10*60, seconds);
    }

    @Test
    public void exactly10MinutesLeadsToUnalteredResult() {
        // given: a duration of 10 seconds
        Duration duration = get10MinutesDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its not altered
        assertEquals(10*60, seconds);
    }

    protected Duration get9MinutesDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 39, 0);

        return new Duration(start, stop);
    }

    protected Duration get10MinutesDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 40, 0);

        return new Duration(start, stop);
    }

    protected Duration get12Minutes10SecondsDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 30, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 42, 10);

        return new Duration(start, stop);
    }
}