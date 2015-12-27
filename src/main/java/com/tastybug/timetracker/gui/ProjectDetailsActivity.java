package com.tastybug.timetracker.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.projectdetail.ProjectDetailFragment;
import com.tastybug.timetracker.gui.projects.ProjectListFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.util.VersionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectDetailsActivity extends Activity {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String PROJECT_UUID = "PROJECT_UUID";

    private String projectUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        setupActionBar();

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

        ProjectDetailFragment detailsFragment = (ProjectDetailFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_detail);

        detailsFragment.showProjectDetailsFor(getProjectByUuid(projectUuid));
    }

    protected Project getProjectByUuid(String uuid) {
        return new Project(uuid, "SYNTETIC PROJECT", null);
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
                startActivity(new Intent(this, ProjectsActivity.class));
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }
}