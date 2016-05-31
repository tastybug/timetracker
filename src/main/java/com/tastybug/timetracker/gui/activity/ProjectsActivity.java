package com.tastybug.timetracker.gui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.eventhandler.PostTrackingKickStopHandler;

public class ProjectsActivity extends Activity {

    private PostTrackingKickStopHandler postTrackingKickStopHandler;

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
        postTrackingKickStopHandler = new PostTrackingKickStopHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        postTrackingKickStopHandler.stop();
    }
}