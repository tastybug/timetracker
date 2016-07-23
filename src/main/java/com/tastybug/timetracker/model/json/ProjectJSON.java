package com.tastybug.timetracker.model.json;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ProjectJSON extends JSONObject {

    static String UUID = "uuid";
    static String TITLE = "title";
    static String DESCRIPTION = "description";
    static String TRACKING_CONFIGURATION = "tracking_configuration";
    static String TRACKING_RECORDS = "tracking_records";

    protected ProjectJSON(Project project) throws JSONException {
        put(UUID, project.getUuid());
        put(TITLE, project.getTitle());
        if (project.getDescription().isPresent()) {
            put(DESCRIPTION, project.getDescription().get());
        }
    }

    protected ProjectJSON(JSONObject toImport) throws JSONException {
        put(UUID, toImport.get(UUID));
        put(TITLE, toImport.get(TITLE));
        put(DESCRIPTION, toImport.opt(DESCRIPTION));
        put(TRACKING_CONFIGURATION, toImport.get(TRACKING_CONFIGURATION));
        put(TRACKING_RECORDS, toImport.get(TRACKING_RECORDS));
    }

    protected void setTrackingConfiguration(TrackingConfiguration trackingConfiguration) throws JSONException {
        put(TRACKING_CONFIGURATION, new TrackingConfigurationJSON(trackingConfiguration));
    }

    protected TrackingConfiguration getTrackingConfiguration() throws JSONException, ParseException {
        return new TrackingConfigurationJSON(getJSONObject(TRACKING_CONFIGURATION)).toTrackingConfiguration();
    }

    protected void setTrackingRecords(List<TrackingRecord> trackingRecords) throws JSONException {
        JSONArray array = new JSONArray();
        for (TrackingRecord trackingRecord : trackingRecords) {
            array.put(new TrackingRecordJSON(trackingRecord));
        }
        put(TRACKING_RECORDS, array);
    }

    protected ArrayList<TrackingRecord> getTrackingRecords() throws JSONException, ParseException {
        JSONArray array = getJSONArray(TRACKING_RECORDS);
        ArrayList<TrackingRecord> trackingRecords = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            trackingRecords.add(new TrackingRecordJSON(array.getJSONObject(0)).getTrackingRecord());
        }

        return trackingRecords;
    }

    protected Project getAsProject() throws JSONException, ParseException {
        Project project = new Project(getString(UUID), getString(TITLE), optString(DESCRIPTION));
        project.setTrackingConfiguration(getTrackingConfiguration());
        project.setTrackingRecords(getTrackingRecords());

        return project;
    }
}
