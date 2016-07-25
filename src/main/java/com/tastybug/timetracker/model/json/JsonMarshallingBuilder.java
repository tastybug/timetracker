package com.tastybug.timetracker.model.json;

import android.content.Context;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonMarshallingBuilder {

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private TrackingRecordDAO trackingRecordDAO;

    private Optional<String> projectUuidOpt = Optional.absent();

    public JsonMarshallingBuilder(Context context) {
        this.projectDAO = new ProjectDAO(context);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
        this.trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public JsonMarshallingBuilder(ProjectDAO projectDAO,
                                  TrackingConfigurationDAO trackingConfigurationDAO,
                                  TrackingRecordDAO trackingRecordDAO) {
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public JsonMarshallingBuilder withProjectUuid(String projectUuid) {
        projectUuidOpt = Optional.of(projectUuid);
        return this;
    }

    public JSONArray build() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        if (projectUuidOpt.isPresent()) {
            Project project = projectDAO.get(projectUuidOpt.get()).get();
            jsonArray.put(createProjectJson(project));
        } else {
            for (Project project : projectDAO.getAll()) {
                jsonArray.put(createProjectJson(project));
            }
        }
        return jsonArray;
    }

    private ProjectJSON createProjectJson(Project project) throws JSONException {
        project.setTrackingConfiguration(trackingConfigurationDAO.getByProjectUuid(project.getUuid()).get());
        project.setTrackingRecords(trackingRecordDAO.getByProjectUuid(project.getUuid()));

        return new ProjectJSON(project);
    }
}
