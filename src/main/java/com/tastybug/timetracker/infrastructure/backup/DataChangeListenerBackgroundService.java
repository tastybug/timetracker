package com.tastybug.timetracker.infrastructure.backup;

import android.app.Service;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.task.project.ProjectConfiguredEvent;
import com.tastybug.timetracker.task.project.ProjectDeletedEvent;
import com.tastybug.timetracker.task.project.create.ProjectCreatedEvent;
import com.tastybug.timetracker.task.tracking.CheckInEvent;
import com.tastybug.timetracker.task.tracking.CheckOutEvent;
import com.tastybug.timetracker.task.tracking.DeletedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

import static com.tastybug.timetracker.util.ConditionalLog.logDebug;
import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class DataChangeListenerBackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);
        logInfo(getClass().getSimpleName(), "started.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new OttoProvider().getSharedBus().unregister(this);
        logInfo(getClass().getSimpleName(), "stopped.");
    }

    private void requestBackup(OttoEvent ottoEvent) {
        new BackupManager(this).dataChanged();
        logDebug(getClass().getSimpleName(), "Requested backup after " + ottoEvent.getClass().getSimpleName());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectCreated(ProjectCreatedEvent event) {
        requestBackup(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectConfigured(ProjectConfiguredEvent event) {
        requestBackup(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectDeleted(ProjectDeletedEvent event) {
        requestBackup(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckIn(CheckInEvent event) {
        requestBackup(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckOut(CheckOutEvent event) {
        requestBackup(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordConfigured(ModifiedTrackingRecordEvent event) {
        requestBackup(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordDeleted(DeletedTrackingRecordEvent event) {
        requestBackup(event);
    }

}
