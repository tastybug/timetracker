package com.tastybug.timetracker.extension.wifitracking.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.core.task.project.delete.ProjectDeletedEvent;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutEvent;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordEvent;
import com.tastybug.timetracker.extension.wifitracking.controller.checkin.TriggerRepository;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

public class WifiTriggerLifecycleService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckOut(CheckOutEvent event) {
        handleCheckout(event.getTrackingRecord().getUuid());
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordUpdate(UpdateTrackingRecordEvent event) {
        if (event.wasStopped()) {
            handleCheckout(event.getTrackingRecord().getProjectUuid());
        }
    }

    private void handleCheckout(String trackingRecordUuid) {
        new SessionsLog(getApplicationContext()).deleteSession(trackingRecordUuid);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectDeleted(ProjectDeletedEvent event) {
        removeTriggerForProject(event.getProjectUuid());
        removeSessionsWithInvalidTrackingRecords();
    }

    private void removeTriggerForProject(String projectUuid) {
        new TriggerRepository(getApplicationContext()).deleteByProjectUuid(projectUuid);
    }

    private void removeSessionsWithInvalidTrackingRecords() {
        new SessionsLog(getApplicationContext()).deleteInvalidSessions();
    }
}
