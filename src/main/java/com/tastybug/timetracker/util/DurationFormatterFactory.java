package com.tastybug.timetracker.util;

import android.content.Context;

import com.tastybug.timetracker.R;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DurationFormatterFactory {

    public static PeriodFormatter getFormatter(Context context, Duration duration) {
        if (duration.getStandardHours() > 0) {
            return getFormatterForHoursMinutesSeconds(context);
        } else if (duration.getStandardMinutes() > 0) {
            return getFormatterForMinutesSeconds(context);
        } else {
            return getFormatterForSeconds(context);
        }
    }

    private static PeriodFormatter getFormatterForHoursMinutesSeconds(Context context) {
            return new PeriodFormatterBuilder()
                    .printZeroIfSupported()
                    .appendHours()
                    .appendSeparator(":")
                    .printZeroIfSupported()
                    .minimumPrintedDigits(2)
                    .appendMinutes()
                    .appendSeparator(":")
                    .printZeroIfSupported()
                    .appendSeconds()
                    .appendSuffix(" " + context.getString(R.string.hours_short))
                    .toFormatter();
    }

    private static PeriodFormatter getFormatterForMinutesSeconds(Context context) {
            return new PeriodFormatterBuilder()
                    .printZeroIfSupported()
                    .appendMinutes()
                    .appendSeparator(":")
                    .minimumPrintedDigits(2)
                    .printZeroIfSupported()
                    .appendSeconds()
                    .appendSuffix(" " + context.getString(R.string.minutes_short))
                    .toFormatter();

    }

    private static PeriodFormatter getFormatterForSeconds(Context context) {
            return new PeriodFormatterBuilder()
                    .printZeroIfSupported()
                    .appendSeconds()
                    .appendSuffix(" " + context.getString(R.string.seconds_short))
                    .toFormatter();
    }
}
