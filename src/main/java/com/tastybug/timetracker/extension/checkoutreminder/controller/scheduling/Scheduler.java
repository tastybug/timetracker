package com.tastybug.timetracker.extension.checkoutreminder.controller.scheduling;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tastybug.timetracker.core.scheduling.BasicScheduler;
import com.tastybug.timetracker.extension.checkoutreminder.controller.CheckoutReminderService;

class Scheduler extends BasicScheduler {

    static final String TOPIC = "Checkout Reminder";

    @Override
    protected String getAlarmTopic() {
        return TOPIC;
    }

    protected PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, CheckoutReminderService.class);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    @Override
    protected long getReleaseFrequency() {
        return 1000 * 60 * 60 * 3;
    }
}
