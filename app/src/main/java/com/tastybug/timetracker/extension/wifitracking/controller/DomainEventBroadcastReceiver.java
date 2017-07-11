package com.tastybug.timetracker.extension.wifitracking.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.core.task.project.ProjectChangeIntent;
import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;
import com.tastybug.timetracker.extension.wifitracking.controller.checkin.TriggerRepository;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class DomainEventBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo(getClass().getSimpleName(), "Received Event %s", intent.getAction());

        if (ProjectChangeIntent.ACTION.equals(intent.getAction())) {
            ProjectChangeIntent pci = new ProjectChangeIntent(intent);
            switch(pci.getChangeType()) {
                case DELETE:
                    handleProjectDeleted(context, pci.getProjectUuid());
            }

        } else if (TrackingRecordChangeIntent.ACTION.equals(intent.getAction())) {
            TrackingRecordChangeIntent trci = new TrackingRecordChangeIntent(intent);
            switch (trci.getChangeType()) {
                case CHECKOUT:
                    handleCheckOut(context, trci.getTrackingRecordUuid());
                    return;
                case UPDATE:
                    handleTrackingRecordUpdate(context, trci.getTrackingRecordUuid(), trci.wasStopped());
            }
        }
    }

    public void handleTrackingRecordUpdate(Context context, String trackingRecordUuid, boolean wasStopped) {
        if (wasStopped) {
            handleCheckOut(context, trackingRecordUuid);
        }
    }

    private void handleCheckOut(Context context, String trackingRecordUuid) {
        new SessionsLog(context).deleteSession(trackingRecordUuid);
    }

    public void handleProjectDeleted(Context context, String projectUuid) {
        new TriggerRepository(context).deleteByProjectUuid(projectUuid);
        new SessionsLog(context).deleteInvalidSessions();
    }
}
