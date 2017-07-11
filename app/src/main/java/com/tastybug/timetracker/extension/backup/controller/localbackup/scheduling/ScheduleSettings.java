package com.tastybug.timetracker.extension.backup.controller.localbackup.scheduling;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tastybug.timetracker.R;

class ScheduleSettings {

    private static final boolean BACKUP_ENABLED_DEFAULT = true;

    private Context context;
    private final SharedPreferences preferences;

    ScheduleSettings(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    boolean isBackupEnabled() {
        return preferences.getBoolean(
                context.getString(R.string.local_backup_enabled_key),
                BACKUP_ENABLED_DEFAULT);
    }
}
