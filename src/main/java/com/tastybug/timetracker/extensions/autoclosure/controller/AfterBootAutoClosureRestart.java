package com.tastybug.timetracker.extensions.autoclosure.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class AfterBootAutoClosureRestart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            logInfo(AfterBootAutoClosureRestart.class.getSimpleName(), "After reboot: restarting auto closure service alarm.");
            new AutoClosureAlarmSetup().setAlarm(context);
        }
    }
}