package com.tastybug.timetracker.task.project;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.database.dao.EntityDAO;
import com.tastybug.timetracker.model.Project;

import java.util.ArrayList;


public class CreateProjectTask extends AsyncTask<Bundle, Integer, Long> {

    private static final String PROJECT_TITLE       = "PROJECT_TITLE";
    private static final String PROJECT_DESCRIPTION = "PROJECT_DESCRIPTION";

    Bundle arguments = new Bundle();
    Context context;
    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

    public static CreateProjectTask aTask(Context context) {
        return new CreateProjectTask(context);
    }

    private CreateProjectTask(Context context) {
        this.context = context;
    }

    public CreateProjectTask withProjectTitle(String title) {
        arguments.putString(PROJECT_TITLE, title);
        return this;
    }

    public CreateProjectTask withProjectDescription(String description) {
        arguments.putString(PROJECT_DESCRIPTION, description);
        return this;
    }

    protected void validateArguments() throws NullPointerException {
        Preconditions.checkNotNull(arguments.getString(PROJECT_TITLE));
    }

    public void execute() throws NullPointerException {
        validateArguments();
        execute(arguments);
    }

    protected Long doInBackground(Bundle... params) {
        Bundle args = params[0];

        Project project = new Project(args.getString(PROJECT_TITLE));
        project.setDescription(Optional.fromNullable(args.getString(PROJECT_DESCRIPTION)));

        storeBatchOperation(project.getDAO(context).getBatchCreate(project));
        try {
            executeBatchOperations();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        return 0l;
    }

    protected void storeBatchOperation(ContentProviderOperation operation) {
        operations.add(operation);
    }

    protected ContentProviderResult[] executeBatchOperations() throws RemoteException, OperationApplicationException {
        if (operations.isEmpty()) {
            return new ContentProviderResult[0];
        }

        return context.getContentResolver().applyBatch(EntityDAO.AUTHORITY, operations);
    }

    protected void onPostExecute(Long result) {
        // notify otto
    }
}


