package com.tastybug.timetracker.core.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.tastybug.timetracker.BuildConfig;
import com.tastybug.timetracker.R;

public class SettingsFragment extends PreferenceFragment {

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
    }
}
