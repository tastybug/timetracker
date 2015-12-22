package com.tastybug.timetracker.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.slf4j.Logger;

public class VersionUtil {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

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
            logger.error("VersionUtil", nnfe.getMessage());
            return null;
        }
    }

    public String getVersionName() {
        return getPackageInfo().versionName;
    }
}
