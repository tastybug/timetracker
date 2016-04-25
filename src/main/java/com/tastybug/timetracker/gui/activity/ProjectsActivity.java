package com.tastybug.timetracker.gui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.eventhandler.DescribeOrDropTinyRecordHandler;

public class ProjectsActivity extends Activity {

    private DescribeOrDropTinyRecordHandler describeOrDropTinyRecordHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        setTitle(R.string.activity_project_title);
        setupActionBar();
    }

    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false); // disables UP arrow
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        describeOrDropTinyRecordHandler = new DescribeOrDropTinyRecordHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        describeOrDropTinyRecordHandler.stop();
    }
}