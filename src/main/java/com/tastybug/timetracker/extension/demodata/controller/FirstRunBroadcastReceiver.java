package com.tastybug.timetracker.extension.demodata.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logInfo;

public class FirstRunBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        logInfo(getClass().getSimpleName(), "Installing demo data for first run.");

        new DemoDataGenerationTask(context).run();
    }
}
