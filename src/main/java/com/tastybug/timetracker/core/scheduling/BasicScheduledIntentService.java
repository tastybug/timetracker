package com.tastybug.timetracker.core.scheduling;

import android.app.IntentService;
import android.content.Intent;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public abstract class BasicScheduledIntentService extends IntentService {

    public BasicScheduledIntentService(String serviceName) {
        super(serviceName);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isEnabled()) {
            logInfo(getClass().getSimpleName(), "Executing '%s' now.", getTopic());
            perform();
        } else {
            logInfo(getClass().getSimpleName(), "Executing '%s' disabled, bailing.", getTopic());
        }
    }

    protected abstract String getTopic();

    protected abstract void perform();

    protected boolean isEnabled() {
        return true;
    }
}
