package com.tastybug.timetracker.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formatter {

    public static DateFormat iso8601() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }

    public static DateFormat date() {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
    }

    public static DateFormat time() {
        return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
    }

    public static DateFormat dateTime() {
        return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
    }

}
