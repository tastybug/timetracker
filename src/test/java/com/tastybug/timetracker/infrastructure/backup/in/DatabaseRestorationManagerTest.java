package com.tastybug.timetracker.infrastructure.backup.in;

import android.os.Build;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class DatabaseRestorationManagerTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);

    private DatabaseRestorationManager subject = new DatabaseRestorationManager(
            projectDAO,
            trackingConfigurationDAO,
            trackingRecordDAO);

    @Test
    public void restoreProjectList_is_noop_when_given_empty_list() {
        // when
        subject.restoreProjectList(new ArrayList<Project>());

        // then
        verifyZeroInteractions(projectDAO);
        verifyZeroInteractions(trackingConfigurationDAO);
        verifyZeroInteractions(trackingRecordDAO);
    }

    @Test(expected = NullPointerException.class)
    public void restoreProjectList_throws_NPE_on_null_argument() {
        // expect
        subject.restoreProjectList(null);
    }

    @Test
    public void restoreProjectList_will_empty_existing_database_beforehand() {
        // given
        ArrayList<Project> existingProjects = aListOf2Projects();
        when(projectDAO.getAll()).thenReturn(existingProjects);

        // when
        subject.restoreProjectList(aListOf2Projects());

        // then
        verify(projectDAO, times(1)).delete(existingProjects.get(0));
        verify(projectDAO, times(1)).delete(existingProjects.get(1));
    }

    @Test
    public void restoreProjectList_creates_given_projects_including_configuration_and_records() {
        // given
        ArrayList<Project> projectsToImport = aListOf2ProjectsWithConfigurationAndRecords();

        // when
        subject.restoreProjectList(projectsToImport);

        // then: all projects created
        verify(projectDAO, times(1)).create(projectsToImport.get(0));
        verify(projectDAO, times(1)).create(projectsToImport.get(1));

        // and: each tracking record in those projects is created
        verify(trackingRecordDAO, times(1)).create(projectsToImport.get(0).getTrackingRecords().get(0));
        verify(trackingRecordDAO, times(1)).create(projectsToImport.get(1).getTrackingRecords().get(0));

        // and: each tracking configuration in those projects is created
        verify(trackingConfigurationDAO, times(1)).create(projectsToImport.get(0).getTrackingConfiguration());
        verify(trackingConfigurationDAO, times(1)).create(projectsToImport.get(1).getTrackingConfiguration());
    }

    private ArrayList<Project> aListOf2Projects() {
        ArrayList<Project> list = new ArrayList<>();
        list.add(new Project("123"));
        list.add(new Project("456"));
        list.get(0).setTrackingRecords(new ArrayList<TrackingRecord>());
        list.get(1).setTrackingRecords(new ArrayList<TrackingRecord>());

        return list;
    }

    private ArrayList<Project> aListOf2ProjectsWithConfigurationAndRecords() {
        ArrayList<Project> list = new ArrayList<>();
        list.add(new Project("123"));
        list.add(new Project("456"));
        list.get(0).setTrackingRecords(new ArrayList<>(Collections.singletonList(new TrackingRecord("123"))));
        list.get(0).setTrackingConfiguration(new TrackingConfiguration("123"));
        list.get(1).setTrackingRecords(new ArrayList<>(Collections.singletonList(new TrackingRecord("456"))));
        list.get(1).setTrackingConfiguration(new TrackingConfiguration("456"));

        return list;
    }
}