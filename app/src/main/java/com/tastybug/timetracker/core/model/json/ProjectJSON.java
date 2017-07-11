package com.tastybug.timetracker.core.model.json;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class ProjectJSON extends JSONObject {

    static String UUID = "uuid";
    static String TITLE = "title";
    static String DESCRIPTION = "description";
    static String CONTRACT_ID = "contract_id";
    static String CLOSED = "closed";
    static String TRACKING_CONFIGURATION = "tracking_configuration";
    static String TRACKING_RECORDS = "tracking_records";

    /**
     * The caller has to provide a full project, including TrackingConfiguration and the list
     * of TrackingRecords (can be empty of course).
     *
     * @throws JSONException        on json marshalling problems
     * @throws NullPointerException if the caller didn't properly assemble the project by leaving
     *                              out the TrackingConfiguration and/or TrackingRecord list
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    ProjectJSON(Project project) throws JSONException {
        Preconditions.checkNotNull(project.getTrackingConfiguration());
        Preconditions.checkNotNull(project.getTrackingRecords());

        put(UUID, project.getUuid());
        put(TITLE, project.getTitle());
        if (project.getDescription().isPresent()) {
            put(DESCRIPTION, project.getDescription().get());
        }
        if (project.getContractId().isPresent()) {
            put(CONTRACT_ID, project.getContractId().get());
        }
        put(CLOSED, project.isClosed());
        setTrackingConfiguration(project.getTrackingConfiguration());
        setTrackingRecords(project.getTrackingRecords());
    }

    ProjectJSON(JSONObject toImport) throws JSONException {
        put(UUID, toImport.get(UUID));
        put(TITLE, toImport.get(TITLE));
        put(DESCRIPTION, toImport.opt(DESCRIPTION));
        put(CONTRACT_ID, toImport.opt(CONTRACT_ID));
        put(CLOSED, toImport.get(CLOSED));
        put(TRACKING_CONFIGURATION, toImport.get(TRACKING_CONFIGURATION));
        put(TRACKING_RECORDS, toImport.get(TRACKING_RECORDS));
    }

    protected TrackingConfiguration getTrackingConfiguration() throws JSONException, ParseException {
        return new TrackingConfigurationJSON(getJSONObject(TRACKING_CONFIGURATION)).toTrackingConfiguration();
    }

    protected void setTrackingConfiguration(TrackingConfiguration trackingConfiguration) throws JSONException {
        put(TRACKING_CONFIGURATION, new TrackingConfigurationJSON(trackingConfiguration));
    }

    protected ArrayList<TrackingRecord> getTrackingRecords() throws JSONException, ParseException {
        JSONArray array = getJSONArray(TRACKING_RECORDS);
        ArrayList<TrackingRecord> trackingRecords = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            trackingRecords.add(new TrackingRecordJSON(array.getJSONObject(i)).toTrackingRecord());
        }

        return trackingRecords;
    }

    protected void setTrackingRecords(List<TrackingRecord> trackingRecords) throws JSONException {
        JSONArray array = new JSONArray();
        for (TrackingRecord trackingRecord : trackingRecords) {
            array.put(new TrackingRecordJSON(trackingRecord));
        }
        put(TRACKING_RECORDS, array);
    }

    Project toProject() throws JSONException, ParseException {
        Project project = new Project(
                getString(UUID),
                getString(TITLE),
                isNull(DESCRIPTION) ? Optional.<String>absent() : Optional.of(getString(DESCRIPTION)),
                isNull(CONTRACT_ID) ? Optional.<String>absent() : Optional.of(getString(CONTRACT_ID)),
                getBoolean(CLOSED));
        project.setTrackingConfiguration(getTrackingConfiguration());
        project.setTrackingRecords(getTrackingRecords());

        return project;
    }
}
