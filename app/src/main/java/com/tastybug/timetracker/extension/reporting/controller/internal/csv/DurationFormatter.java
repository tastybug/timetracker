package com.tastybug.timetracker.extension.reporting.controller.internal.csv;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

class DurationFormatter {

    String formatDuration(Duration duration) {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendHours()
                .appendSeparator(":")
                .printZeroIfSupported()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .printZeroIfSupported()
                .appendSeconds()
                .toFormatter();

        return formatter.print(duration.toPeriod());
    }
}
