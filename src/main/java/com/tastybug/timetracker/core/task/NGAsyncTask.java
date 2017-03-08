package com.tastybug.timetracker.core.task;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import java.util.List;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logDebug;
import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;


class NGAsyncTask extends AsyncTask<Bundle, Integer, Long> {

    private TaskPayload taskPayload;
    private OttoProvider ottoProvider;
    private BatchOperationExecutor batchOperationExecutor;

    NGAsyncTask(Context context, TaskPayload payload) {
        this(new OttoProvider(), new BatchOperationExecutor(context.getContentResolver()), payload);
    }

    NGAsyncTask(OttoProvider ottoProvider, BatchOperationExecutor batchOperationExecutor, TaskPayload payload) {
        this.batchOperationExecutor = batchOperationExecutor;
        this.taskPayload = payload;
        this.ottoProvider = ottoProvider;
    }

    void run() throws IllegalArgumentException, NullPointerException {
        taskPayload.validate();
        execute();
    }

    protected Long doInBackground(Bundle... params) {
        logDebug(getClass().getSimpleName(), "Performing background stuff..");
        List<ContentProviderOperation> batchOperations = taskPayload.prepareBatchOperations();
        logDebug(getClass().getSimpleName(), "Persisting to database..");
        batchOperationExecutor.executeBatch(batchOperations);

        return 0L;
    }

    protected void onPostExecute(Long result) {
        OttoEvent event = taskPayload.preparePostEvent();
        logInfo(getClass().getSimpleName(), "Finished! Firing " + event);
        ottoProvider.getSharedBus().post(event);
    }
}
