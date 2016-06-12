package com.tastybug.timetracker.gui.fragment.project.configuration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.gui.activity.NGProjectConfigurationActivity;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingFactory;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;
import com.tastybug.timetracker.task.project.ProjectConfiguredEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NGTrackingConfigurationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String projectUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.projectUuid = savedInstanceState.getString(NGProjectConfigurationActivity.PROJECT_UUID);
        } else {
            this.projectUuid = getActivity().getIntent().getExtras().getString(NGProjectConfigurationActivity.PROJECT_UUID);
        }

        TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
        initPreferencesWithDataFromEntity(trackingConfiguration, PreferenceManager.getDefaultSharedPreferences(getActivity()));
        addPreferencesFromResource(R.xml.tracking_configuration_preferences);
        setSummariesAtPreferences(trackingConfiguration);
    }

    private TrackingConfiguration getTrackingConfigurationFromDB() {
        return new TrackingConfigurationDAO(getActivity()).getByProjectUuid(projectUuid).get();
    }

    private void initPreferencesWithDataFromEntity(TrackingConfiguration trackingConfiguration, SharedPreferences sharedPref) {
        sharedPref.edit()
                .putInt(getString(R.string.tracking_configuration_hour_limit_preference_key),
                        trackingConfiguration.getHourLimit().or(0))
                .putLong(getString(R.string.tracking_configuration_start_date_preference_key),
                        trackingConfiguration.getStart().isPresent() ? trackingConfiguration.getStart().get().getTime() : -1L)
                .putLong(getString(R.string.tracking_configuration_end_date_inclusive_preference_key),
                        trackingConfiguration.getEnd().isPresent() ? trackingConfiguration.getEndDateAsInclusive().get().getTime() : -1L)
                .putString(getString(R.string.tracking_configuration_rounding_strategy_preference_key),
                        trackingConfiguration.getRoundingStrategy().name())
                .apply();
    }

    private void setSummariesAtPreferences(TrackingConfiguration trackingConfiguration) {
        if (trackingConfiguration.getHourLimit().isPresent()) {
            findPreference(getString(R.string.tracking_configuration_hour_limit_preference_key)).setSummary(getString(R.string.hour_limit_of_X_hours, trackingConfiguration.getHourLimit().get()));
        } else {
            findPreference(getString(R.string.tracking_configuration_hour_limit_preference_key)).setSummary(R.string.tracking_configuration_hour_limit_preference_summary_none);
        }
        if (trackingConfiguration.getStart().isPresent()) {
            findPreference(getString(R.string.tracking_configuration_start_date_preference_key))
                    .setSummary(getString(R.string.starts_at_X, SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(trackingConfiguration.getStart().get())));
        } else {
            findPreference(getString(R.string.tracking_configuration_start_date_preference_key)).setSummary(R.string.tracking_configuration_start_date_preference_summary_none);
        }
        if (trackingConfiguration.getEnd().isPresent()) {
            findPreference(getString(R.string.tracking_configuration_end_date_inclusive_preference_key))
                    .setSummary(getString(R.string.ends_at_inclusive_X, SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(trackingConfiguration.getEndDateAsInclusive().get())));
        } else {
            findPreference(getString(R.string.tracking_configuration_end_date_inclusive_preference_key)).setSummary(R.string.tracking_configuration_end_date_inclusive_preference_summary_none);
        }
        findPreference(getString(R.string.tracking_configuration_rounding_strategy_preference_key))
                .setSummary(trackingConfiguration.getRoundingStrategy().getDescriptionStringResource());
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
        TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
        Integer hourLimit = sharedPreferences.getInt(getString(R.string.tracking_configuration_hour_limit_preference_key), 0);
        Long startTimeStamp = sharedPreferences.getLong(getString(R.string.tracking_configuration_start_date_preference_key), -1L);
        Optional<Date> startDateOpt = startTimeStamp == -1L ? Optional.<Date>absent() : Optional.of(new Date(startTimeStamp));
        Long endTimeStamp = sharedPreferences.getLong(getString(R.string.tracking_configuration_end_date_inclusive_preference_key), -1L);
        Optional<Date> endDateInclusiveOpt = endTimeStamp == -1L ? Optional.<Date>absent() : Optional.of(new Date(endTimeStamp));
        RoundingFactory.Strategy strategy = RoundingFactory.Strategy.valueOf(sharedPreferences.getString(getString(R.string.tracking_configuration_rounding_strategy_preference_key), RoundingFactory.Strategy.NO_ROUNDING.name()));

        if (key.equals(getString(R.string.tracking_configuration_hour_limit_preference_key))
                && !isHourLimitValid(hourLimit)) {
            revertChanges(trackingConfiguration, sharedPreferences);
            showWarningInvalidHourLimit();
        } else if (key.equals(getString(R.string.tracking_configuration_start_date_preference_key))
                    && !isStartDateValid(startDateOpt)) {
            revertChanges(trackingConfiguration, sharedPreferences);
            showWarningInvalidStartDateLimit();
        } else if (key.equals(getString(R.string.tracking_configuration_end_date_inclusive_preference_key))
                && !isEndDateValid(endDateInclusiveOpt)) {
            revertChanges(trackingConfiguration, sharedPreferences);
            showWarningInvalidEndDateLimit();
        } else {
            saveChanges(hourLimit, startDateOpt, endDateInclusiveOpt, strategy);
        }
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

    private void revertChanges(TrackingConfiguration configuration, SharedPreferences sharedPreferences) {
        initPreferencesWithDataFromEntity(configuration, sharedPreferences);
    }

    private void saveChanges(Integer hourLimit, Optional<Date> startDateOpt, Optional<Date> endDateInclusiveOpt, RoundingFactory.Strategy strategy) {
        ConfigureProjectTask task = ConfigureProjectTask.aTask(getActivity())
                .withProjectUuid(projectUuid)
                .withHourLimit(hourLimit)
                .withRoundingStrategy(strategy);
        task.withStartDate(startDateOpt.orNull());
        task.withInclusiveEndDate(endDateInclusiveOpt.orNull());
        task.execute();
    }

    @Subscribe
    public void handleProjectConfigured(ProjectConfiguredEvent event) {
        if (event.getProjectUuid().equals(projectUuid)) {
            TrackingConfiguration trackingConfiguration = getTrackingConfigurationFromDB();
            setSummariesAtPreferences(trackingConfiguration);
        }
    }
}