package com.tastybug.timetracker.model;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TimeFrameTest {

    @Test
    public void canStartATimeframeAndStopItLater() {
        // given:
        TimeFrame timeFrame = new TimeFrame();
        assertFalse(timeFrame.hasStart());
        assertFalse(timeFrame.isRunning());
        assertFalse(timeFrame.hasEnd());

        // when
        timeFrame.start();

        // then
        assertTrue(timeFrame.hasStart());
        assertTrue(timeFrame.isRunning());
        assertFalse(timeFrame.hasEnd());

        // when
        timeFrame.stop();

        // then
        assertTrue(timeFrame.hasStart());
        assertTrue(timeFrame.hasEnd());
        assertFalse(timeFrame.isRunning());

        // and
        assertNotNull(timeFrame.toDuration().get());
    }

    @Test
    public void canSetDescriptionAtTimeframe() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        assertNull(timeFrame.getDescription());

        // when
        timeFrame.setDescription("bla");

        // then
        assertEquals("bla", timeFrame.getDescription());
    }

    @Test(expected = IllegalStateException.class)
    public void startingAnAlreadyStartedTimeframeYieldsException() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        timeFrame.start();

        // when
        timeFrame.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stoppingAnAlreadyStoppedTimeframeYieldsException() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        timeFrame.start();
        timeFrame.stop();

        // when
        timeFrame.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void canNotStopBeforeStarting() {
        // given
        TimeFrame timeFrame = new TimeFrame();

        // when
        timeFrame.stop();
    }

    @Test
    public void canOnlyGetAsJodaDurationWhenFinished() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        assertFalse(timeFrame.toDuration().isPresent());

        // when
        timeFrame.start();

        // then
        assertFalse(timeFrame.toDuration().isPresent());

        // when
        timeFrame.stop();

        // then
        assertTrue(timeFrame.toDuration().isPresent());
    }
}