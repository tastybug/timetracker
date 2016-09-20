package com.tastybug.timetracker.infrastructure.filecache;


import android.content.Context;

import java.io.File;

/**
 * Encapsulates access to the application cache directory which is assigned by the OS.
 * Does some sanity check before handing it out.
 */
public class CacheDirectoryProvider {

    private Context context;

    CacheDirectoryProvider(Context context) {
        this.context = context;
    }

    File getCacheDir() {
        File cache = context.getCacheDir();
        if (cache == null || !cache.exists()) {
            throw new IllegalStateException("Application cache dir does not exist!");
        }
        return cache;
    }

    File getCacheSubdir(String name) {
        File cacheDir = getCacheDir();
        File subdir = new File(cacheDir, name);
        if (!subdir.exists()) {
            if (!subdir.mkdir()) {
                throw new IllegalStateException("Failed to created dir '" + name + "' in cache.");
            }
        } else if (subdir.isFile()) {
            throw new IllegalStateException("Cache dir '" + name + "' exists, but is a file.");
        }
        return subdir;
    }
}
