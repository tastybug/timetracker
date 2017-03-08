package com.tastybug.timetracker.extension.checkoutreminder.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tastybug.timetracker.R;

class ReminderSettings {

    private Context context;

    ReminderSettings(Context context) {
        this.context = context;
    }

    boolean isEnabled() {
        return getSharedPreferences().getBoolean(context.getString(R.string.checkout_reminder_enabled_settings_key), true);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

}
