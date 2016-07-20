package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ProjectJSONTest {

    @Test
    public void can_marshal_a_project_uuid() throws Exception {
        // given
        Project project = new Project("title");

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals(project.getUuid(), json.getString(ProjectJSON.UUID));
    }

    @Test
    public void can_marshal_a_project_title() throws Exception {
        // given
        Project project = new Project("title");

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals("title", json.getString(ProjectJSON.TITLE));
    }

    @Test
    public void can_marshal_a_project_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.of("blub"));

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals("blub", json.getString(ProjectJSON.DESCRIPTION));
    }

    @Test
    public void can_marshal_a_project_without_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.<String>absent());

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertTrue(json.isNull(ProjectJSON.DESCRIPTION));
    }

}