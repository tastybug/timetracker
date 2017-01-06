package com.tastybug.timetracker.task.project;

import android.content.Context;
import android.os.Bundle;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.task.AbstractAsyncTask;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;


public class DeleteProjectTask extends AbstractAsyncTask {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private ProjectDAO projectDAO;

    public DeleteProjectTask(Context context) {
        this(context, new ProjectDAO(context));
    }

    DeleteProjectTask(Context context, ProjectDAO projectDAO) {
        super(context);
        this.projectDAO = projectDAO;
    }

    public static DeleteProjectTask aTask(Context context) {
        return new DeleteProjectTask(context);
    }

    public DeleteProjectTask withProjectUuid(String uuid) {
        arguments.putString(PROJECT_UUID, uuid);
        return this;
    }

    @Override
    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_UUID));
    }

    @Override
    protected void performBackgroundStuff(Bundle args) {
        String projectUuidToDelete = args.getString(PROJECT_UUID);
        boolean success = projectDAO.delete(projectUuidToDelete);
        if (!success) {
            throw new RuntimeException("Failed to delete project " + projectUuidToDelete);
        }
    }

    protected void onPostExecute(Long result) {
        logInfo(getClass().getSimpleName(), "Deleted project " + arguments.getString(PROJECT_UUID));
        ottoProvider.getSharedBus().post(new ProjectDeletedEvent(arguments.getString(PROJECT_UUID)));
    }

}
