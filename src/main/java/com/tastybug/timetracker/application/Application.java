package com.tastybug.timetracker.application;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initializeJoda();

        printWelcomeToast();
    }

    private void initializeJoda() {
        JodaTimeAndroid.init(this);
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
