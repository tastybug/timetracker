package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;


class NGAsyncTask extends AsyncTask<Bundle, Integer, Long> {

    private TaskPayload taskPayload;
    private BatchOperationExecutor batchOperationExecutor;

    NGAsyncTask(Context context, TaskPayload payload) {
        this(new BatchOperationExecutor(context.getContentResolver()), payload);
    }

    NGAsyncTask(BatchOperationExecutor batchOperationExecutor, TaskPayload payload) {
        this.batchOperationExecutor = batchOperationExecutor;
        this.taskPayload = payload;
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
        taskPayload.firePostEvent();
    }
}
