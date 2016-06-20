package com.tastybug.timetracker.application;

import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.background.NotificationLifecycleBackgroundService;
import com.tastybug.timetracker.task.testdata.TestDataGenerationTask;
import com.tastybug.timetracker.util.FirstRunHelper;
import com.tastybug.timetracker.util.VersionHelper;

import net.danlew.android.joda.JodaTimeAndroid;

public class Application extends android.app.Application {

    private FirstRunHelper firstRunHelper;
    private VersionHelper versionHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        firstRunHelper = new FirstRunHelper(this);
        versionHelper = new VersionHelper(this);

        initializeJoda();
        initializeDayNightThemeMode();
        startNotificationManagerBackgroundService();
        if (isFirstRun()) {
            runGenerateTestDataTask();
            declareFirstRunConsumed();
        }

        showWelcomeToast();
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
    }

    private void startNotificationManagerBackgroundService() {
        startService(new Intent(this, NotificationLifecycleBackgroundService.class));
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

    private void runGenerateTestDataTask() {
        if (versionHelper.isDevelopmentVersion()) {
            new TestDataGenerationTask(this).execute();
        } else {
            Log.i(getClass().getSimpleName(), "Skipping test data generation,"
                    + versionHelper.getVersionName()
                    + " is not a SNAPSHOT release!");
        }
    }

    private void showWelcomeToast() {
        Toast.makeText(this,
                getString(R.string.app_start_welcome_app_name_X_at_version_Y,
                        versionHelper.getApplicationName(),
                        versionHelper.getVersionName()),
                Toast.LENGTH_SHORT).show();
    }
}
