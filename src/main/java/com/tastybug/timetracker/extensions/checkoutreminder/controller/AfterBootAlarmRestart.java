package com.tastybug.timetracker.extensions.checkoutreminder.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class AfterBootAlarmRestart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            logInfo(AfterBootAlarmRestart.class.getSimpleName(), "After reboot: restarting checkout reminder service alarm.");
            new ReminderAlarmSetup().setAlarm(context);
        }
    }
}