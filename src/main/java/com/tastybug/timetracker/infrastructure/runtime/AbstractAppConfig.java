package com.tastybug.timetracker.infrastructure.runtime;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Base class for configurations specified in AndroidManifest.xml.
 * Derived classes will access domain specific configuration keys using accessor methods
 * implemented in here.
 */
public abstract class AbstractAppConfig {

    private Context context;

    protected AbstractAppConfig(Context context) {
        this.context = context;
    }

    protected int getIntValue(String key) {
        try {
            return getMetaDataBundle(context).getInt(key);
        } catch (PackageManager.NameNotFoundException nnfe) {
            throw new RuntimeException("Metadata not found: " + key);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Metadata " + key + " is not a valid int", nfe);
        }
    }

    protected String getStringValue(String key) {
        try {
            return getMetaDataBundle(context).getString(key);
        } catch (PackageManager.NameNotFoundException nnfe) {
            throw new RuntimeException("Metadata not found: " + key);
        }
    }

    protected Bundle getMetaDataBundle(Context context) throws PackageManager.NameNotFoundException {
        ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        return ai.metaData;
    }
}
