package com.tastybug.timetracker.infrastructure.backup;

import android.app.Service;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.task.project.ProjectConfiguredEvent;
import com.tastybug.timetracker.task.project.ProjectCreatedEvent;
import com.tastybug.timetracker.task.project.ProjectDeletedEvent;
import com.tastybug.timetracker.task.tracking.DeletedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

public class DataChangeListenerBackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);
        Log.i(getClass().getSimpleName(), "started.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new OttoProvider().getSharedBus().unregister(this);
        Log.i(getClass().getSimpleName(), "stopped.");
    }

    private void requestBackup(OttoEvent ottoEvent) {
        new BackupManager(this).dataChanged();
        Log.d(getClass().getSimpleName(), "Requested backup after " + ottoEvent.getClass().getSimpleName());
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
    public void handleTrackingStarted(KickStartedTrackingRecordEvent event) {
        requestBackup(event);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingStopped(KickStoppedTrackingRecordEvent event) {
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
