package com.tastybug.timetracker.extension.checkoutreminder.controller.scheduling;

import com.tastybug.timetracker.core.scheduling.BasicScheduler;
import com.tastybug.timetracker.core.scheduling.BasicSchedulerStarter;

public class SchedulerStarter extends BasicSchedulerStarter {

    @Override
    protected String getTopic() {
        return Scheduler.TOPIC;
    }

    @Override
    protected BasicScheduler createScheduler() {
        return new Scheduler();
    }
}