package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;


public abstract class AbstractAsyncTask extends AsyncTask<Bundle, Integer, Long> {

    protected OttoProvider ottoProvider = new OttoProvider();
    protected Bundle arguments = new Bundle();
    protected Context context;
    private BatchOperationExecutor batchOperationExecutor;

    protected AbstractAsyncTask(Context context) {
        this.context = context;
        this.batchOperationExecutor = new BatchOperationExecutor(context.getContentResolver());
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
        List<ContentProviderOperation> batchOperations = performBackgroundStuff(params[0]);
        logDebug(getClass().getSimpleName(), "Persisting to database..");
        batchOperationExecutor.executeBatch(batchOperations);

        return 0L;
    }

    protected abstract List<ContentProviderOperation> performBackgroundStuff(Bundle args);
}
