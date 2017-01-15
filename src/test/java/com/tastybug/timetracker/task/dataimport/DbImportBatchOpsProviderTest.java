package com.tastybug.timetracker.task.dataimport;

import android.content.ContentProviderOperation;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DbImportBatchOpsProviderTest {

    private ProjectDAO projectDAO = mock(ProjectDAO.class);
    private TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    private TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);

    private DbImportBatchOpsProvider dbImportBatchOpsProvider = new DbImportBatchOpsProvider(projectDAO, trackingConfigurationDAO, trackingRecordDAO);

    @Test
    public void getOperations_returns_operations_for_projects_and_contained_entities() {
        // given
        List<Project> importableProjectList = aListOfTwoProjects();

        // when
        List<ContentProviderOperation> operationsList = dbImportBatchOpsProvider.getOperations(importableProjectList);

        // then
        assertEquals(6, operationsList.size());

        // and
        verify(projectDAO, times(2)).getBatchCreate(any(Project.class));

        // and
        verify(trackingConfigurationDAO, times(2)).getBatchCreate(any(TrackingConfiguration.class));

        // and
        verify(trackingRecordDAO, times(2)).getBatchCreate(any(TrackingRecord.class));
    }

    private List<Project> aListOfTwoProjects() {
        Project project1 = new Project("123", "proj1", Optional.<String>absent());
        Project project2 = new Project("456", "proj2", Optional.<String>absent());
        project1.setTrackingConfiguration(new TrackingConfiguration("123"));
        project2.setTrackingConfiguration(new TrackingConfiguration("456"));

        ArrayList<Project> list = new ArrayList<>();
        list.add(project1);
        list.add(project2);

        ArrayList<TrackingRecord> trackingRecords = new ArrayList<>();
        trackingRecords.add(new TrackingRecord("123"));
        project1.setTrackingRecords(trackingRecords);

        trackingRecords = new ArrayList<>();
        trackingRecords.add(new TrackingRecord("456"));
        project2.setTrackingRecords(trackingRecords);

        return list;
    }
}