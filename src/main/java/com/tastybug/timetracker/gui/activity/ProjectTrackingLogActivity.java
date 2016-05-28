package com.tastybug.timetracker.gui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.eventhandler.DescribeOrDropTinyRecordHandler;
import com.tastybug.timetracker.gui.fragment.trackingrecord.list.TrackingRecordListFragment;
import com.tastybug.timetracker.gui.fragment.trackingrecord.log.TrackingLogStatisticsFragment;
import com.tastybug.timetracker.model.Project;

public class ProjectTrackingLogActivity extends BaseActivity {

    public static final String PROJECT_UUID = "PROJECT_UUID";

    private DescribeOrDropTinyRecordHandler describeOrDropTinyRecordHandler;
    private String projectUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_tracking_record_log);
//        setupActionBar();

        if (savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
        } else {
            Intent intent = getIntent();
            projectUuid = intent.getStringExtra(PROJECT_UUID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        describeOrDropTinyRecordHandler = new DescribeOrDropTinyRecordHandler(this);

        TrackingLogStatisticsFragment trackingLogStatisticsFragment = getTrackingLogStatisticsFragment();
        TrackingRecordListFragment trackingRecordListFragment = getTrackingLogFragment();

        Project project = getProjectByUuid(projectUuid);
        setTitle(getProjectByUuid(projectUuid).getTitle());

        trackingLogStatisticsFragment.showProjectDetailsFor(project);
        trackingRecordListFragment.showProject(projectUuid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        describeOrDropTinyRecordHandler.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
    }

    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // backpress geht hier nicht, da wir nicht notwendigerweise nach 'oben' gingen
                startActivity(new Intent(this, ProjectsActivity.class));
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }
}