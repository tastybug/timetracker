package com.tastybug.timetracker.core.scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.extension.autoclosure.controller.scheduling.SchedulerStarter;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public abstract class BasicSchedulerStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo(SchedulerStarter.class.getSimpleName(), "Setting up schedule for '%s'.", getTopic());
        createScheduler().setAlarm(context);
    }

    protected abstract String getTopic();

    protected abstract BasicScheduler createScheduler();

}
