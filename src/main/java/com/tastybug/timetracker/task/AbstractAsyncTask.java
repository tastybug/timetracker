package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;

import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.dao.EntityDAO;

import java.util.ArrayList;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;
import static com.tastybug.timetracker.util.ConditionalLog.logError;


public abstract class AbstractAsyncTask extends AsyncTask<Bundle, Integer, Long> {

    protected OttoProvider ottoProvider = new OttoProvider();
    protected Bundle arguments = new Bundle();
    protected Context context;
    private ArrayList<ContentProviderOperation> operations = new ArrayList<>();

    protected AbstractAsyncTask(Context context) {
        this.context = context;
    }

    protected abstract void validateArguments() throws NullPointerException;

    public void setOttoProvider(OttoProvider ottoProvider) {
        this.ottoProvider = ottoProvider;
    }

    public void execute() throws NullPointerException {
        validateArguments();
        execute(arguments);
    }

    protected Long doInBackground(Bundle... params) {
        logDebug(getClass().getSimpleName(), "Performing background stuff..");
        performBackgroundStuff(params[0]);
        logDebug(getClass().getSimpleName(), "Persisting to database..");
        executeBatchOperations();

        return 0L;
    }

    protected abstract void performBackgroundStuff(Bundle args);

    protected void storeBatchOperation(ContentProviderOperation operation) {
        operations.add(operation);
    }

    private ContentProviderResult[] executeBatchOperations() {
        try {
            if (operations.isEmpty()) {
                return new ContentProviderResult[0];
            }

            return context.getContentResolver().applyBatch(EntityDAO.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            logError(getClass().getSimpleName(), "Problem executing sql batch operation.", e);
            throw new RuntimeException("Problem executing sql batch operation.", e);
        }
    }
}
