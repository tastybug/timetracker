package com.tastybug.timetracker.gui.fragment.project.configuration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.gui.activity.NGProjectConfigurationActivity;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;
import com.tastybug.timetracker.task.project.ProjectConfiguredEvent;

public class NGProjectConfigurationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String projectUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.projectUuid = savedInstanceState.getString(NGProjectConfigurationActivity.PROJECT_UUID);
        } else {
            this.projectUuid = getActivity().getIntent().getExtras().getString(NGProjectConfigurationActivity.PROJECT_UUID);
        }

        Project project = getProjectFromDB();
        initPreferencesWithDataFromProject(project);
        addPreferencesFromResource(R.xml.project_preferences);
        setSummaries(project);
    }

    private Project getProjectFromDB() {
        return new ProjectDAO(getActivity()).get(this.projectUuid).get();
    }

    private void initPreferencesWithDataFromProject(Project project) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPref.edit()
                .putString(getString(R.string.project_title_preference_key), project.getTitle())
                .putString(getString(R.string.project_description_preference_key), project.getDescription().or(""))
                .apply();
    }

    private void setSummaries(Project project) {
        findPreference(getString(R.string.project_title_preference_key)).setSummary(project.getTitle());
        findPreference(getString(R.string.project_description_preference_key)).setSummary(project.getDescription().or("none"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NGProjectConfigurationActivity.PROJECT_UUID, projectUuid);
    }

    @Override
    public void onResume() {
        super.onResume();
        new OttoProvider().getSharedBus().register(this);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        Project project = getProjectFromDB();
        String title = sharedPreferences.getString(getString(R.string.project_title_preference_key),"");
        String description = sharedPreferences.getString(getString(R.string.project_description_preference_key),"");

        if (!isTitleValid(title)) {
            revertChanges(project, sharedPreferences);
            showWarningInvalidTitle();
        } else if (!isDescriptionValid(description)) {
            revertChanges(project, sharedPreferences);
            showWarningInvalidDescription();
        } else {
            saveChanges(title, description);
        }
    }

    private boolean isTitleValid(String projectTitle) {
        try {
            Project project1 = new Project("");
            project1.setTitle(projectTitle);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isDescriptionValid(String description) {
        try {
            Project project1 = new Project("");
            project1.setDescription(Optional.of(description));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void showWarningInvalidTitle() {
        Toast.makeText(getActivity(), R.string.warning_invalid_title_was_reverted, Toast.LENGTH_LONG).show();
    }

    private void showWarningInvalidDescription() {
        Toast.makeText(getActivity(), R.string.warning_invalid_description_was_reverted, Toast.LENGTH_LONG).show();
    }

    private void revertChanges(Project project, SharedPreferences sharedPreferences) {
        sharedPreferences
                .edit()
                .putString(getString(R.string.project_title_preference_key), project.getTitle())
                .putString(getString(R.string.project_description_preference_key), project.getDescription().or(""))
                .apply();
    }

    private void saveChanges(String projectTitle, String description) {
        ConfigureProjectTask.aTask(getActivity())
                .withProjectUuid(projectUuid)
                .withProjectTitle(projectTitle)
                .withProjectDescription(description)
                .execute();
    }

    @Subscribe
    public void handleProjectConfigured(ProjectConfiguredEvent event) {
        if (event.getProjectUuid().equals(projectUuid)) {
            Project project = getProjectFromDB();
            setSummaries(project);
        }
    }
}