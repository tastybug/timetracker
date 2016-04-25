package com.tastybug.timetracker.gui.eventhandler;

import com.tastybug.timetracker.task.OttoProvider;

public class AbstractOttoEventHandler {

    public AbstractOttoEventHandler() {
        start();
    }

    public void start() {
        new OttoProvider().getSharedBus().register(this);
    }

    public void stop() {
        new OttoProvider().getSharedBus().unregister(this);
    }
}
