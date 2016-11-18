package com.tastybug.timetracker.infrastructure.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tastybug.timetracker.R;

class BackupEnabledService {

    private final String BACKUP_PREF_KEY;
    private SharedPreferences sharedPreferences;

    BackupEnabledService(Context context) {
        BACKUP_PREF_KEY = context.getString(R.string.backup_enabled_preference_key);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    boolean isBackupFacilityEnabled() {
        return sharedPreferences.getBoolean(BACKUP_PREF_KEY, false);
    }

}
