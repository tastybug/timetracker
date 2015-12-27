package com.tastybug.timetracker.gui;

import android.app.ActionBar;
import android.app.Activity;
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

public class ProjectsActivity extends Activity implements ProjectListFragment.ProjectListSelectionListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        setTitle(R.string.activity_project_title);
        setupActionBar();

        //
        Toast.makeText(this, "Welcome to " + (new VersionUtil(this).getVersionName()), Toast.LENGTH_SHORT).show();
    }

    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false); // disables UP arrow
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Up", Toast.LENGTH_SHORT).show();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    public void onProjectSelected(Project project) {
        logger.info("Selected " + project.toString());
        showProjectDetails(project);
    }

    private void showProjectDetails(Project project) {
        ProjectDetailFragment detailsFragment = (ProjectDetailFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_detail);
        if (detailsFragment == null) {
            Intent intent = new Intent(this, ProjectDetailsActivity.class);
            intent.putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
            startActivity(intent);
        } else {
            detailsFragment.showProjectDetailsFor(project);
        }
    }
}