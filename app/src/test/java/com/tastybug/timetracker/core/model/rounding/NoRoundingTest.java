package com.tastybug.timetracker.core.model.rounding;

import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NoRoundingTest extends AbstractRoundingTestcase {

    private NoRounding SUBJECT = new NoRounding();

    @Test
    public void willRoundUpToFullMinute() {
        // given: a duration of 1:10min
        Duration duration = getOneMinute10SecondsDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then
        assertEquals(70, seconds);
    }

    @Test
    public void underOneMinuteEqualsOneMinute() {
        // given: a duration of 10 seconds
        Duration duration = get10SecondsDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then
        assertEquals(10, seconds);
    }

    @Test
    public void exactlyOneMinuteLeadsToUnalteredResult() {
        // given: a duration of 60 seconds
        Duration duration = get1MinuteDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then
        assertEquals(60, seconds);
    }
}