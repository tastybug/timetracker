package com.tastybug.timetracker.model.json;

import android.content.Context;

import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectMarshalling implements JsonMarshaller {

    static String UUID = "uuid";
    static String TITLE = "title";
    static String DESCRIPTION = "description";
    static String TRACKING_CONFIGURATION = "tracking_configuration";

    private ProjectDAO projectDAO;
    private TrackingConfigurationMarshalling trackingConfigurationMarshalling;

    public ProjectMarshalling(Context context) {
        projectDAO = new ProjectDAO(context);
        trackingConfigurationMarshalling = new TrackingConfigurationMarshalling(context);
    }

    public ProjectMarshalling(ProjectDAO projectDAO, TrackingConfigurationMarshalling trackingConfigurationMarshalling) {
        this.projectDAO = projectDAO;
        this.trackingConfigurationMarshalling = trackingConfigurationMarshalling;
    }

    protected JSONObject getAsJson(Project project) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(UUID, project.getUuid());
        json.put(TITLE, project.getTitle());
        if (project.getDescription().isPresent()) {
            json.put(DESCRIPTION, project.getDescription().get());
        }
        json.put(TRACKING_CONFIGURATION,
                trackingConfigurationMarshalling.generateJSON(project.getUuid()).get(0));
        return json;
    }

    @Override
    public List<JSONObject> generateJSON() throws JSONException {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        for (Project project : projectDAO.getAll()) {
            jsonObjectArrayList.add(getAsJson(project));
        }
        return jsonObjectArrayList;
    }

    @Override
    public List<JSONObject> generateJSON(String projectUuid) throws JSONException {
        ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<>();
        jsonObjectArrayList.add(getAsJson(projectDAO.get(projectUuid).get()));
        return jsonObjectArrayList;
    }
}
