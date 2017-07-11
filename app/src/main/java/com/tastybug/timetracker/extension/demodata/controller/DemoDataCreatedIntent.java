package com.tastybug.timetracker.extension.demodata.controller;

import android.content.Intent;

public class DemoDataCreatedIntent extends Intent {

    private static final String ACTION = "com.tastybug.timetracker.DEMO_DATA_CREATED";
    private static final String CATEGORY = "com.tastybug.timetracker.INFRASTRUCTURAL_EVENT";

    public DemoDataCreatedIntent() {
        setAction(ACTION);
        addCategory(CATEGORY);
    }
}
