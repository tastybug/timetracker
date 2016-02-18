package com.tastybug.timetracker.facade;

import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.tracking.KickstartTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.ModifyTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.TrackingTaskFactory;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class TrackingFacadeTest extends TestCase {

    Context context = mock(Context.class);
    TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    TrackingTaskFactory taskFactory = mock(TrackingTaskFactory.class);
    TrackingFacade testSubject = new TrackingFacade(context, trackingRecordDAO, taskFactory);

    @Test public void startTrackingForProjectRunsTaskCorrectly() {
        // given
        KickstartTrackingRecordTask taskMock = mock(KickstartTrackingRecordTask.class);
        when(taskFactory.aKickstartTask(isA(Context.class))).thenReturn(taskMock);
        when(taskMock.withProjectUuid(any(String.class))).thenReturn(taskMock);

        // when
        testSubject.startTracking("project-uuid");

        // then
        verify(taskMock, times(1)).withProjectUuid(eq("project-uuid"));
        verify(taskMock, times(1)).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void startingTrackingWithNullProjectUuidYieldsException() {
        // expect
        testSubject.startTracking(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void startingTrackingWithEmptyProjectUuidYieldsException() {
        // expect
        testSubject.startTracking("");
    }

    @Test public void stopTrackingForProjectRunsTaskCorrectly() {
        // given
        ModifyTrackingRecordTask taskMock = mock(ModifyTrackingRecordTask.class);
        when(taskFactory.aModificationTask(isA(Context.class))).thenReturn(taskMock);
        when(taskMock.withStoppableProjectUuid(any(String.class))).thenReturn(taskMock);

        // when
        testSubject.stopTracking("project-uuid");

        // then
        verify(taskMock, times(1)).withStoppableProjectUuid(eq("project-uuid"));
        verify(taskMock, times(1)).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void stoppingTrackingWithNullProjectUuidYieldsException() {
        // expect
        testSubject.stopTracking(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void stoppingTrackingWithEmptyProjectUuidYieldsException() {
        // expect
        testSubject.stopTracking("");
    }

    @Test public void canGetRunningTrackingRecordForProject() {
        // given
        when(trackingRecordDAO.getRunning(any(String.class))).thenReturn(Optional.of(mock(TrackingRecord.class)));

        // when
        testSubject.getOngoingTracking("project-uuid");

        // then
        verify(trackingRecordDAO, times(1)).getRunning(eq("project-uuid"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkForRunningTrackingWithNullProjectUuidYieldsException() {
        // expect
        testSubject.getOngoingTracking(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkForRunningTrackingWithEmptyProjectUuidYieldsException() {
        // expect
        testSubject.getOngoingTracking("");
    }
}