package com.tastybug.timetracker.extension.backup.controller.localbackup.scheduling;

import android.app.IntentService;
import android.content.Intent;

import com.tastybug.timetracker.extension.backup.controller.localbackup.LocalBackupService;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class ScheduledIntentService extends IntentService {

    private static final String TAG = ScheduledIntentService.class.getSimpleName();

    public ScheduledIntentService() {
        super(ScheduledIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        logInfo(TAG, "Local backup ..");
        if (getLocalBackupSettings().isBackupEnabled()) {
            logInfo(TAG, ".. starting now.");
            new LocalBackupService(getApplicationContext()).performBackup();
        } else {
            logInfo(TAG, ".. disabled, nothing to be done.");
        }
    }

    private ScheduleSettings getLocalBackupSettings() {
        return new ScheduleSettings(getApplicationContext());
    }
}
