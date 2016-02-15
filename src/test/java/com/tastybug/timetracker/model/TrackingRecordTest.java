package com.tastybug.timetracker.model;


import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.database.dao.DAOFactory;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class TrackingRecordTest {

    Context contextMock = mock(Context.class);
    DAOFactory daoFactory = mock(DAOFactory.class);
    TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);

    @Before
    public void setup() {
        when(daoFactory.getDao(eq(TrackingRecord.class), isA(Context.class))).thenReturn(trackingRecordDAO);
    }

    @Test
    public void canStartATrackingRecordAndStopItLater() {
        // given:
        TrackingRecord trackingRecord = new TrackingRecord();
        assertFalse(trackingRecord.getStart().isPresent());
        assertFalse(trackingRecord.isRunning());
        assertFalse(trackingRecord.getEnd().isPresent());

        // when
        trackingRecord.start();

        // then
        assertTrue(trackingRecord.getStart().isPresent());
        assertTrue(trackingRecord.isRunning());
        assertFalse(trackingRecord.getEnd().isPresent());

        // when
        trackingRecord.stop();

        // then
        assertTrue(trackingRecord.getStart().isPresent());
        assertTrue(trackingRecord.getEnd().isPresent());
        assertFalse(trackingRecord.isRunning());

        // and
        assertNotNull(trackingRecord.toDuration().get());
    }

    @Test
    public void canSetDescriptionAtTrackingRecord() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        assertFalse(trackingRecord.getDescription().isPresent());

        // when
        trackingRecord.setDescription(Optional.of("bla"));

        // then
        assertEquals("bla", trackingRecord.getDescription().get());
    }

    @Test(expected = IllegalStateException.class)
    public void startingAnAlreadyStartedTrackingRecordYieldsException() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.start();

        // when
        trackingRecord.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stoppingAnAlreadyStoppedTrackingRecordYieldsException() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        trackingRecord.start();
        trackingRecord.stop();

        // when
        trackingRecord.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void canNotStopBeforeStarting() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecord.stop();
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullProjectUuid() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();

        // when
        trackingRecord.setProjectUuid(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullStartDate() {
        // expect
        new TrackingRecord().setStart(null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullEndDate() {
        // expect
        new TrackingRecord().setEnd(null);
    }

    @Test
    public void canOnlyGetAsJodaDurationWhenFinished() {
        // given
        TrackingRecord trackingRecord = new TrackingRecord();
        assertFalse(trackingRecord.toDuration().isPresent());

        // when
        trackingRecord.start();

        // then
        assertFalse(trackingRecord.toDuration().isPresent());

        // when
        trackingRecord.stop();

        // then
        assertTrue(trackingRecord.toDuration().isPresent());
    }

    @Test public void fieldChangesLeadToDatabaseUpdates() {
        // given
        TrackingRecord TrackingRecord = new TrackingRecord();
        TrackingRecord.setContext(contextMock);
        TrackingRecord.setDAOFactory(daoFactory);

        // when
        TrackingRecord.setUuid("10000"); // this does not trigger
        TrackingRecord.start();
        TrackingRecord.stop();
        TrackingRecord.setDescription(Optional.of("blablabla"));

        // then
        verify(trackingRecordDAO, times(3)).update(TrackingRecord);
    }
}