package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class VersionHelper {

    PackageInfo packageInfo;
    Context context;

    public VersionHelper(Context context) {
        this.context = context;
        PackageManager packageManager = context.getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(getClass().getSimpleName(), nnfe.getMessage());
            throw new RuntimeException("Cannot lookup package info for this application.", nnfe);
        }
    }

    public String getVersionName() {
        return packageInfo.versionName;
    }

    public boolean isDevelopmentVersion() {
        return getVersionName().contains("SNAPSHOT");
    }

    public String getApplicationName() {
        return context.getString(packageInfo.applicationInfo.labelRes);
    }

}
