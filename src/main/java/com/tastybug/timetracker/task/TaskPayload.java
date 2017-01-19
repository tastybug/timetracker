package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Bundle;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

import java.util.List;

public abstract class TaskPayload {

    protected Context context;
    protected Bundle arguments = new Bundle();

    public TaskPayload(Context context) {
        this.context = context;
    }

    protected abstract void validate() throws IllegalArgumentException, NullPointerException;

    protected abstract List<ContentProviderOperation> prepareBatchOperations();

    protected abstract OttoEvent preparePostEvent();

    public void run() {
        new NGAsyncTask(context, this).run();
    }
}
