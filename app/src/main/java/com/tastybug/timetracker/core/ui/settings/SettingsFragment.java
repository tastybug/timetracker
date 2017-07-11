package com.tastybug.timetracker.core.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.tastybug.timetracker.BuildConfig;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        findPreference(getString(R.string.app_version_info))
                .setTitle(getString(R.string.application_W_version_X_githash_Y_buildtype_Z,
                        getString(R.string.app_name),
                        BuildConfig.VERSION_NAME,
                        BuildConfig.GIT_HASH,
                        BuildConfig.BUILD_TYPE));
        setSummaries();
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
        setSummaries();
    }

    private void setSummaries() {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        setWifiTrackingGracePeriodSummary(sharedPreferences);
    }

    private void setWifiTrackingGracePeriodSummary(SharedPreferences sharedPreferences) {
        String defaultGracePeriod = getResources().getInteger(R.integer.wifi_tracking_grace_period_in_minutes_default) + "";
        String settingsKey = getString(R.string.wifi_tracking_grace_period_in_minutes_key);
        findPreference(settingsKey).setSummary(getString(R.string.wifi_tracking_grace_period_X_minutes_summary,
                sharedPreferences.getString(settingsKey, defaultGracePeriod)));
    }

}
