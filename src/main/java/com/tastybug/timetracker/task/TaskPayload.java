package com.tastybug.timetracker.task;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Bundle;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import java.util.List;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public abstract class TaskPayload {

    protected Context context;
    protected Bundle arguments = new Bundle();
    protected OttoProvider ottoProvider;

    public TaskPayload(Context context, OttoProvider ottoProvider) {
        this.context = context;
        this.ottoProvider = ottoProvider;
    }

    protected abstract void validate() throws IllegalArgumentException, NullPointerException;

    protected abstract List<ContentProviderOperation> prepareBatchOperations();

    protected abstract OttoEvent preparePostEvent();

    void firePostEvent() {
        OttoEvent event = preparePostEvent();
        logInfo(getClass().getSimpleName(), "Finished! Firing " + event);
        ottoProvider.getSharedBus().post(event);
    }

    public void run() {
        new NGAsyncTask(context, this).run();
    }
}
