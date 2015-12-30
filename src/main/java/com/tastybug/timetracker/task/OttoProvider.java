package com.tastybug.timetracker.task;

import com.squareup.otto.Bus;

public class OttoProvider {

    public static Bus bus = new Bus();

    public Bus getSharedBus() {
        return bus;
    }
}
