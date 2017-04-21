package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Intent;

class ApplicationStartIntent extends Intent {

    private static final String ACTION = "com.tastybug.timetracker.APP_START";

    ApplicationStartIntent() {
        setAction(ACTION);
    }
}
