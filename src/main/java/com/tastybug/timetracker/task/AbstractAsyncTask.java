package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;


public abstract class AbstractAsyncTask extends AsyncTask<Bundle, Integer, Long> {

    protected OttoProvider ottoProvider = new OttoProvider();
    protected Bundle arguments = new Bundle();
    protected Context context;
    private BatchOperationExecutor batchOperationExecutor;

    protected AbstractAsyncTask(Context context) {
        this(context, new BatchOperationExecutor(context.getContentResolver()));
    }

    protected AbstractAsyncTask(Context context, BatchOperationExecutor batchOperationExecutor) {
        this.context = context;
        this.batchOperationExecutor = batchOperationExecutor;
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
        batchOperationExecutor.executeBatch();

        return 0L;
    }

    protected abstract void performBackgroundStuff(Bundle args);

    protected void storeBatchOperation(ContentProviderOperation operation) {
        batchOperationExecutor.addOperation(operation);
    }
}
