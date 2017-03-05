package com.tastybug.timetracker.extensions.reporting.ui;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.util.DateProvider;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class DefaultReportTimeFrameProvider {

    Optional<TrackingConfiguration> trackingConfigurationOptional = Optional.absent();
    private DateProvider dateProvider = new DateProvider();

    DefaultReportTimeFrameProvider() {
    }

    DefaultReportTimeFrameProvider(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    public DefaultReportTimeFrameProvider forTrackingConfiguration(TrackingConfiguration trackingConfiguration) {
        this.trackingConfigurationOptional = Optional.fromNullable(trackingConfiguration);
        return this;
    }

    public Interval getTimeFrame() {
        if (hasFramedTrackingConfiguration()) {
            return getIntervalForTrackingConfiguration(trackingConfigurationOptional.get());
        }
        return getIntervalForCurrentMonth();
    }

    private boolean hasFramedTrackingConfiguration() {
        return trackingConfigurationOptional.isPresent()
                && trackingConfigurationOptional.get().getStart().isPresent()
                && trackingConfigurationOptional.get().getEnd().isPresent();
    }

    private Interval getIntervalForCurrentMonth() {
        DateTime today = new DateTime(dateProvider.getCurrentDate())
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
        DateTime monthStart = today.withDayOfMonth(1);
        DateTime monthEnd = monthStart.plusMonths(1).minusDays(1);

        return new Interval(monthStart, monthEnd);
    }

    private Interval getIntervalForTrackingConfiguration(TrackingConfiguration trackingConfiguration) {
        return new Interval(new DateTime(trackingConfiguration.getStart().get()),
                new DateTime(trackingConfiguration.getEnd().get()));
    }
}
