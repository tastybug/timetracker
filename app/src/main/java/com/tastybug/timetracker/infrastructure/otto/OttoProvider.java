package com.tastybug.timetracker.infrastructure.otto;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class OttoProvider {

    public static Bus bus = new Bus(ThreadEnforcer.ANY);

    public Bus getSharedBus() {
        return bus;
    }
}
