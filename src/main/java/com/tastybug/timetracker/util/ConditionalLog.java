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
        String validatedTag = getTagWithValidLength(tag);
        if (Log.isLoggable(validatedTag, level)) {
            Log.i(validatedTag, message);
        }
    }

    private static String getTagWithValidLength(String tag) {
        return tag.length() > 23 ? tag.substring(0, 23) : tag;
    }
}
