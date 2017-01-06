package com.tastybug.timetracker.task.project.create;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;
import com.tastybug.timetracker.task.BatchOperationExecutor;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;


public class CreateProjectTask extends AbstractAsyncTask {

    private static final String PROJECT_TITLE = "PROJECT_TITLE";

    private ProjectDAO projectDAO;
    private TrackingConfigurationDAO trackingConfigurationDAO;
    private ProjectFactory projectFactory;
    private TrackingConfigurationFactory trackingConfigurationFactory;
    private Project project;

    public CreateProjectTask(Context context) {
        this(context,
                new ProjectDAO(context),
                new TrackingConfigurationDAO(context),
                new ProjectFactory(),
                new TrackingConfigurationFactory(),
                new BatchOperationExecutor(context.getContentResolver()));
    }

    CreateProjectTask(Context context,
                      ProjectDAO projectDAO,
                      TrackingConfigurationDAO trackingConfigurationDAO,
                      ProjectFactory projectFactory,
                      TrackingConfigurationFactory trackingConfigurationFactory,
                      BatchOperationExecutor batchOperationExecutor) {
        super(context, batchOperationExecutor);
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
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_TITLE));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        project = projectFactory.aProject(args.getString(PROJECT_TITLE));
        TrackingConfiguration trackingConfiguration = trackingConfigurationFactory.aTrackingConfiguration(project.getUuid());

        storeBatchOperation(projectDAO.getBatchCreate(project));
        storeBatchOperation(trackingConfigurationDAO.getBatchCreate(trackingConfiguration));
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Created project " + project);
        ottoProvider.getSharedBus().post(new ProjectCreatedEvent(project));
    }
}