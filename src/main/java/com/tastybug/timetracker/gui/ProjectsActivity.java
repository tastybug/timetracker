package com.tastybug.timetracker.gui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.projectdetail.ProjectDetailFragment;
import com.tastybug.timetracker.gui.projects.ProjectListFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ProjectDeletedEvent;
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
    protected void onResume() {
        super.onResume();

        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    public void onProjectSelected(Project project) {
        logger.info("Selected " + project.toString());
        showProjectDetails(project);
    }

    private Optional<ProjectDetailFragment> getTwoPaneProjectDetailFragment() {
        ProjectDetailFragment detailsFragment = (ProjectDetailFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_detail);
        return Optional.fromNullable(detailsFragment);
    }

    private void showProjectDetails(Project project) {
        Optional<ProjectDetailFragment> detailsFragmentOpt = getTwoPaneProjectDetailFragment();
        if (detailsFragmentOpt.isPresent()) {
            detailsFragmentOpt.get().showProjectDetailsFor(project);
        } else {
            Intent intent = new Intent(this, ProjectDetailsActivity.class);
            intent.putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
            startActivity(intent);
        }
    }

    private void showNoProjectDetails() {
        Optional<ProjectDetailFragment> detailsFragmentOpt = getTwoPaneProjectDetailFragment();
        if (detailsFragmentOpt.isPresent()) {
            detailsFragmentOpt.get().showNoProject();
        }
        // if its a different activity, simply do not open it
    }

    @Subscribe
    public void handleProjectDeletedEvent(ProjectDeletedEvent event) {
        Toast.makeText(this, "Deleted project " + event.getProjectUuid(), Toast.LENGTH_SHORT).show();
        showNoProjectDetails();
    }

}