package com.tastybug.timetracker.infrastructure.filecache;

import android.content.Context;

import com.tastybug.timetracker.util.ConditionalLog;
import com.tastybug.timetracker.util.DateProvider;

import org.joda.time.DateTime;

import java.io.File;

import static com.tastybug.timetracker.util.ConditionalLog.logInfo;

public class CacheCleaner {

    private static final String TAG = CacheCleaner.class.getSimpleName();

    private CacheDirectoryProvider cacheDirectoryProvider;
    private DateProvider dateProvider;

    public CacheCleaner(Context context) {
        this(new CacheDirectoryProvider(context), new DateProvider());
    }

    CacheCleaner(CacheDirectoryProvider cacheDirectoryProvider,
                 DateProvider dateProvider) {
        this.cacheDirectoryProvider = cacheDirectoryProvider;
        this.dateProvider = dateProvider;
    }

    public void cleanupCache() {
        logInfo(TAG, "Purging cruft from " + cacheDirectoryProvider.getCacheDirectory());
        for (File file : cacheDirectoryProvider.getCacheDirectory().listFiles()) {
            if (isPurgeable(file)) {
                if (!file.delete()) {
                    ConditionalLog.logError(TAG, "Failed to purge " + file.getName());
                } else {
                    ConditionalLog.logDebug(TAG, "Purged " + file.getName());
                }
            } else {
                ConditionalLog.logDebug(TAG, "Skipping non-purgeable " + file.getName());
            }
        }
    }

    private boolean isPurgeable(File file) {
        return new DateTime(dateProvider.getCurrentDate()).minusDays(1).isAfter(file.lastModified());
    }
}
