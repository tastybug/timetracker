package com.tastybug.timetracker.ui.shared;

import android.content.Context;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.dao.TrackingConfigurationDAO;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DurationFormatter {

    public DurationFormatter() {
    }

    public static DurationFormatter a() {
        return new DurationFormatter();
    }

    public String formatEffectiveDuration(Context context, TrackingRecord trackingRecord) {
        Duration effectiveDuration = trackingRecord.toEffectiveDuration(getTrackingConfiguration(context, trackingRecord)).get();
        return formatDuration(context, effectiveDuration);
    }

    public String formatMeasuredDuration(Context context, TrackingRecord trackingRecord) {
        Duration measuredDuration = trackingRecord.toDuration().get();
        return formatDuration(context, measuredDuration);
    }

    public String formatDuration(Context context, Duration duration) {
        return getFormatter(context, duration).print(duration.toPeriod());
    }

    private TrackingConfiguration getTrackingConfiguration(Context context, TrackingRecord trackingRecord) {
        return new TrackingConfigurationDAO(context).getByProjectUuid(trackingRecord.getProjectUuid()).get();
    }

    private PeriodFormatter getFormatter(Context context, Duration duration) {
        if (duration.getStandardHours() > 0) {
            return getFormatterForHoursMinutesSeconds(context);
        } else if (duration.getStandardMinutes() > 0) {
            return getFormatterForMinutesSeconds(context);
        } else {
            return getFormatterForSeconds(context);
        }
    }

    private PeriodFormatter getFormatterForHoursMinutesSeconds(Context context) {
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

    private PeriodFormatter getFormatterForMinutesSeconds(Context context) {
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

    private PeriodFormatter getFormatterForSeconds(Context context) {
        return new PeriodFormatterBuilder()
                .printZeroIfSupported()
                .appendSeconds()
                .appendSuffix(" " + context.getString(R.string.seconds_short))
                .toFormatter();
    }
}
