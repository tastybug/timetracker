package com.tastybug.timetracker.extension.autoclosure.controller.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.core.util.BasicAlarmSetup;

class AlarmSetup extends BasicAlarmSetup {

    @Override
    protected String getAlarmTopic() {
        return "auto closure";
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
