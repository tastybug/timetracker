package com.tastybug.timetracker.extension.warning.completion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tastybug.timetracker.R;

class CompletionWarningSettings {

    private Context context;

    CompletionWarningSettings(Context context) {
        this.context = context;
    }

    boolean isEnabled() {
        return getSharedPreferences().getBoolean(context.getString(R.string.completion_warning_enabled_settings_key), true);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
