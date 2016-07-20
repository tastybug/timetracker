package com.tastybug.timetracker.model.json;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    protected void setTrackingConfiguration(TrackingConfiguration trackingConfiguration) throws JSONException {
        put(TRACKING_CONFIGURATION, new TrackingConfigurationJSON(trackingConfiguration));
    }

    protected void setTrackingRecords(List<TrackingRecord> trackingRecords) throws JSONException {
        JSONArray array = new JSONArray();
        for (TrackingRecord trackingRecord : trackingRecords) {
            array.put(new TrackingRecordJSON(trackingRecord));
        }
        put(TRACKING_RECORDS, array);
    }
}
