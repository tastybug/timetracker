package com.tastybug.timetracker.model.rounding;

import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FullMinuteUpTest extends AbstractRoundingTestcase {

    private FullMinuteUp SUBJECT = new FullMinuteUp();

    @Test
    public void willRoundUpToFullMinute() {
        // given: a duration of 1:10min
        Duration duration = getOneMinute10SecondsDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its rounded to 2 minutes, equals 120 seconds
        assertEquals(120, seconds);
    }

    @Test
    public void underOneMinuteEqualsOneMinute() {
        // given: a duration of 10 seconds
        Duration duration = get10SecondsDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: its rounded to 1 full minute, equals 60 seconds
        assertEquals(60, seconds);
    }

    @Test
    public void exactlyOneMinuteLeadsToUnalteredResult() {
        // given: a duration of 60 seconds
        Duration duration = get1MinuteDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then: we get 60 seconds
        assertEquals(60, seconds);
    }
}