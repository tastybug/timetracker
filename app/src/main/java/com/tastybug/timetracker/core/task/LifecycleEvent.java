package com.tastybug.timetracker.core.task;

import android.content.Intent;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public interface LifecycleEvent extends OttoEvent {

    String LIFECYCLE_EVENT_CATEGORY = "com.tastybug.timetracker.LIFECYCLE_EVENT";

    Intent getAsBroadcastEvent();
}
