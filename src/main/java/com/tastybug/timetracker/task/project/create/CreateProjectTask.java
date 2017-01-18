package com.tastybug.timetracker.task.project.create;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
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
    private ProjectFactory projectFactory;
    private TrackingConfigurationFactory trackingConfigurationFactory;
    private Project project;

    public CreateProjectTask(Context context) {
        this(context,
                new OttoProvider(),
                new ProjectDAO(context),
                new TrackingConfigurationDAO(context),
                new ProjectFactory(),
                new TrackingConfigurationFactory());
    }

    CreateProjectTask(Context context,
                      OttoProvider ottoProvider,
                      ProjectDAO projectDAO,
                      TrackingConfigurationDAO trackingConfigurationDAO,
                      ProjectFactory projectFactory,
                      TrackingConfigurationFactory trackingConfigurationFactory) {
        super(context, ottoProvider);
        this.projectDAO = projectDAO;
        this.trackingConfigurationDAO = trackingConfigurationDAO;
        this.projectFactory = projectFactory;
        this.trackingConfigurationFactory = trackingConfigurationFactory;
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
        project = projectFactory.aProject(arguments.getString(PROJECT_TITLE));
        TrackingConfiguration trackingConfiguration = trackingConfigurationFactory.aTrackingConfiguration(project.getUuid());

        return Arrays.asList(projectDAO.getBatchCreate(project),
                trackingConfigurationDAO.getBatchCreate(trackingConfiguration));
    }

    @Override
    protected OttoEvent preparePostEvent() {
        return new ProjectCreatedEvent(project);
    }
}