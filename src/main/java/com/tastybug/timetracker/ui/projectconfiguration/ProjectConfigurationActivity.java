package com.tastybug.timetracker.ui.projectconfiguration;

import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.ui.core.BaseActivity;

public class ProjectConfigurationActivity extends BaseActivity {

    public static final String PROJECT_UUID = "PROJECT_UUID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_configuration);
        setTitle(R.string.project_configuration_activity_title);
    }
}
