package com.tastybug.timetracker.model.json;

import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectMarshalling {

    static String UUID = "uuid";
    static String TITLE = "title";
    static String DESCRIPTION = "description";
    static String TRACKING_CONFIGURATION = "tracking_configuration";
    static String TRACKING_RECORDS = "tracking_records";

    private ProjectDAO projectDAO;
    private TrackingConfigurationMarshalling trackingConfigurationMarshalling;
    private TrackingRecordMarshalling trackingRecordMarshalling;

    public ProjectMarshalling(Context context) {
        projectDAO = new ProjectDAO(context);
        trackingConfigurationMarshalling = new TrackingConfigurationMarshalling(context);
    }

    public ProjectMarshalling(ProjectDAO projectDAO,
                              TrackingConfigurationMarshalling trackingConfigurationMarshalling,
                              TrackingRecordMarshalling trackingRecordMarshalling) {
        this.projectDAO = projectDAO;
        this.trackingConfigurationMarshalling = trackingConfigurationMarshalling;
        this.trackingRecordMarshalling = trackingRecordMarshalling;
    }

    protected JSONObject getAsJson(Project project) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(UUID, project.getUuid());
        json.put(TITLE, project.getTitle());
        if (project.getDescription().isPresent()) {
            json.put(DESCRIPTION, project.getDescription().get());
        }
        json.put(TRACKING_CONFIGURATION,
                trackingConfigurationMarshalling.getAsJsonByProjectUuid(project.getUuid()));

        JSONArray array = new JSONArray(trackingRecordMarshalling.getAsJsonByProjectUuid(project.getUuid()));
        json.put(TRACKING_RECORDS, array);
        return json;
    }

    public List<JSONObject> dumpAllProjectsAsJSONs() throws JSONException {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        for (Project project : projectDAO.getAll()) {
            jsonObjectArrayList.add(getAsJson(project));
        }
        return jsonObjectArrayList;
    }
}
