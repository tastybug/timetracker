package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class JsonExportBuilderTest {

    ProjectDAO projectDAO = mock(ProjectDAO.class);
    TrackingConfigurationDAO trackingConfigurationDAO = mock(TrackingConfigurationDAO.class);
    TrackingRecordDAO trackingRecordDAO = mock(TrackingRecordDAO.class);

    JsonExportBuilder builder = new JsonExportBuilder(projectDAO, trackingConfigurationDAO, trackingRecordDAO);

    @Test
    public void build_exports_one_project_when_providing_a_project_uuid() throws JSONException {
        // given
        Project project = new Project("project-uuid");
        when(projectDAO.get(project.getUuid())).thenReturn(Optional.of(project));
        when(trackingConfigurationDAO.getByProjectUuid(project.getUuid())).thenReturn(Optional.of(new TrackingConfiguration("")));
        when(trackingRecordDAO.getByProjectUuid(project.getUuid())).thenReturn(new ArrayList<TrackingRecord>());

        // when
        JSONArray projectArray = builder.withProjectUuid(project.getUuid()).build();

        // then
        assertEquals(1, projectArray.length());

        // and
        verify(projectDAO, times(1)).get(project.getUuid());
        verify(trackingConfigurationDAO, times(1)).getByProjectUuid(project.getUuid());
        verify(trackingRecordDAO, times(1)).getByProjectUuid(project.getUuid());
    }

    @Test
    public void build_exports_all_projects_when_setting_no_specific_project_uuid() throws JSONException {
        // given
        when(projectDAO.getAll()).thenReturn(aListOfTwoProjects());
        when(trackingConfigurationDAO.getByProjectUuid(anyString())).thenReturn(Optional.of(new TrackingConfiguration("")));
        when(trackingRecordDAO.getByProjectUuid(anyString())).thenReturn(new ArrayList<TrackingRecord>());

        // when
        JSONArray projectArray = builder.build();

        // then
        assertEquals(2, projectArray.length());

        // and
        verify(projectDAO, times(1)).getAll();
        verify(trackingConfigurationDAO, times(2)).getByProjectUuid(isA(String.class));
        verify(trackingRecordDAO, times(2)).getByProjectUuid(isA(String.class));
    }

    @Test
    public void build_returns_empty_array_for_global_export_when_no_project_is_available() throws JSONException {
        // given
        when(projectDAO.getAll()).thenReturn(new ArrayList<Project>());

        // when
        JSONArray projectArray = builder.build();

        // then
        assertEquals(0, projectArray.length());
    }

    @Test(expected = IllegalStateException.class)
    public void build_yields_IllegalStateException_for_unknown_project_uuid() throws JSONException {
        // given
        Project project = new Project("project-uuid");
        when(projectDAO.get(project.getUuid())).thenReturn(Optional.<Project>absent());

        // expect
        builder.withProjectUuid(project.getUuid()).build();
    }

    @Test(expected = NullPointerException.class)
    public void withProjectUuid_yields_NPE_for_null_project_uuid() {
        // expect
        builder.withProjectUuid(null);
    }

    private ArrayList<Project> aListOfTwoProjects() {
        ArrayList<Project> list = new ArrayList<>();
        list.add(new Project(""));
        list.add(new Project(""));
        return list;
    }
}