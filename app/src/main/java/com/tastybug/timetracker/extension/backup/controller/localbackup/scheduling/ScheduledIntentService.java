package com.tastybug.timetracker.extension.backup.controller.localbackup.scheduling;

import com.tastybug.timetracker.core.scheduling.BasicScheduledIntentService;
import com.tastybug.timetracker.extension.backup.controller.localbackup.LocalBackupService;

public class ScheduledIntentService extends BasicScheduledIntentService {

    public ScheduledIntentService() {
        super(ScheduledIntentService.class.getSimpleName());
    }

    @Override
    protected String getTopic() {
        return Scheduler.TOPIC;
    }

    @Override
    protected void perform() {
        new LocalBackupService(getApplicationContext()).performBackup();
    }

    @Override
    protected boolean isEnabled() {
        return new ScheduleSettings(getApplicationContext()).isBackupEnabled();
    }
}
