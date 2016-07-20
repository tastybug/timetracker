package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ProjectMarshallingTest {

    ProjectDAO projectDAOMock = mock(ProjectDAO.class);
    TrackingConfigurationMarshalling trackingConfigurationMarshalling = mock(TrackingConfigurationMarshalling.class);
    TrackingRecordMarshalling trackingRecordMarshalling = mock(TrackingRecordMarshalling.class);

    ProjectMarshalling subject = new ProjectMarshalling(projectDAOMock,
            trackingConfigurationMarshalling,
            trackingRecordMarshalling);

    @Before
    public void setup() throws Exception {
        when(trackingConfigurationMarshalling.getAsJsonByProjectUuid(anyString())).thenReturn(new JSONObject());
        when(trackingRecordMarshalling.getAsJsonByProjectUuid(anyString())).thenReturn(Arrays.asList(new JSONObject()));
    }

    @Test
    public void getAsJson_can_marshal_a_project_uuid() throws Exception {
        // given
        Project project = new Project("title");

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertEquals(json.getString(ProjectMarshalling.UUID), project.getUuid());
    }

    @Test
    public void getAsJson_can_marshal_a_project_title() throws Exception {
        // given
        Project project = new Project("title");

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertEquals(json.getString(ProjectMarshalling.TITLE), "title");
    }

    @Test
    public void getAsJson_can_marshal_a_project_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.of("blub"));

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertEquals(json.getString(ProjectMarshalling.DESCRIPTION), "blub");
    }

    @Test
    public void getAsJson_can_marshal_a_project_without_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.<String>absent());

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertTrue(json.isNull(ProjectMarshalling.DESCRIPTION));
    }

    @Test
    public void getAsJson_stores_tracking_configuration_created_by_marshaller_in_project_json() throws Exception {
        // given
        Project project = new Project("one");
        JSONObject json = new JSONObject();
        json.put("name", "value");
        when(trackingConfigurationMarshalling.getAsJsonByProjectUuid(project.getUuid())).thenReturn(json);

        // when
        JSONObject projectJson = subject.getAsJson(project);

        // then
        assertEquals("value", projectJson.getJSONObject(ProjectMarshalling.TRACKING_CONFIGURATION).get("name"));
    }

    @Test
    public void getAsJson_calls_tracking_configuration_marshaller_for_tracking_configuration_jsons() throws Exception {
        // given
        Project project = new Project("one");
        when(trackingConfigurationMarshalling.getAsJsonByProjectUuid(project.getUuid())).thenReturn(new JSONObject());

        // when
        subject.getAsJson(project);

        // then
        verify(trackingConfigurationMarshalling, times(1)).getAsJsonByProjectUuid(project.getUuid());
    }

    @Test
    public void getAsJson_calls_tracking_record_marshaller_for_tracking_record_jsons() throws Exception {
        // given
        Project project = new Project("one");
        when(trackingRecordMarshalling.getAsJsonByProjectUuid(project.getUuid())).thenReturn(Arrays.asList(new JSONObject()));

        // when
        subject.getAsJson(project);

        // then
        verify(trackingRecordMarshalling, times(1)).getAsJsonByProjectUuid(project.getUuid());
    }

    @Test
    public void getAsJson_stores_tracking_records_created_by_marshaller_in_project_json() throws Exception {
        // given
        Project project = new Project("one");
        JSONObject json = new JSONObject();
        json.put("name", "value");
        when(trackingRecordMarshalling.getAsJsonByProjectUuid(project.getUuid())).thenReturn(Arrays.asList(json));

        // when
        JSONObject projectJson = subject.getAsJson(project);

        // then
        assertEquals("value", projectJson.getJSONArray(ProjectMarshalling.TRACKING_RECORDS).getJSONObject(0).get("name"));

        // and
        assertEquals(1, projectJson.getJSONArray(ProjectMarshalling.TRACKING_RECORDS).length());
    }

    @Test
    public void dumpAllProjectsAsJSONs_is_successful_but_returns_empty_json_list_when_no_project_is_in_DB() throws Exception {
        // given
        when(projectDAOMock.getAll()).thenReturn(new ArrayList<Project>());

        // when
        List<JSONObject> allProjectsAsJSONs = subject.dumpAllProjectsAsJSONs();

        // then
        assertTrue(allProjectsAsJSONs.isEmpty());

        // and
        verify(projectDAOMock, times(1)).getAll();
    }

    @Test
    public void dumpAllProjectsAsJSONs_returns_correct_list_of_jsons_with_multiple_existing_projects_in_DB() throws Exception {
        // given
        ArrayList<Project> projects = aListOfTwoProjects();
        when(projectDAOMock.getAll()).thenReturn(projects);

        // when
        List<JSONObject> allProjectsAsJSONs = subject.dumpAllProjectsAsJSONs();

        // then
        assertEquals(2, allProjectsAsJSONs.size());
    }

    ArrayList<Project> aListOfTwoProjects() {
        ArrayList<Project> list = new ArrayList<>();
        list.add(new Project("one"));
        list.add(new Project("two"));

        return list;
    }
}