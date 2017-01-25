package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;

import com.tastybug.timetracker.extension.autoclosure.controller.AutoClosureAlarmSetup;
import com.tastybug.timetracker.extension.checkoutreminder.controller.ReminderAlarmSetup;
import com.tastybug.timetracker.extension.trackingplayer.TrackingPlayerLifecycleService;
import com.tastybug.timetracker.extension.warning.WarningEventListenerService;
import com.tastybug.timetracker.extension.wifitracking.controller.WifiTriggerLifecycleService;
import com.tastybug.timetracker.infrastructure.filecache.CacheCleaner;

import net.danlew.android.joda.JodaTimeAndroid;

public class Application extends android.app.Application {

    private FirstRunHelper firstRunHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        firstRunHelper = new FirstRunHelper(this);

        initializeJoda();
        initializeDayNightThemeMode();
        cleanupCacheFolder();
        startTrackingPlayerLifecycleBackgroundService();
        startWifiTriggerLifecycleBackgroundService();
        startWarningBackgroundService();
        startAutoClosureAlarmSetup();
        startCheckoutReminderAlarmSetup();
        if (isFirstRun()) {
            declareFirstRunConsumed();
        }
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
    }

    private void startTrackingPlayerLifecycleBackgroundService() {
        startService(new Intent(this, TrackingPlayerLifecycleService.class));
    }

    private void startWifiTriggerLifecycleBackgroundService() {
        startService(new Intent(this, WifiTriggerLifecycleService.class));
    }

    private void startAutoClosureAlarmSetup() {
        new AutoClosureAlarmSetup().setAlarm(getApplicationContext());
    }

    private void startCheckoutReminderAlarmSetup() {
        new ReminderAlarmSetup().setAlarm(getApplicationContext());
    }

    private void startWarningBackgroundService() {
        startService(new Intent(this, WarningEventListenerService.class));
    }

    private void initializeDayNightThemeMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private void cleanupCacheFolder() {
        new CacheCleaner(this).cleanupCache();
    }

    private boolean isFirstRun() {
        return firstRunHelper.isFirstRun();
    }

    private void declareFirstRunConsumed() {
        firstRunHelper.declareFirstRunConsumed();
    }
}
