package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.ProjectTimeConstraints;


public class CreateProjectTask extends AbstractAsyncTask {

    private static final String TAG = CreateProjectTask.class.getSimpleName();

    private static final String PROJECT_TITLE       = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";


    private Project project;

    public static CreateProjectTask aTask(Context context) {
        return new CreateProjectTask(context);
    }

    private CreateProjectTask(Context context) {
        super(context);
    }

    public CreateProjectTask withProjectTitle(String title) {
        arguments.putString(PROJECT_TITLE, title);
        return this;
    }

    public CreateProjectTask withProjectDescription(String description) {
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

        ProjectTimeConstraints timeConstraints = new ProjectTimeConstraints(project.getUuid());

        storeBatchOperation(project.getDAO(context).getBatchCreate(project));
        storeBatchOperation(timeConstraints.getDAO(context).getBatchCreate(timeConstraints));
    }

    protected void onPostExecute(Long result) {
        // notify otto
        // and update the test
        Log.i(TAG, "Created project " + project);
        ottoProvider.getSharedBus().post(new ProjectCreatedEvent(project));
    }

}


