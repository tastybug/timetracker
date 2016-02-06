package com.tastybug.timetracker.model.rounding;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XMinutesUpTest extends AbstractRoundingTestcase {

    @Test
    public void willRoundUpToFullMinute() {
        // given: a duration of 1:10min
        Duration duration = getOneMinute10SecondsDuration();

        // when
        long seconds = XMinutesUp.fullMinutesUp().getDurationInSeconds(duration);

        // then: its rounded to 2 minutes, equals 120 seconds
        assertEquals(120, seconds);
    }

    @Test
    public void underOneMinuteEqualsOneMinute() {
        // given: a duration of 10 seconds
        Duration duration = get10SecondsDuration();

        // when
        long seconds = XMinutesUp.fullMinutesUp().getDurationInSeconds(duration);

        // then: its rounded to 1 full minute, equals 60 seconds
        assertEquals(60, seconds);
    }

    @Test
    public void exactlyOneMinuteLeadsToUnalteredResult() {
        // given: a duration of 60 seconds
        Duration duration = get1MinuteDuration();

        // when
        long seconds = XMinutesUp.fullMinutesUp().getDurationInSeconds(duration);

        // then: we get 60 seconds
        assertEquals(60, seconds);
    }

    @Test
    public void willRound12MinutesUpTo20Minutes() {
        // given: a duration 12:10min
        Duration duration = get12Minutes10SecondsDuration();

        // when
        long seconds = XMinutesUp.tenMinutesUp().getDurationInSeconds(duration);

        // then: its rounded to 20 minutes
        assertEquals(20*60, seconds);
    }

    @Test
    public void willRound9MinutesUpTo10Minutes() {
        // given: a duration of 9 minutes
        Duration duration = get9MinutesDuration();

        // when
        long seconds = XMinutesUp.tenMinutesUp().getDurationInSeconds(duration);

        // then: its rounded to 10 minutes
        assertEquals(10*60, seconds);
    }

    @Test
    public void exactly10MinutesLeadsToUnalteredResult() {
        // given: a duration of 10 seconds
        Duration duration = get10MinutesDuration();

        // when
        long seconds = XMinutesUp.tenMinutesUp().getDurationInSeconds(duration);

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