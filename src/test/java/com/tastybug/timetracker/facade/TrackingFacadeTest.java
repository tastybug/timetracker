package com.tastybug.timetracker.facade;

import android.content.Context;
import android.os.Build;

import com.tastybug.timetracker.database.dao.TimeFrameDAO;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class TrackingFacadeTest extends TestCase {

    Context context = mock(Context.class);
    TimeFrameDAO timeFrameDAO = mock(TimeFrameDAO.class);
    TrackingFacade testSubject = new TrackingFacade(context, timeFrameDAO);

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

    @Test(expected = IllegalArgumentException.class)
    public void checkForRunningTrackingWithNullProjectUuidYieldsException() {
        // expect
        testSubject.isTracking(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkForRunningTrackingWithEmptyProjectUuidYieldsException() {
        // expect
        testSubject.isTracking("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingTimeFramesWithNullProjectUuidYieldsException() {
        // expect
        testSubject.getTimeFrames(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void gettingTimeFramesWithEmptyProjectUuidYieldsException() {
        // expect
        testSubject.getTimeFrames("");
    }

}