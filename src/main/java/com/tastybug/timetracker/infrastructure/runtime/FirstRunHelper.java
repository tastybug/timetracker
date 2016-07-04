package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Context;
import android.preference.PreferenceManager;

public class FirstRunHelper {

    private static final String FIRST_RUN_KEY = "FIRST_RUN_KEY";

    private Context context;

    public FirstRunHelper(Context context) {
        this.context = context;
    }

    public boolean isFirstRun () {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(FIRST_RUN_KEY, true);
    }

    public void declareFirstRunConsumed () {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(FIRST_RUN_KEY, false).apply();
    }

}
