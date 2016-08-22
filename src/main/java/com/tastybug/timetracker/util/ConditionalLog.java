package com.tastybug.timetracker.util;

import android.util.Log;

public class ConditionalLog {

    public static void logError(String tag, String message) {
        log(Log.ERROR, tag,message);
    }

    public static void logError(String tag, String message, Exception e) {
        String validatedTag = getTagWithValidLength(tag);
        if (Log.isLoggable(validatedTag, Log.ERROR)) {
            Log.e(validatedTag, message, e);
        }
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
            switch(level) {
                case Log.DEBUG:
                    Log.d(validatedTag, message);
                    break;
                case Log.INFO:
                    Log.i(validatedTag, message);
                    break;
                case Log.WARN:
                    Log.w(validatedTag, message);
                    break;
                case Log.ERROR:
                    Log.e(validatedTag, message);
                    break;
                default:
                    Log.wtf(validatedTag, message);
            }
        }
    }

    private static String getTagWithValidLength(String tag) {
        return tag.length() > 23 ? tag.substring(0, 23) : tag;
    }
}
