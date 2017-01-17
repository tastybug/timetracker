package com.tastybug.timetracker.ui.projectconfiguration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.dao.ProjectDAO;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.model.rounding.Rounding;
import com.tastybug.timetracker.task.project.config.ConfigureProjectTask;
import com.tastybug.timetracker.task.project.config.ProjectConfiguredEvent;
import com.tastybug.timetracker.util.DefaultLocaleDateFormatter;

import java.util.Date;

public class ProjectConfigurationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String projectUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.projectUuid = savedInstanceState.getString(ProjectConfigurationActivity.PROJECT_UUID);
        } else {
            this.projectUuid = getActivity().getIntent().getExtras().getString(ProjectConfigurationActivity.PROJECT_UUID);
        }

        Project project = getProjectFromDB();
        TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
        initPreferencesWithDataFromProject(PreferenceManager.getDefaultSharedPreferences(getActivity()), project, trackingConfiguration);
        addPreferencesFromResource(R.xml.project_preferences);
        setSummaries(project, trackingConfiguration);
    }

    private Project getProjectFromDB() {
        return new ProjectDAO(getActivity()).get(this.projectUuid).get();
    }

    private TrackingConfiguration getTrackingConfigurationFromDB() {
        return new TrackingConfigurationDAO(getActivity()).getByProjectUuid(projectUuid).get();
    }

    private void initPreferencesWithDataFromProject(SharedPreferences sharedPref, Project project, TrackingConfiguration trackingConfiguration) {
        sharedPref.edit()
                .putString(getString(R.string.project_title_preference_key), project.getTitle())
                .putString(getString(R.string.project_description_preference_key), project.getDescription().or(""))
                .putInt(getString(R.string.tracking_configuration_hour_limit_preference_key),
                        trackingConfiguration.getHourLimit().or(0))
                .putLong(getString(R.string.tracking_configuration_start_date_preference_key),
                        trackingConfiguration.getStart().isPresent() ? trackingConfiguration.getStart().get().getTime() : -1L)
                .putLong(getString(R.string.tracking_configuration_end_date_inclusive_preference_key),
                        trackingConfiguration.getEnd().isPresent() ? trackingConfiguration.getEndDateAsInclusive().get().getTime() : -1L)
                .putString(getString(R.string.tracking_configuration_rounding_strategy_preference_key),
                        trackingConfiguration.getRoundingStrategy().name())
                .putBoolean(getString(R.string.tracking_configuration_prompt_for_description_preference_key),
                        trackingConfiguration.isPromptForDescription())
                .apply();
    }

    private void setSummaries(Project project, TrackingConfiguration trackingConfiguration) {
        findPreference(getString(R.string.project_title_preference_key)).setSummary(project.getTitle());
        findPreference(getString(R.string.project_description_preference_key)).setSummary(project.getDescription().or(""));
        if (trackingConfiguration.getHourLimit().isPresent()) {
            findPreference(getString(R.string.tracking_configuration_hour_limit_preference_key)).setSummary(getString(R.string.hour_limit_of_X_hours, trackingConfiguration.getHourLimit().get()));
        } else {
            findPreference(getString(R.string.tracking_configuration_hour_limit_preference_key)).setSummary(R.string.tracking_configuration_hour_limit_preference_summary_none);
        }
        if (trackingConfiguration.getStart().isPresent()) {
            findPreference(getString(R.string.tracking_configuration_start_date_preference_key))
                    .setSummary(getString(R.string.starts_at_X, DefaultLocaleDateFormatter.date().format(trackingConfiguration.getStart().get())));
        } else {
            findPreference(getString(R.string.tracking_configuration_start_date_preference_key)).setSummary(R.string.tracking_configuration_start_date_preference_summary_none);
        }
        if (trackingConfiguration.getEnd().isPresent()) {
            findPreference(getString(R.string.tracking_configuration_end_date_inclusive_preference_key))
                    .setSummary(getString(R.string.ends_at_inclusive_X, DefaultLocaleDateFormatter.date().format(trackingConfiguration.getEndDateAsInclusive().get())));
        } else {
            findPreference(getString(R.string.tracking_configuration_end_date_inclusive_preference_key)).setSummary(R.string.tracking_configuration_end_date_inclusive_preference_summary_none);
        }
        findPreference(getString(R.string.tracking_configuration_rounding_strategy_preference_key))
                .setSummary(trackingConfiguration.getRoundingStrategy().getDescriptionStringResource());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ProjectConfigurationActivity.PROJECT_UUID, projectUuid);
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
        TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
        String title = sharedPreferences.getString(getString(R.string.project_title_preference_key), "");
        String description = sharedPreferences.getString(getString(R.string.project_description_preference_key), "");
        Integer hourLimit = sharedPreferences.getInt(getString(R.string.tracking_configuration_hour_limit_preference_key), 0);
        Long startTimeStamp = sharedPreferences.getLong(getString(R.string.tracking_configuration_start_date_preference_key), -1L);
        Optional<Date> startDateOpt = startTimeStamp == -1L ? Optional.<Date>absent() : Optional.of(new Date(startTimeStamp));
        Long endTimeStamp = sharedPreferences.getLong(getString(R.string.tracking_configuration_end_date_inclusive_preference_key), -1L);
        Optional<Date> endDateInclusiveOpt = endTimeStamp == -1L ? Optional.<Date>absent() : Optional.of(new Date(endTimeStamp));
        Boolean promptForDescription = sharedPreferences.getBoolean(getString(R.string.tracking_configuration_prompt_for_description_preference_key), false);
        Rounding.Strategy strategy = Rounding.Strategy.valueOf(sharedPreferences.getString(getString(R.string.tracking_configuration_rounding_strategy_preference_key), Rounding.Strategy.NO_ROUNDING.name()));

        if (key.equals(getString(R.string.project_title_preference_key))
                && !isTitleValid(title)) {
            revertChanges(project, trackingConfiguration, sharedPreferences);
            showWarningInvalidTitle();
        } else if (key.equals(getString(R.string.project_description_preference_key))
                && !isDescriptionValid(description)) {
            revertChanges(project, trackingConfiguration, sharedPreferences);
            showWarningInvalidDescription();
        } else if (key.equals(getString(R.string.tracking_configuration_hour_limit_preference_key))
                && !isHourLimitValid(hourLimit)) {
            revertChanges(project, trackingConfiguration, sharedPreferences);
            showWarningInvalidHourLimit();
        } else if (key.equals(getString(R.string.tracking_configuration_start_date_preference_key))
                && !isStartDateValid(startDateOpt)) {
            revertChanges(project, trackingConfiguration, sharedPreferences);
            showWarningInvalidStartDateLimit();
        } else if (key.equals(getString(R.string.tracking_configuration_end_date_inclusive_preference_key))
                && !isEndDateValid(endDateInclusiveOpt)) {
            revertChanges(project, trackingConfiguration, sharedPreferences);
            showWarningInvalidEndDateLimit();
        } else {
            saveChanges(title, description);
            saveChanges(hourLimit, startDateOpt, endDateInclusiveOpt, promptForDescription, strategy);
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

    private boolean isHourLimitValid(Integer limit) {
        try {
            TrackingConfiguration configuration = new TrackingConfiguration("");
            configuration.setHourLimit(Optional.of(limit));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isStartDateValid(Optional<Date> startDateOpt) {
        TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
        return !startDateOpt.isPresent()
                || (!trackingConfiguration.getEnd().isPresent() || trackingConfiguration.getEnd().get().after(startDateOpt.get()));
    }

    private boolean isEndDateValid(Optional<Date> endDateInclusiveOpt) {
        TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
        return !endDateInclusiveOpt.isPresent()
                || (!trackingConfiguration.getStart().isPresent() || trackingConfiguration.getStart().get().before(endDateInclusiveOpt.get()));
    }

    private void showWarningInvalidHourLimit() {
        Toast.makeText(getActivity(), R.string.warning_invalid_hour_limit_was_reverted, Toast.LENGTH_LONG).show();
    }

    private void showWarningInvalidStartDateLimit() {
        Toast.makeText(getActivity(), R.string.warning_invalid_start_date_was_reverted, Toast.LENGTH_LONG).show();
    }

    private void showWarningInvalidEndDateLimit() {
        Toast.makeText(getActivity(), R.string.warning_invalid_end_date_was_reverted, Toast.LENGTH_LONG).show();
    }

    private void showWarningInvalidTitle() {
        Toast.makeText(getActivity(), R.string.warning_invalid_title_was_reverted, Toast.LENGTH_LONG).show();
    }

    private void showWarningInvalidDescription() {
        Toast.makeText(getActivity(), R.string.warning_invalid_description_was_reverted, Toast.LENGTH_LONG).show();
    }

    private void revertChanges(Project project, TrackingConfiguration configuration, SharedPreferences sharedPreferences) {
        initPreferencesWithDataFromProject(sharedPreferences, project, configuration);
    }

    private void saveChanges(String projectTitle, String description) {
        new ConfigureProjectTask(getActivity())
                .withProjectUuid(projectUuid)
                .withProjectTitle(projectTitle)
                .withProjectDescription(description)
                .execute();
    }

    private void saveChanges(Integer hourLimit, Optional<Date> startDateOpt, Optional<Date> endDateInclusiveOpt, Boolean promptForDescription, Rounding.Strategy strategy) {
        ConfigureProjectTask task = new ConfigureProjectTask(getActivity())
                .withProjectUuid(projectUuid)
                .withHourLimit(hourLimit)
                .withPromptForDescription(promptForDescription)
                .withRoundingStrategy(strategy);
        task.withStartDate(startDateOpt.orNull());
        task.withInclusiveEndDate(endDateInclusiveOpt.orNull());
        task.execute();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleProjectConfigured(ProjectConfiguredEvent event) {
        if (event.getProjectUuid().equals(projectUuid)) {
            Project project = getProjectFromDB();
            TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
            setSummaries(project, trackingConfiguration);
        }
    }
}