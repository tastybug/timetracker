package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Intent;

class FirstRunPropagationIntent extends Intent {

    private static final String ACTION = "com.tastybug.timetracker.FIRST_RUN";

    FirstRunPropagationIntent() {
        setAction(ACTION);
    }
}
