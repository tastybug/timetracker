package com.tastybug.timetracker.infrastructure.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DefaultLocaleDateFormatter {

    public DefaultLocaleDateFormatter() {
    }

    public static DateFormat iso8601() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    }

    public static DateFormat dayOfWeek() {
        return new SimpleDateFormat("EEE", Locale.getDefault());
    }

    public static DateFormat date() {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
    }

    public static DateFormat dateLong() {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);
    }

    public static DateFormat time() {
        return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
    }

    public static DateFormat dateTime() {
        return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
    }

    public String dateFormat(Date date) {
        return date().format(date);
    }

    public String timeFormat(Date date) {
        return time().format(date);
    }

    public String dateTimeFormat(Date date) {
        return dateTime().format(date);
    }

}
