package com.tastybug.timetracker.extension.wifitracking.controller.checkout;

import android.content.Context;
import android.preference.PreferenceManager;

import com.tastybug.timetracker.R;

public class GracePeriodProvider {

    private Context context;

    GracePeriodProvider(Context context) {
        this.context = context;
    }

    int getGracePeriodInSeconds() {
        String defaultGracePeriod = context.getResources().getInteger(R.integer.wifi_tracking_grace_period_in_minutes_default) + "";
        String periodsMinutes = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.wifi_tracking_grace_period_in_minutes_key), defaultGracePeriod);
        return Integer.parseInt(periodsMinutes) * 60;
    }
}
