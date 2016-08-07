package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;

import com.tastybug.timetracker.infrastructure.backup.DataChangeListenerBackgroundService;
import com.tastybug.timetracker.ui.trackingplayer.LifecycleService;

import net.danlew.android.joda.JodaTimeAndroid;

public class Application extends android.app.Application {

    private FirstRunHelper firstRunHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        firstRunHelper = new FirstRunHelper(this);

        initializeJoda();
        initializeDayNightThemeMode();
        startTrackingPlayerLifecycleBackgroundService();
        startBackupDataChangeListenerBackgroundService();
        if (isFirstRun()) {
            declareFirstRunConsumed();
        }
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
    }

    private void startTrackingPlayerLifecycleBackgroundService() {
        startService(new Intent(this, LifecycleService.class));
    }

    private void startBackupDataChangeListenerBackgroundService() {
        startService(new Intent(this, DataChangeListenerBackgroundService.class));
    }

    private void initializeDayNightThemeMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private boolean isFirstRun() {
        return firstRunHelper.isFirstRun();
    }

    private void declareFirstRunConsumed() {
        firstRunHelper.declareFirstRunConsumed();
    }
}
