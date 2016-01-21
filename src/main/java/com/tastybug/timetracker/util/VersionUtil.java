package com.tastybug.timetracker.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class VersionUtil {

    private static final String TAG = VersionUtil.class.getSimpleName();

    private Context context;

    public VersionUtil(Context context) {
        this.context = context;
    }

    public PackageInfo getPackageInfo() {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            return pInfo;
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(TAG, nnfe.getMessage());
            return null;
        }
    }

    public String getVersionName() {
        return getPackageInfo().versionName;
    }
}
