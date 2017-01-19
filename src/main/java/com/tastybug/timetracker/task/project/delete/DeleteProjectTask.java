package com.tastybug.timetracker.task.project.delete;

import android.content.ContentProviderOperation;
import android.content.Context;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.task.TaskPayload;

import java.util.Collections;
import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;


public class DeleteProjectTask extends TaskPayload {

    private static final String PROJECT_UUID = "PROJECT_UUID";

    private ProjectDAO projectDAO;

    public DeleteProjectTask(Context context) {
        this(context, new ProjectDAO(context));
    }

    DeleteProjectTask(Context context, ProjectDAO projectDAO) {
        super(context);
        this.projectDAO = projectDAO;
    }

    public DeleteProjectTask withProjectUuid(String uuid) {
        arguments.putString(PROJECT_UUID, uuid);
        return this;
    }

    protected void validate() throws IllegalArgumentException, NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_UUID));
    }

    protected List<ContentProviderOperation> prepareBatchOperations() {
        String projectUuidToDelete = arguments.getString(PROJECT_UUID);
        boolean success = projectDAO.delete(projectUuidToDelete);
        if (!success) {
            throw new RuntimeException("Failed to delete project " + projectUuidToDelete);
        }

        return Collections.emptyList();
    }

    protected OttoEvent preparePostEvent() {
        logInfo(getClass().getSimpleName(), "Deleted project " + arguments.getString(PROJECT_UUID));
        return new ProjectDeletedEvent(arguments.getString(PROJECT_UUID));
    }
}
