package com.tastybug.timetracker.model.json;

import android.content.Context;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.dao.TrackingRecordDAO;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonExportBuilder {

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private TrackingRecordDAO trackingRecordDAO;

    private Optional<String> projectUuidOpt = Optional.absent();

    public JsonExportBuilder(Context context) {
        this.projectDAO = new ProjectDAO(context);
        this.trackingConfigurationDAO = new TrackingConfigurationDAO(context);
        this.trackingRecordDAO = new TrackingRecordDAO(context);
    }

    public JsonExportBuilder(ProjectDAO projectDAO,
                             TrackingConfigurationDAO trackingConfigurationDAO,
                             TrackingRecordDAO trackingRecordDAO) {
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
        this.trackingRecordDAO = trackingRecordDAO;
    }

    public JsonExportBuilder withProjectUuid(String projectUuid) {
        projectUuidOpt = Optional.of(projectUuid);
        return this;
    }

    public List<ProjectJSON> build() throws JSONException {
        List<ProjectJSON> jsonObjectArrayList;
        if (projectUuidOpt.isPresent()) {
            Project project = projectDAO.get(projectUuidOpt.get()).get();
            jsonObjectArrayList = Arrays.asList(createProjectJson(project));
        } else {
            jsonObjectArrayList = new ArrayList<>();
            for (Project project : projectDAO.getAll()) {
                jsonObjectArrayList.add(createProjectJson(project));
            }
        }
        return jsonObjectArrayList;
    }

    private ProjectJSON createProjectJson(Project project) throws JSONException {
        ProjectJSON projectJSON = new ProjectJSON(project);
        projectJSON.setTrackingConfiguration(trackingConfigurationDAO.getByProjectUuid(project.getUuid()).get());
        projectJSON.setTrackingRecords(trackingRecordDAO.getByProjectUuid(project.getUuid()));

        return projectJSON;
    }
}
