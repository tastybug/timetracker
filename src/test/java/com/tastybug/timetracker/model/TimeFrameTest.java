package com.tastybug.timetracker.model;


import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class TimeFrameTest {

    Context contextMock = mock(Context.class);
    TimeFrameDAO daoMock = mock(TimeFrameDAO.class);

    @Test
    public void canStartATimeframeAndStopItLater() {
        // given:
        TimeFrame timeFrame = new TimeFrame();
        assertFalse(timeFrame.getStart().isPresent());
        assertFalse(timeFrame.isRunning());
        assertFalse(timeFrame.getEnd().isPresent());

        // when
        timeFrame.start();

        // then
        assertTrue(timeFrame.getStart().isPresent());
        assertTrue(timeFrame.isRunning());
        assertFalse(timeFrame.getEnd().isPresent());

        // when
        timeFrame.stop();

        // then
        assertTrue(timeFrame.getStart().isPresent());
        assertTrue(timeFrame.getEnd().isPresent());
        assertFalse(timeFrame.isRunning());

        // and
        assertNotNull(timeFrame.toDuration().get());
    }

    @Test
    public void canSetDescriptionAtTimeFrame() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        assertFalse(timeFrame.getDescription().isPresent());

        // when
        timeFrame.setDescription(Optional.of("bla"));

        // then
        assertEquals("bla", timeFrame.getDescription().get());
    }

    @Test(expected = IllegalStateException.class)
    public void startingAnAlreadyStartedTimeFrameYieldsException() {
        // given
        TimeFrame timeFrame = new TimeFrame();
        timeFrame.start();

        // when
        timeFrame.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stoppingAnAlreadyStoppedTimeFrameYieldsException() {
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

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectUuid() {
        // given
        TimeFrame timeFrame = new TimeFrame();

        // when
        timeFrame.setProjectUuid(null);
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

    @Test public void fieldChangesLeadToDatabaseUpdates() {
        // given
        TimeFrame timeframe = new TimeFrame();
        timeframe.setContext(contextMock);
        timeframe.setDAO(daoMock);

        // when
        timeframe.setUuid("10000"); // this does not trigger
        timeframe.start();
        timeframe.stop();
        timeframe.setDescription(Optional.of("blablabla"));

        // then
        verify(daoMock, times(3)).update(timeframe);
    }
}