package com.tastybug.timetracker.extension.warning;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutEvent;
import com.tastybug.timetracker.extension.warning.completion.CompletionWarningService;
import com.tastybug.timetracker.extension.warning.expiration.ExpirationWarningService;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class WarningEventListenerService extends Service {

    private CompletionWarningService completionWarningService;
    private ExpirationWarningService expirationWarningService;

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        new OttoProvider().getSharedBus().register(this);
        completionWarningService = new CompletionWarningService(this);
        expirationWarningService = new ExpirationWarningService(this);

        logInfo(WarningEventListenerService.class.getSimpleName(), "started.");
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
        completionWarningService.handleProjectStopped(event.getTrackingRecord().getProjectUuid());
        expirationWarningService.handleProjectStopped(event.getTrackingRecord().getProjectUuid());
    }

}
