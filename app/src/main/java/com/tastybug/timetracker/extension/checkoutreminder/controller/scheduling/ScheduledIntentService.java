package com.tastybug.timetracker.extension.checkoutreminder.controller.scheduling;

import com.tastybug.timetracker.core.scheduling.BasicScheduledIntentService;
import com.tastybug.timetracker.extension.checkoutreminder.controller.CheckoutReminderService;

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
        new CheckoutReminderService(getApplicationContext()).perform();
    }

    @Override
    protected boolean isEnabled() {
        return new ReminderSettings(getApplicationContext()).isEnabled();
    }

}
