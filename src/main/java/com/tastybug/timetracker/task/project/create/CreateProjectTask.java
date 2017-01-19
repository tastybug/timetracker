package com.tastybug.timetracker.task.project.create;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.task.TaskPayload;

import java.util.Arrays;
import java.util.List;


public class CreateProjectTask extends TaskPayload {

    private static final String PROJECT_TITLE = "PROJECT_TITLE";

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private Project project;

    public CreateProjectTask(Context context) {
        this(context,
                new ProjectDAO(context),
                new TrackingConfigurationDAO(context));
    }

    CreateProjectTask(Context context,
                      ProjectDAO projectDAO,
                      TrackingConfigurationDAO trackingConfigurationDAO) {
        super(context);
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
    }

    public CreateProjectTask withProjectTitle(String title) {
        arguments.putString(PROJECT_TITLE, title);
        return this;
    }

    @Override
    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_TITLE));
    }

    @Override
    protected List<ContentProviderOperation> prepareBatchOperations() {
        project = new Project(arguments.getString(PROJECT_TITLE));
        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid());

        return Arrays.asList(projectDAO.getBatchCreate(project),
                trackingConfigurationDAO.getBatchCreate(trackingConfiguration));
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new ProjectCreatedEvent(project);
    }
}