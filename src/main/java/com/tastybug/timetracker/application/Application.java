package com.tastybug.timetracker.application;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.tastybug.timetracker.task.testdata.TestDataGenerationTask;
import com.tastybug.timetracker.util.FirstRunHelper;

import net.danlew.android.joda.JodaTimeAndroid;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initializeJoda();
        if (isFirstRun()) {
            performFirstRunSetup();
            declareFirstRunConsumed();
        }

        printWelcomeToast();
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
    }

    private boolean isFirstRun() {
        return new FirstRunHelper(this).isFirstRun();
    }

    private void declareFirstRunConsumed() {
        new FirstRunHelper(this).declareFirstRunConsumed();
    }

    private void performFirstRunSetup() {
        runGenerateTestdataTask();
    }

    private void runGenerateTestdataTask() {
        if (getVersionName(getPackageInfo()).contains("SNAPSHOT")) {
            new TestDataGenerationTask(this).execute();
        } else {
            Log.i(getClass().getSimpleName(), "Skipping test data generation,"
                    + getVersionName(getPackageInfo()).contains("SNAPSHOT")
                    + " is not a SNAPSHOT release!");
        }
    }

    private void printWelcomeToast() {
        PackageInfo packageInfo = getPackageInfo();
        Toast.makeText(this,
                getApplicationName(packageInfo) + " " + getVersionName(packageInfo),
                Toast.LENGTH_SHORT).show();
    }

    private PackageInfo getPackageInfo() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            return pInfo;
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(getClass().getSimpleName(), nnfe.getMessage());
            return null;
        }
    }

    private String getVersionName(PackageInfo packageInfo) {
        return packageInfo.versionName;
    }

    private String getApplicationName(PackageInfo packageInfo) {
        return getString(packageInfo.applicationInfo.labelRes);
    }
}
