package com.tastybug.timetracker.extension.warning.expiration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tastybug.timetracker.R;

class ExpirationWarningSettings {

    private Context context;

    ExpirationWarningSettings(Context context) {
        this.context = context;
    }

    boolean isEnabled() {
        return getSharedPreferences().getBoolean(context.getString(R.string.expiration_warning_enabled_settings_key), true);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
