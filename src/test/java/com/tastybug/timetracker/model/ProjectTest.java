package com.tastybug.timetracker.model;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.database.dao.DAOFactory;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TimeFrameDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = "target/filtered-manifest/AndroidManifest.xml")
public class ProjectTest {

    Context context = mock(Context.class);
    DAOFactory daoFactory = mock(DAOFactory.class);
    ProjectDAO projectDAO = mock(ProjectDAO.class);
    TimeFrameDAO timeFrameDAO = mock(TimeFrameDAO.class);
    TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    ContentResolver contentResolver = mock(ContentResolver.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(contentResolver);
        when(daoFactory.getDao(eq(Project.class), isA(Context.class))).thenReturn(projectDAO);
        when(daoFactory.getDao(eq(TimeFrame.class), isA(Context.class))).thenReturn(timeFrameDAO);
        when(daoFactory.getDao(eq(TrackingConfiguration.class), isA(Context.class))).thenReturn(trackingConfigurationDAO);
    }

    @Test public void canCreateProjectWithTitle() {
        // when
        Project project = new Project("project title");

        // then
        assertNotNull(project);
        assertEquals("project title", project.getTitle());
    }

    @Test public void noProjectDescriptionIsHandledWell() {
        // given
        Project project = new Project("project title");

        // when
        project.setDescription(Optional.of("bla"));

        // then
        assertEquals("bla", project.getDescription().get());

        // when
        project.setDescription(Optional.<String>absent());

        // then
        assertFalse(project.getDescription().isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void canNotSetNullUuid() {
        // given
        Project project = new Project("project title");

        // when
        project.setUuid(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetNullProjectTitle() {
        // given
        Project project = new Project("project title");

        // when
        project.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canNotSetEmptyProjectTitle() {
        // given
        Project project = new Project("project title");

        // when
        project.setTitle("");
    }

    @Test(expected = IllegalStateException.class)
    public void lazilyGettingTimeFramesWithoutContextYieldsException() {
        // given
        Project project = new Project("project title");

        // when
        project.getTimeFrames();
    }

    @Test public void canLazilyGetTimeFrames() {
        // given
        Project project = new Project("project title");
        project.setContext(context);
        project.setDAOFactory(daoFactory);

        // when
        project.getTimeFrames();

        // then
        verify(timeFrameDAO, times(1)).getByProjectUuid(project.getUuid());
    }

    @Test(expected = IllegalStateException.class)
    public void creatingTimeFramesWithoutContextYieldsException() {
        // given
        Project project = new Project("project title");

        // when
        project.createTimeFrame();
    }

    @Test public void canCreateTimeframes() {
        // given
        Project project = new Project("project title");
        project.setContext(context);
        project.setDAOFactory(daoFactory);

        // when
        TimeFrame timeFrame = project.createTimeFrame();

        // then
        assertNotNull(timeFrame);
        assertEquals(project.getUuid(), timeFrame.getProjectUuid());

        // and: the project contains the time frame
        project.getTimeFrames().contains(timeFrame);

        // and
        verify(timeFrameDAO, times(1)).getByProjectUuid(project.getUuid());
        verify(timeFrameDAO, times(1)).create(timeFrame);
    }

    @Test(expected = IllegalStateException.class)
    public void removingTimeFramesWithoutContextYieldsException() {
        // given
        Project project = new Project("project title");

        // when
        project.removeTimeFrame(new TimeFrame());
    }

    @Test(expected = NullPointerException.class)
    public void removingNullTimeFrameYieldsException() {
        // given
        Project project = new Project("project title");
        project.setContext(context);

        // when
        project.removeTimeFrame(null);
    }

    @Test public void removingUnknownTimeFrameYieldsFalse() {
        // given
        Project project = new Project("project title");
        project.setContext(context);
        project.setDAOFactory(daoFactory);
        when(timeFrameDAO.getByProjectUuid(project.getUuid())).thenReturn(new ArrayList<TimeFrame>());

        // when
        boolean result = project.removeTimeFrame(new TimeFrame());

        // then
        assertFalse(result);

        // and
        verify(timeFrameDAO, never()).delete(any(TimeFrame.class));
    }

    @Test public void canRemoveTimeFrame() {
        // given
        Project project = new Project("project title");
        project.setContext(context);
        project.setDAOFactory(daoFactory);
        ArrayList<TimeFrame> timeFrames = twoTimeFrames(project);
        when(timeFrameDAO.getByProjectUuid(project.getUuid())).thenReturn(timeFrames);
        TimeFrame timeFrame1 = timeFrames.get(0);
        TimeFrame timeFrame2 = timeFrames.get(1);

        // when
        boolean success = project.removeTimeFrame(timeFrame1);

        // then
        assertTrue(success);

        // and
        assertFalse(project.getTimeFrames().contains(timeFrame1));

        // and
        assertFalse(project.getTimeFrames().isEmpty());

        // and
        verify(timeFrameDAO, times(1)).delete(timeFrame1);

        // when
        success = project.removeTimeFrame(timeFrame2);

        // then
        assertTrue(success);

        // and
        assertTrue(project.getTimeFrames().isEmpty());

        // and
        verify(timeFrameDAO, times(1)).delete(timeFrame2);
    }

    @Test(expected = IllegalStateException.class)
    public void gettingTrackingConfigurationWithoutContextYieldsException() {
        // given
        Project project = new Project("project title");

        // when
        project.getTrackingConfiguration();
    }

    @Test public void canGetTrackingConfiguration() {
        // given
        Project project = new Project("project title");
        project.setContext(context);
        project.setDAOFactory(daoFactory);
        TrackingConfiguration expectedConfiguration = new TrackingConfiguration("1", project.getUuid(), null, null, null, TimeFrameRounding.Strategy.NO_ROUNDING);
        when(trackingConfigurationDAO.getByProjectUuid(project.getUuid())).thenReturn(Optional.of(expectedConfiguration));

        // when
        TrackingConfiguration trackingConfiguration = project.getTrackingConfiguration();

        // then
        assertEquals(expectedConfiguration, trackingConfiguration);

        // and
        verify(trackingConfigurationDAO, times(1)).getByProjectUuid(project.getUuid());
    }

    @Test public void fieldChangesLeadToDatabaseUpdates() {
        // given
        Project project = new Project("project title");
        project.setContext(context);
        project.setDAOFactory(daoFactory);

        // when
        project.setUuid("123"); // this does not trigger
        project.setTitle("new title");
        project.setDescription(Optional.of("bla"));

        // then
        verify(projectDAO, times(2)).update(project);
    }

    private ArrayList<TimeFrame> twoTimeFrames(Project project) {
        ArrayList<TimeFrame> list = new ArrayList<TimeFrame>();

        list.add(new TimeFrame("1", project.getUuid(), null, null));
        list.add(new TimeFrame("2", project.getUuid(), null, null));

        return list;
    }
}