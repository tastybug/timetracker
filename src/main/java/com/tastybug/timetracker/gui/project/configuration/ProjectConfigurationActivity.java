package com.tastybug.timetracker.gui.project.configuration;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;
import com.tastybug.timetracker.task.project.ProjectConfiguredEvent;

public class ProjectConfigurationActivity extends Activity {

    public static final String PROJECT_UUID = "PROJECT_UUID";

    private String projectUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_configuration);
        setupActionBar();
        setOrRestoreState(savedInstanceState);
    }

    private void setOrRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
        } else {
            Intent intent = getIntent();
            projectUuid = intent.getStringExtra(PROJECT_UUID);
        }

        Project project = getProjectByUuid(projectUuid);
        TrackingConfiguration trackingConfiguration = getTrackingConfigurationByProjectUuid(projectUuid);

        setTitle(getString(R.string.project_configuration_for_project_X, project.getTitle()));

        ProjectConfigurationFragment configurationFragment = (ProjectConfigurationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_project_configuration);
        configurationFragment.showProject(project);


        TrackingConfigurationFragment constraintFragment = (TrackingConfigurationFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_tracking_configuration);
        constraintFragment.showTrackingConfiguration(trackingConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.activity_project_configuration, menu);

        return true;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PROJECT_UUID, projectUuid);
    }

    // TODO mehrfach implementiert
    protected Project getProjectByUuid(String uuid) {
        return new ProjectDAO(this).get(uuid).get();
    }

    protected TrackingConfiguration getTrackingConfigurationByProjectUuid(String projectUuid) {
        return new TrackingConfigurationDAO(this).getByProjectUuid(projectUuid).get();
    }

    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_confirm_project_configuration:
                if (isConfigurationValid()) {
                    ConfigureProjectTask task = buildProjectConfigurationTask();
                    task.execute();
                }
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private boolean isConfigurationValid() {
        ProjectConfigurationFragment configurationFragment = getProjectConfigurationFragment();
        TrackingConfigurationFragment constraintFragment = getTrackingConfigurationFragment();

        return configurationFragment.validateSettings()
                && constraintFragment.validateSettings();
    }

    private ConfigureProjectTask buildProjectConfigurationTask() {
        ConfigureProjectTask task = ConfigureProjectTask.aTask(this).withProjectUuid(projectUuid);
        ProjectConfigurationFragment configurationFragment = getProjectConfigurationFragment();
        TrackingConfigurationFragment constraintFragment = getTrackingConfigurationFragment();

        configurationFragment.collectModifications(task);
        constraintFragment.collectModifications(task);

        return task;
    }

    private ProjectConfigurationFragment getProjectConfigurationFragment() {
        return (ProjectConfigurationFragment) getFragmentManager().findFragmentById(R.id.fragment_project_configuration);
    }

    private TrackingConfigurationFragment getTrackingConfigurationFragment() {
        return (TrackingConfigurationFragment) getFragmentManager().findFragmentById(R.id.fragment_tracking_configuration);
    }

    @Subscribe public void handleProjectConfiguredEvent(ProjectConfiguredEvent event) {
        onBackPressed();
    }
}