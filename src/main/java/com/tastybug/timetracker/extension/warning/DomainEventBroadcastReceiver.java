package com.tastybug.timetracker.extension.warning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.core.task.tracking.TrackingRecordChangeIntent;
import com.tastybug.timetracker.extension.warning.completion.CompletionWarningService;
import com.tastybug.timetracker.extension.warning.expiration.ExpirationWarningService;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class DomainEventBroadcastReceiver extends BroadcastReceiver {

    private CompletionWarningService completionWarningService;
    private ExpirationWarningService expirationWarningService;

    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo(getClass().getSimpleName(), "Received Event %s", intent.getAction());

        completionWarningService = new CompletionWarningService(context);
        expirationWarningService = new ExpirationWarningService(context);

        if (TrackingRecordChangeIntent.ACTION.equals(intent.getAction())) {
            TrackingRecordChangeIntent trci = new TrackingRecordChangeIntent(intent);
            switch (trci.getChangeType()) {
                case CHECKOUT:
                    handleCheckOut(trci.getProjectUuid());
            }
        }
    }

    public void handleCheckOut(String projectUuid) {
        completionWarningService.handleProjectStopped(projectUuid);
        expirationWarningService.handleProjectStopped(projectUuid);
    }

}