package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Intent;

class AppStartPropagationIntent extends Intent {

    private static final String ACTION = "com.tastybug.timetracker.APP_START";

    AppStartPropagationIntent() {
        setAction(ACTION);
    }
}
