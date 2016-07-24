package com.tastybug.timetracker.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.tastybug.timetracker.model.json.ProjectJSON.DESCRIPTION;
import static com.tastybug.timetracker.model.json.ProjectJSON.TITLE;
import static com.tastybug.timetracker.model.json.ProjectJSON.TRACKING_CONFIGURATION;
import static com.tastybug.timetracker.model.json.ProjectJSON.TRACKING_RECORDS;
import static com.tastybug.timetracker.model.json.ProjectJSON.UUID;
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
        assertEquals(project.getUuid(), json.getString(UUID));
    }

    @Test
    public void can_marshal_a_project_title() throws Exception {
        // given
        Project project = new Project("title");

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals("title", json.getString(TITLE));
    }

    @Test
    public void can_marshal_a_project_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.of("blub"));

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals("blub", json.getString(DESCRIPTION));
    }

    @Test
    public void can_marshal_a_project_without_description() throws Exception {
        // given
        Project project = new Project("title");
        project.setDescription(Optional.<String>absent());

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertTrue(json.isNull(DESCRIPTION));
    }

    @Test
    public void can_import_a_json() throws Exception {
        // given
        JSONObject toImportFrom = aProjectJSON();

        // when
        ProjectJSON subject = new ProjectJSON(toImportFrom);

        // then
        assertEquals(toImportFrom.get(UUID), subject.get(UUID));
        assertEquals(toImportFrom.getString(TITLE), subject.get(TITLE));
        assertEquals(toImportFrom.getString(DESCRIPTION), subject.getString(DESCRIPTION));
        assertEquals(toImportFrom.getJSONObject(TRACKING_CONFIGURATION), subject.getJSONObject(TRACKING_CONFIGURATION));
        assertEquals(toImportFrom.getJSONObject(TRACKING_RECORDS), subject.getJSONObject(TRACKING_RECORDS));
    }

    @Test
    public void can_import_a_json_without_description() throws Exception {
        // given
        JSONObject toImportFrom = aProjectJSON();
        toImportFrom.put(DESCRIPTION, null);

        // when
        ProjectJSON subject = new ProjectJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(DESCRIPTION));
    }

    private JSONObject aProjectJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(UUID, "uuid");
        jsonObject.put(TITLE, "title");
        jsonObject.put(DESCRIPTION, "desc");
        jsonObject.put(TRACKING_CONFIGURATION, new JSONObject());
        jsonObject.put(TRACKING_RECORDS, new JSONObject());

        return jsonObject;
    }
}