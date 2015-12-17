package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.Project;


public class CreateProjectTask extends AbstractAsyncTask {

    private static final String PROJECT_TITLE       = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";

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
        Project project = new Project(args.getString(PROJECT_TITLE));
        project.setDescription(Optional.fromNullable(args.getString(PROJECT_DESCRIPTION)));

        storeBatchOperation(project.getDAO(context).getBatchCreate(project));
    }
}


