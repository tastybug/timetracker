package com.tastybug.timetracker.gui.dashboard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.project.detail.ProjectDetailsActivity;
import com.tastybug.timetracker.gui.project.detail.ProjectStatisticsFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ProjectDeletedEvent;

public class ProjectsActivity extends Activity implements ProjectListFragment.ProjectListSelectionListener {

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
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    public void onProjectSelected(Project project) {
        showProjectDetails(project);
    }

    private Optional<ProjectStatisticsFragment> getTwoPaneProjectDetailFragment() {
        ProjectStatisticsFragment detailsFragment = (ProjectStatisticsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_statistics);
        return Optional.fromNullable(detailsFragment);
    }

    private void showProjectDetails(Project project) {
        Optional<ProjectStatisticsFragment> detailsFragmentOpt = getTwoPaneProjectDetailFragment();
        if (detailsFragmentOpt.isPresent()) {
            detailsFragmentOpt.get().showProjectDetailsFor(project);
        } else {
            Intent intent = new Intent(this, ProjectDetailsActivity.class);
            intent.putExtra(ProjectDetailsActivity.PROJECT_UUID, project.getUuid());
            startActivity(intent);
        }
    }

    private void showNoProjectDetails() {
        Optional<ProjectStatisticsFragment> detailsFragmentOpt = getTwoPaneProjectDetailFragment();
        if (detailsFragmentOpt.isPresent()) {
            detailsFragmentOpt.get().showNoProject();
        }
        // if its a different activity, simply do not open it
    }

    @Subscribe public void handleProjectDeletedEvent(ProjectDeletedEvent event) {
        Log.d(getClass().getSimpleName(), "Deleted project " + event.getProjectUuid());
        showNoProjectDetails();
    }
}