package com.tastybug.timetracker.extension.backup.controller.localbackup.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.core.scheduling.BasicScheduler;

class Scheduler extends BasicScheduler {

    static final String TOPIC = "Local Auto Backup";

    @Override
    protected String getAlarmTopic() {
        return TOPIC;
    }

    protected PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, ScheduledIntentService.class);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    @Override
    protected long getReleaseFrequency() {
        return AlarmManager.INTERVAL_DAY;
    }
}
