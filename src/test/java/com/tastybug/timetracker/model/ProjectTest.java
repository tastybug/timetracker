package com.tastybug.timetracker.model;

import android.content.Context;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.dao.DAOFactory;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.model.rounding.Rounding;

import org.junit.Test;

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

public class ProjectTest {

    @Test
    public void canCreateProjectWithTitle() {
        // when
        Project project = new Project("project title");

        // then
        assertNotNull(project);
        assertEquals("project title", project.getTitle());
    }

    @Test
    public void noProjectDescriptionIsHandledWell() {
        // given
        Project project = new Project("project title");

        // when
        project.setDescription(Optional.of("bla"));

        // then
        assertEquals("bla", project.getDescription().orNull());

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

    @Test
    public void canLazilyGetTrackingRecords() {
        // given
        DAOFactory daoFactory = mock(DAOFactory.class);
        TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);
        when(daoFactory.getDao(eq(TrackingRecord.class), isA(Context.class))).thenReturn(trackingRecordDAO);
        Project project = new Project("project title");
        project.setDAOFactory(daoFactory);

        // when
        project.getTrackingRecords(mock(Context.class));

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

    @Test
    public void canLazilyGetTrackingConfiguration() {
        // given
        DAOFactory daoFactory = mock(DAOFactory.class);
        TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
        when(daoFactory.getDao(eq(TrackingConfiguration.class), isA(Context.class))).thenReturn(trackingConfigurationDAO);
        Project project = new Project("project title");
        project.setDAOFactory(daoFactory);
        TrackingConfiguration expectedConfiguration = new TrackingConfiguration("1", project.getUuid(), Optional.<Integer>absent(), Optional.<Date>absent(), Optional.<Date>absent(), false, Rounding.Strategy.NO_ROUNDING);
        when(trackingConfigurationDAO.getByProjectUuid(project.getUuid())).thenReturn(Optional.of(expectedConfiguration));

        // when
        TrackingConfiguration trackingConfiguration = project.getTrackingConfiguration(mock(Context.class));

        // then
        assertEquals(expectedConfiguration, trackingConfiguration);

        // and
        verify(trackingConfigurationDAO, times(1)).getByProjectUuid(project.getUuid());
    }
}