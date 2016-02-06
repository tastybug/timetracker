package com.tastybug.timetracker.model.rounding;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ThirtyMinutesUpTest {

    private XMinutesUp SUBJECT = XMinutesUp.thirtyMinutesUp();

    @Test
    public void willRound30Minutes1SecondUpTo60Minutes() {
        // given: a duration 30:01min
        Duration duration = get30Minutes1SecondDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its rounded to 20 minutes
        assertEquals(60*60, seconds);
    }

    @Test
    public void willRound29MinutesUpTo30Minutes() {
        // given: a duration of 29 minutes
        Duration duration = get29MinutesDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its rounded to 30 minutes
        assertEquals(30*60, seconds);
    }

    @Test
    public void exactly10MinutesLeadsToUnalteredResult() {
        // given: a duration of 30 minutes
        Duration duration = get30MinutesDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its not altered
        assertEquals(30*60, seconds);
    }

    protected Duration get29MinutesDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 15, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 44, 0);

        return new Duration(start, stop);
    }

    protected Duration get30MinutesDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 15, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 45, 0);

        return new Duration(start, stop);
    }

    protected Duration get30Minutes1SecondDuration() {
        DateTime start = new DateTime(2015, 1, 1, 13, 15, 0);
        DateTime stop = new DateTime(2015, 1, 1, 13, 45, 1);

        return new Duration(start, stop);
    }
}