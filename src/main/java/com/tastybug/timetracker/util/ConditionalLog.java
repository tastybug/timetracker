package com.tastybug.timetracker.util;

import android.util.Log;

public class ConditionalLog {

    public static void logError(String tag, String message) {
        log(Log.ERROR, tag,message);
    }

    public static void logWarn(String tag, String message) {
        log(Log.WARN, tag,message);
    }

    public static void logInfo(String tag, String message) {
        log(Log.INFO, tag,message);
    }

    public static void logDebug(String tag, String message) {
        log(Log.DEBUG, tag,message);
    }

    private static void log(int level, String tag, String message) {
        if (Log.isLoggable(tag, level)) {
            Log.i(tag, message);
        }
    }
}
