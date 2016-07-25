package com.tastybug.timetracker.model;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.dao.DAOFactory;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.rounding.RoundingFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ProjectTest {

    Context context = mock(Context.class);
    DAOFactory daoFactory = mock(DAOFactory.class);
    ProjectDAO projectDAO = mock(ProjectDAO.class);
    TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
    TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    ContentResolver contentResolver = mock(ContentResolver.class);

    @Before
    public void setup() {
        when(context.getContentResolver()).thenReturn(contentResolver);
        when(daoFactory.getDao(eq(Project.class), isA(Context.class))).thenReturn(projectDAO);
        when(daoFactory.getDao(eq(TrackingRecord.class), isA(Context.class))).thenReturn(trackingRecordDAO);
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

    @Test(expected = NullPointerException.class)
    public void canNotSetNullDescription() {
        // given
        Project project = new Project("project title");

        // when
        project.setDescription(null);
    }

    @Test(expected = NullPointerException.class)
    public void lazilyGettingTrackingRecordsWithoutContextYieldsException() {
        // given
        Project project = new Project("project title");

        // when
        project.getTrackingRecords(null);
    }

    @Test public void canLazilyGetTrackingRecords() {
        // given
        Project project = new Project("project title");
        project.setDAOFactory(daoFactory);

        // when
        project.getTrackingRecords(context);

        // then
        verify(trackingRecordDAO, times(1)).getByProjectUuid(project.getUuid());
    }

    @Test(expected = NullPointerException.class)
    public void lazilyGettingTrackingConfigurationWithoutContextYieldsException() {
        // given
        Project project = new Project("project title");

        // when
        project.getTrackingConfiguration(null);
    }

    @Test public void canLazilyGetTrackingConfiguration() {
        // given
        Project project = new Project("project title");
        project.setDAOFactory(daoFactory);
        TrackingConfiguration expectedConfiguration = new TrackingConfiguration("1", project.getUuid(), Optional.<Integer>absent(), Optional.<Date>absent(), Optional.<Date>absent(), false, RoundingFactory.Strategy.NO_ROUNDING);
        when(trackingConfigurationDAO.getByProjectUuid(project.getUuid())).thenReturn(Optional.of(expectedConfiguration));

        // when
        TrackingConfiguration trackingConfiguration = project.getTrackingConfiguration(context);

        // then
        assertEquals(expectedConfiguration, trackingConfiguration);

        // and
        verify(trackingConfigurationDAO, times(1)).getByProjectUuid(project.getUuid());
    }
}