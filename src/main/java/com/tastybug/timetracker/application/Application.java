package com.tastybug.timetracker.application;

import android.util.Log;
import android.widget.Toast;

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
        if (isFirstRun()) {
            runGenerateTestDataTask();
            declareFirstRunConsumed();
        }

        printWelcomeToast();
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
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

    private void printWelcomeToast() {
        Toast.makeText(this,
                versionHelper.getApplicationName() + " " + versionHelper.getVersionName(),
                Toast.LENGTH_SHORT).show();
    }

}
