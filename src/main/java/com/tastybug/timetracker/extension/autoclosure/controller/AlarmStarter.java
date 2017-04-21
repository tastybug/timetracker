package com.tastybug.timetracker.extension.autoclosure.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class AlarmStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo(AlarmStarter.class.getSimpleName(), "Starting auto closure service alarm.");
        new AlarmSetup().setAlarm(context);
    }
}