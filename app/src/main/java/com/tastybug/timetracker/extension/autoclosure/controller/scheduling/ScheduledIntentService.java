package com.tastybug.timetracker.extension.autoclosure.controller.scheduling;

import com.tastybug.timetracker.core.scheduling.BasicScheduledIntentService;
import com.tastybug.timetracker.extension.autoclosure.controller.AutoClosureService;

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
        new AutoClosureService(getApplicationContext()).performGlobalAutoClose();
    }
}
