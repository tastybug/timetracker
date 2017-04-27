package com.tastybug.timetracker.extension.backup.controller.localbackup.scheduling;

import android.app.IntentService;
import android.content.Intent;

import com.tastybug.timetracker.extension.backup.controller.localbackup.LocalBackupService;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class AlarmCallback extends IntentService {

    private static final String TAG = AlarmCallback.class.getSimpleName();

    public AlarmCallback() {
        super(AlarmCallback.class.getSimpleName());
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
