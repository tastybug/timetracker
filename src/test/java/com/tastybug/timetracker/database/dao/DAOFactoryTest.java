package com.tastybug.timetracker.database.dao;

import android.content.Context;
import android.os.Build;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.model.TrackingConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class DAOFactoryTest {

    @Test public void returnsAllExpectedDaos() {
        // expect
        assertTrue(new DAOFactory().getDao(Project.class, mock(Context.class)) instanceof ProjectDAO);
        assertTrue(new DAOFactory().getDao(TrackingConfiguration.class, mock(Context.class)) instanceof TrackingConfigurationDAO);
        assertTrue(new DAOFactory().getDao(TimeFrame.class, mock(Context.class)) instanceof TimeFrameDAO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unexpectedEntityClassYieldsException() {
        // expect
        new DAOFactory().getDao(String.class, mock(Context.class));
    }
}