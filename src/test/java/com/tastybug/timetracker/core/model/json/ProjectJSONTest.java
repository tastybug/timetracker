package com.tastybug.timetracker.core.model.json;

import android.os.Build;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.rounding.Rounding;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static com.tastybug.timetracker.core.model.json.ProjectJSON.CLOSED;
import static com.tastybug.timetracker.core.model.json.ProjectJSON.CONTRACT_ID;
import static com.tastybug.timetracker.core.model.json.ProjectJSON.DESCRIPTION;
import static com.tastybug.timetracker.core.model.json.ProjectJSON.TITLE;
import static com.tastybug.timetracker.core.model.json.ProjectJSON.TRACKING_CONFIGURATION;
import static com.tastybug.timetracker.core.model.json.ProjectJSON.TRACKING_RECORDS;
import static com.tastybug.timetracker.core.model.json.ProjectJSON.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN, manifest = Config.NONE)
public class ProjectJSONTest {

    @Test
    public void can_marshal_a_project_uuid() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals(project.getUuid(), json.getString(UUID));
    }

    @Test
    public void can_marshal_a_project_title() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals(project.getTitle(), json.getString(TITLE));
    }

    @Test
    public void can_marshal_a_project_closed_state() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setClosed(true);

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals(project.isClosed(), json.getBoolean(CLOSED));
    }

    @Test
    public void can_marshal_a_project_description() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setDescription(Optional.of("blub"));

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals(project.getDescription().get(), json.getString(DESCRIPTION));
    }

    @Test
    public void can_marshal_a_project_without_description() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setDescription(Optional.<String>absent());

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertTrue(json.isNull(DESCRIPTION));
    }

    @Test
    public void can_marshal_a_project_contract_id() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setContractId(Optional.of("contract"));

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertEquals(project.getContractId().get(), json.getString(CONTRACT_ID));
    }

    @Test
    public void can_marshal_a_project_without_contract_id() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setContractId(Optional.<String>absent());

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertTrue(json.isNull(CONTRACT_ID));
    }

    @Test
    public void can_marshal_a_project_without_any_records() throws Exception {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setTrackingRecords(new ArrayList<TrackingRecord>());

        // when
        ProjectJSON json = new ProjectJSON(project);

        // then
        assertFalse(json.isNull(TRACKING_RECORDS));
        assertEquals(0, json.getJSONArray(TRACKING_RECORDS).length());
    }

    @Test(expected = NullPointerException.class)
    public void marshalling_a_project_without_configuration_yields_NPE() throws JSONException {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setTrackingConfiguration(null);

        // when
        new ProjectJSON(project);
    }

    @Test(expected = NullPointerException.class)
    public void marshalling_a_project_without_tracking_record_list_yields_NPE() throws JSONException {
        // given
        Project project = aProjectWith2RecordsAndAConfiguration();
        project.setTrackingRecords(null);

        // when
        new ProjectJSON(project);
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
        assertEquals(toImportFrom.getString(CONTRACT_ID), subject.getString(CONTRACT_ID));
        assertEquals(toImportFrom.getBoolean(CLOSED), subject.getBoolean(CLOSED));
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

    @Test
    public void can_import_a_json_without_contract_id() throws Exception {
        // given
        JSONObject toImportFrom = aProjectJSON();
        toImportFrom.put(CONTRACT_ID, null);

        // when
        ProjectJSON subject = new ProjectJSON(toImportFrom);

        // then
        assertTrue(subject.isNull(CONTRACT_ID));
    }

    @Test
    public void to_project_will_build_project_including_records_and_configuration() throws Exception {
        // given
        Project projectIn = aProjectWith2RecordsAndAConfiguration();

        // when
        Project projectOut = new ProjectJSON(projectIn).toProject();

        // when
        assertEquals(projectIn.getUuid(), projectOut.getUuid());
        assertEquals(projectIn.getTrackingConfiguration().getUuid(),
                projectOut.getTrackingConfiguration().getUuid());
        assertEquals(2, projectOut.getTrackingRecords().size());
    }

    private JSONObject aProjectJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(UUID, "uuid");
        jsonObject.put(TITLE, "title");
        jsonObject.put(DESCRIPTION, "desc");
        jsonObject.put(CONTRACT_ID, "contract");
        jsonObject.put(CLOSED, false);
        jsonObject.put(TRACKING_CONFIGURATION, new JSONObject());
        jsonObject.put(TRACKING_RECORDS, new JSONObject());

        return jsonObject;
    }

    private Project aProjectWith2RecordsAndAConfiguration() {
        Project project = new Project("uuid", "title", Optional.<String>absent(), Optional.<String>absent(), false);
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration("uuid", Rounding.Strategy.NO_ROUNDING);
        ArrayList<TrackingRecord> trackingRecordArrayList = new ArrayList<>();
        trackingRecordArrayList.add(new TrackingRecord("uuid"));
        trackingRecordArrayList.add(new TrackingRecord("uuid"));
        project.setTrackingConfiguration(trackingConfiguration);
        project.setTrackingRecords(trackingRecordArrayList);

        return project;
    }
}