package com.tastybug.timetracker.core.ui.util;

import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DurationFormatter {

    private String hoursAbbreviation,
            minutesAbbreviation,
            secondsAbbreviation;

    DurationFormatter() {
        this("h", "m", "s");
    }

    DurationFormatter(String hoursAbbreviation, String minutesAbbreviation, String secondsAbbreviation) {
        this.hoursAbbreviation = hoursAbbreviation;
        this.minutesAbbreviation = minutesAbbreviation;
        this.secondsAbbreviation = secondsAbbreviation;
    }

    public static DurationFormatter a() {
        return new DurationFormatter();
    }

    public String formatEffectiveDuration(TrackingConfiguration trackingConfiguration, TrackingRecord trackingRecord) {
        Duration effectiveDuration = trackingRecord.toEffectiveDuration(trackingConfiguration).get();
        return formatDuration(effectiveDuration);
    }

    public String formatMeasuredDuration(TrackingRecord trackingRecord) {
        Duration measuredDuration = trackingRecord.toDuration().get();
        return formatDuration(measuredDuration);
    }

    public String formatDuration(Duration duration) {
        return getFormatter(duration).print(duration.toPeriod());
    }

    private PeriodFormatter getFormatter(Duration duration) {
        if (duration.getStandardHours() > 0) {
            return getFormatterForHoursMinutesSeconds();
        } else if (duration.getStandardMinutes() > 0) {
            return getFormatterForMinutesSeconds();
        } else {
            return getFormatterForSeconds();
        }
    }

    private PeriodFormatter getFormatterForHoursMinutesSeconds() {
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
                .appendSuffix(hoursAbbreviation)
                .toFormatter();
    }

    private PeriodFormatter getFormatterForMinutesSeconds() {
        return new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendMinutes()
                .appendSeparator(":")
                .minimumPrintedDigits(2)
                .printZeroIfSupported()
                .appendSeconds()
                .appendSuffix(minutesAbbreviation)
                .toFormatter();

    }

    protected PeriodFormatter getFormatterForSeconds() {
        return new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendSeconds()
                .appendSuffix(secondsAbbreviation)
                .toFormatter();
    }
}
