package com.tastybug.timetracker.ui.core;

import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

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
