package com.tastybug.timetracker.model.rounding;

import org.joda.time.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FullMinuteDownTest extends AbstractRoundingTestcase {

    private FullMinuteDown SUBJECT = new FullMinuteDown();

    @Test
    public void willRoundDownToFullMinute() {
        // given
        Duration duration = getOneMinute10SecondsDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then
        assertEquals(60, seconds);
    }

    @Test
    public void underOneMinuteEqualsEmptyDuration() {
        // given
        Duration duration = get10SecondsDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then
        assertEquals(0, seconds);
    }

    @Test
    public void exactlyOneMinuteLeadsToUnalteredResult() {
        // given
        Duration duration = get1MinuteDuration();

        // when
        long seconds = SUBJECT.getDurationInSeconds(duration);

        // then
        assertEquals(60, seconds);
    }
}