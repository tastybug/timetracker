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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ProjectMarshallingTest {

    private ProjectDAO projectDAOMock = mock(ProjectDAO.class);
    private TrackingConfigurationMarshalling trackingConfigurationMarshalling = mock(TrackingConfigurationMarshalling.class);

    private ProjectMarshalling subject = new ProjectMarshalling(projectDAOMock, trackingConfigurationMarshalling);

    @Before
    public void setup() throws Exception {
        when(trackingConfigurationMarshalling.generateJSON(anyString())).thenReturn(Arrays.asList(new JSONObject()));
    }

    @Test
    public void can_marshal_a_project_uuid() throws Exception {
        // given
        Project project = new Project("title");

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertEquals(json.getString(ProjectMarshalling.UUID), project.getUuid());
    }

    @Test
    public void can_marshal_a_project_title() throws Exception {
        // given
        Project project = new Project("title");

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertEquals(json.getString(ProjectMarshalling.TITLE), "title");
    }

    @Test
    public void can_marshal_a_project_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.of("blub"));

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertEquals(json.getString(ProjectMarshalling.DESCRIPTION), "blub");
    }

    @Test
    public void can_marshal_a_project_without_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.<String>absent());

        // when
        JSONObject json = subject.getAsJson(project);

        // then
        assertTrue(json.isNull(ProjectMarshalling.DESCRIPTION));
    }

    @Test
    public void can_marshall_when_no_project_is_available() throws Exception {
        // given
        when(projectDAOMock.getAll()).thenReturn(new ArrayList<Project>());

        // when
        List<JSONObject> jsons = subject.generateJSON();

        // then
        assertTrue(jsons.isEmpty());

        // and
        verify(projectDAOMock, times(1)).getAll();
    }

    @Test
    public void can_marshall_multiple_projects() throws Exception {
        // given
        ArrayList<Project> projects = aListOfTwoProjects();
        when(projectDAOMock.getAll()).thenReturn(projects);

        // when
        List<JSONObject> jsons = subject.generateJSON();

        // then
        assertEquals(2, jsons.size());
    }

    public void can_marshall_a_specific_project_by_uuid() throws Exception {
        // given
        when(projectDAOMock.get(anyString())).thenReturn(Optional.of(new Project("lala")));

        // when
        List<JSONObject> jsons = subject.generateJSON();

        // then
        assertEquals(1, jsons.size());

        // and
        assertEquals("lala", jsons.get(0).getString(ProjectMarshalling.TITLE));
    }

    public void can_marshall_a_tracking_configuration() throws Exception {
        // given
        when(trackingConfigurationMarshalling.generateJSON(anyString())).thenReturn(Arrays.asList(new JSONObject()));

        // when
        List<JSONObject> jsons = subject.generateJSON();

        // then
        assertNotNull(jsons.get(0).getJSONObject(ProjectMarshalling.TRACKING_CONFIGURATION));
    }

    ArrayList<Project> aListOfTwoProjects() {
        ArrayList<Project> list = new ArrayList<>();
        list.add(new Project("one"));
        list.add(new Project("two"));

        return list;
    }
}