package com.tastybug.timetracker.core.ui.projectdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.task.project.delete.ProjectDeletedEvent;
import com.tastybug.timetracker.core.ui.core.AbstractOttoEventHandler;
import com.tastybug.timetracker.core.ui.core.BaseActivity;
import com.tastybug.timetracker.core.ui.dashboard.ProjectsActivity;
import com.tastybug.timetracker.core.ui.delegate.CheckoutAskForDescriptionDelegate;

import static com.tastybug.timetracker.infrastructure.util.ConditionalLog.logDebug;

public class ProjectDetailsActivity extends BaseActivity {

    public static final String PROJECT_UUID = "PROJECT_UUID";

    private CheckoutAskForDescriptionDelegate checkoutAskForDescriptionDelegate;
    private BackPressOnProjectDeletion backPressOnProjectDeletion;
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
        checkoutAskForDescriptionDelegate = new CheckoutAskForDescriptionDelegate(this);
        backPressOnProjectDeletion = new BackPressOnProjectDeletion();

        ProjectStatisticsFragment detailsFragment = getProjectStatisticsFragment();
        TrackingRecordListFragment trackingRecordListFragment = getTrackingRecordListFragment();

        Project project = getProjectByUuid(projectUuid);
        setTitle(getProjectByUuid(projectUuid).getTitle());

        detailsFragment.showProjectDetailsFor(project);
        trackingRecordListFragment.showProject(projectUuid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkoutAskForDescriptionDelegate.stop();
        backPressOnProjectDeletion.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
    }

    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
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

    protected ProjectStatisticsFragment getProjectStatisticsFragment() {
        return (ProjectStatisticsFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_statistics);
    }

    protected TrackingRecordListFragment getTrackingRecordListFragment() {
        return (TrackingRecordListFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_record_list);
    }

    private class BackPressOnProjectDeletion extends AbstractOttoEventHandler {

        @SuppressWarnings("unused")
        @Subscribe
        public void handleProjectDeletedEvent(ProjectDeletedEvent event) {
            /*
            Das Eventhandling muss in der Activity passieren, da das Fragment nicht weiss, ob es two-pane oder single-pane
            ausgefuehrt wird. Ergo muss die Activity entscheiden, wie eine Projektloeschung sich navigatorisch auswirkt.
             */
            logDebug(getClass().getSimpleName(), "Deleted project " + event.getProjectUuid());
            onBackPressed();
        }
    }
}