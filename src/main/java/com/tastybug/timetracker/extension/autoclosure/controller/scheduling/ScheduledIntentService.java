package com.tastybug.timetracker.extension.autoclosure.controller.scheduling;

import android.app.IntentService;
import android.content.Intent;

import com.tastybug.timetracker.extension.autoclosure.controller.AutoClosureService;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class ScheduledIntentService extends IntentService {

    private static final String TAG = ScheduledIntentService.class.getSimpleName();

    public ScheduledIntentService() {
        super(ScheduledIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        logInfo(TAG, "Starting autoclosure NOW!");
        new AutoClosureService(getApplicationContext()).performGlobalAutoClose();
    }
}
