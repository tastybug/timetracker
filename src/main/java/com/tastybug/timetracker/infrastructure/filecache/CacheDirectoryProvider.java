package com.tastybug.timetracker.infrastructure.filecache;


import android.content.Context;

import java.io.File;

/**
 * Encapsulates access to the application cache directory which is assigned by the OS.
 * Does some sanity check before handing it out.
 */
class CacheDirectoryProvider {

    private static final String APP_CACHE_FOLDER = "appCacheFolder";
    private Context context;

    CacheDirectoryProvider(Context context) {
        this.context = context;
    }

    File getCacheDirectory() {
        File rootCacheDirectory = getRootCacheDirectory();
        File appCacheFolder = new File(rootCacheDirectory, APP_CACHE_FOLDER);
        if (!appCacheFolder.exists()) {
            if (!appCacheFolder.mkdir()) {
                throw new IllegalStateException("Failed to create cache folder!");
            }
        }
        return appCacheFolder;
    }

    private File getRootCacheDirectory() {
        File cache = context.getCacheDir();
        if (cache == null || !cache.exists()) {
            throw new IllegalStateException("Application cache dir does not exist!");
        }
        return cache;
    }
}
