package com.tastybug.timetracker.core.ui.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tastybug.timetracker.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_configuration);
        setTitle(R.string.application_settings_activity_title);
    }
}
