package com.tastybug.timetracker.util;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DurationFormatterFactory {

    public static PeriodFormatter getFormatter(Duration duration) {
        if (duration.getStandardHours() > 0) {
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
                    .appendSuffix(" hours", " hours")
                    .toFormatter();
        } else if (duration.getStandardMinutes() > 0) {
            return new PeriodFormatterBuilder()
                    .printZeroIfSupported()
                    .appendMinutes()
                    .appendSeparator(":")
                    .minimumPrintedDigits(2)
                    .printZeroIfSupported()
                    .appendSeconds()
                    .appendSuffix(" mins", " mins")
                    .toFormatter();
        } else {
            return new PeriodFormatterBuilder()
                    .printZeroIfSupported()
                    .appendSeconds()
                    .appendSuffix(" secs", " secs")
                    .toFormatter();
        }
    }
}
