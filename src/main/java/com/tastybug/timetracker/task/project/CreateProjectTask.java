package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;


public class CreateProjectTask extends AbstractAsyncTask {

    private static final String PROJECT_TITLE = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";


    private Project project;

    private CreateProjectTask(Context context) {
        super(context);
    }

    public static CreateProjectTask aTask(Context context) {
        return new CreateProjectTask(context);
    }

    public CreateProjectTask withProjectTitle(String title) {
        arguments.putString(PROJECT_TITLE, title);
        return this;
    }

    CreateProjectTask withProjectDescription(String description) {
        arguments.putString(PROJECT_DESCRIPTION, description);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_TITLE));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        project = new Project(args.getString(PROJECT_TITLE));
        project.setDescription(Optional.fromNullable(args.getString(PROJECT_DESCRIPTION)));

        TrackingConfiguration trackingConfiguration = new TrackingConfiguration(project.getUuid());

        storeBatchOperation(new ProjectDAO(context).getBatchCreate(project));
        storeBatchOperation(new TrackingConfigurationDAO(context).getBatchCreate(trackingConfiguration));
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Created project " + project);
        ottoProvider.getSharedBus().post(new ProjectCreatedEvent(project));
    }

}


