package com.tastybug.timetracker.extension.backup.controller.localbackup.scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmArming extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new AlarmSetup().setAlarm(context);
    }
}