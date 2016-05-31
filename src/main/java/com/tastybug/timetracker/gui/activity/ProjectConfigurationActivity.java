package com.tastybug.timetracker.gui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.gui.dialog.navigation.ConfirmBackpressDialogFragment;
import com.tastybug.timetracker.gui.fragment.project.configuration.ProjectConfigurationFragment;
import com.tastybug.timetracker.gui.fragment.project.configuration.TrackingConfigurationFragment;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;
import com.tastybug.timetracker.task.project.ProjectConfiguredEvent;

public class ProjectConfigurationActivity extends BaseActivity {

    public static final String PROJECT_UUID = "PROJECT_UUID";

    private String projectUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_configuration);
        setupActionBar();
        setOrRestoreState(savedInstanceState);
    }

    protected void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    private void setOrRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            projectUuid = savedInstanceState.getString(PROJECT_UUID);
        } else {
            Intent intent = getIntent();
            projectUuid = intent.getStringExtra(PROJECT_UUID);
        }

        Project project = getProjectByUuid(projectUuid);
        setTitle(getString(R.string.project_configuration_for_project_X, project.getTitle()));

        initProjectConfigurationFragment(project);
        initTrackingConfigurationFragment(getTrackingConfigurationByProjectUuid(projectUuid));
    }

    private void initProjectConfigurationFragment(Project project) {
        ProjectConfigurationFragment configurationFragment = getProjectConfigurationFragment();
        configurationFragment.showProject(project);
    }

    private void initTrackingConfigurationFragment(TrackingConfiguration trackingConfiguration) {
        TrackingConfigurationFragment constraintFragment = getTrackingConfigurationFragment();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_confirm_project_configuration:
                performSaveProjectConfiguration();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private void performSaveProjectConfiguration() {
        if (isConfigurationValid()) {
            ConfigureProjectTask task = buildProjectConfigurationTask();
            task.execute();
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

    @Override
    public void onBackPressed() {
        if (hasFragmentWithUnsavedModifications()) {
            showConfirmBackButtonPressLossDialog();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasFragmentWithUnsavedModifications() {
        Project project = getProjectByUuid(projectUuid);
        TrackingConfiguration configuration = new TrackingConfigurationDAO(this).getByProjectUuid(projectUuid).get();
        return getProjectConfigurationFragment().hasUnsavedModifications(project)
                || getTrackingConfigurationFragment().hasUnsavedModifications(configuration);
    }

    private void showConfirmBackButtonPressLossDialog() {
        ConfirmBackpressDialogFragment
                .aDialog()
                .forEntityUuid(projectUuid)
                .show(getFragmentManager(), getClass().getSimpleName());
    }

    @Subscribe public void handleProjectConfiguredEvent(ProjectConfiguredEvent event) {
        super.onBackPressed();
    }

    @Subscribe public void handleSaveThenBackpressRequestedEvent(ConfirmBackpressDialogFragment.SaveThenBackpressRequestedEvent event) {
        performSaveProjectConfiguration();
    }

    @Subscribe public void handleDiscardThenBackpressRequestedEvent(ConfirmBackpressDialogFragment.DiscardThenBackpressRequestedEvent event) {
        super.onBackPressed();
    }
}