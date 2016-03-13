package com.tastybug.timetracker.gui.project.detail;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.gui.dashboard.ProjectsActivity;
import com.tastybug.timetracker.gui.dialog.EditTrackingRecordDescriptionDialogFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ProjectDeletedEvent;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;

public class ProjectDetailsActivity extends Activity {

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
        new OttoProvider().getSharedBus().register(this);

        ProjectStatisticsFragment detailsFragment = getProjectStatisticsFragment();
        TrackingControlPanelFragment trackingPanelFragment = getTrackingControlPanelFragment();
        TrackingRecordListFragment trackingRecordListFragment = getTrackingRecordListFragment();

        Project project = getProjectByUuid(projectUuid);
        setTitle(getProjectByUuid(projectUuid).getTitle());

        detailsFragment.showProjectDetailsFor(project);
        trackingPanelFragment.renderProject(project);
        trackingRecordListFragment.showProject(projectUuid);
    }

    private ProjectStatisticsFragment getProjectStatisticsFragment() {
        return (ProjectStatisticsFragment) getFragmentManager()
                    .findFragmentById(R.id.fragment_project_statistics);
    }

    private TrackingControlPanelFragment getTrackingControlPanelFragment() {
        return (TrackingControlPanelFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_control_panel);
    }

    private TrackingRecordListFragment getTrackingRecordListFragment() {
        return (TrackingRecordListFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_record_list);
    }

    @Override
    protected void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
    }

    protected Project getProjectByUuid(String uuid) {
        Project project = new ProjectDAO(this).get(uuid).get();
        project.setContext(this);
        return project;
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

    @Subscribe public void handleProjectDeletedEvent(ProjectDeletedEvent event) {
        /*
        Das Eventhandling muss in der Activity passieren, da das Fragment nicht weiss, ob es two-pane oder single-pane
        ausgefuehrt wird. Ergo muss die Activity entscheiden, wie eine Projektloeschung sich navigatorisch auswirkt.
         */
        Log.d(getClass().getSimpleName(), "Deleted project " + event.getProjectUuid());
        onBackPressed();
    }

    @Subscribe public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        EditTrackingRecordDescriptionDialogFragment
                .aDialog()
                .forTrackingRecord(event.getTrackingRecord())
                .show(getFragmentManager(), getClass().getSimpleName());
    }

}