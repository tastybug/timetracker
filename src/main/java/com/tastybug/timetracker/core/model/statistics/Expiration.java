package com.tastybug.timetracker.core.model.statistics;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingConfiguration;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Hours;

import java.util.Date;

public class Expiration {

    private TrackingConfiguration trackingConfiguration;
    private Date now;
    private Optional<Long> remainingDays;
    private Optional<Integer> expirationPercent;


    public Expiration(TrackingConfiguration trackingConfiguration) {
        this(trackingConfiguration, new Date());
    }


    public Expiration(TrackingConfiguration trackingConfiguration,
                      Date now) {
        Preconditions.checkNotNull(trackingConfiguration);
        Preconditions.checkNotNull(now);

        this.trackingConfiguration = trackingConfiguration;
        this.now = now;
        this.remainingDays = calculateRemainingDays();
        this.expirationPercent = calculateExpirationPercent();
    }

    private Optional<Integer> calculateExpirationPercent() {
        Optional<Date> startDate = trackingConfiguration.getStart();
        Optional<Date> endDate = trackingConfiguration.getEnd();
        if (!startDate.isPresent() || !endDate.isPresent()) {
            return Optional.absent();
        }
        if (startDate.get().after(now)) {
            return Optional.of(0);
        } else if (endDate.get().before(now)) {
            return Optional.of(100);
        }

        int full = Hours.hoursBetween(new DateTime(startDate.get()), new DateTime(endDate.get())).getHours();
        int exp = Hours.hoursBetween(new DateTime(startDate.get()), new DateTime(now)).getHours();
        return Optional.of((int) (exp / (full / 100d)));
    }

    private Optional<Long> calculateRemainingDays() {
        if (!trackingConfiguration.getEnd().isPresent()) {
            return Optional.absent();
        }
        Date start = trackingConfiguration.getStart().isPresent()
                && trackingConfiguration.getStart().get().after(now) ? trackingConfiguration.getStart().get() : now;
        if (start.after(trackingConfiguration.getEnd().get())) {
            return Optional.of(0L);
        }

        Duration duration = new Duration(start.getTime(), trackingConfiguration.getEnd().get().getTime());
        return Optional.of(duration.getStandardDays());
    }

    public Optional<Long> getRemainingDays() {
        return remainingDays;
    }

    public Optional<Integer> getExpirationPercent() {
        return expirationPercent;
    }

    public boolean isExpired() {
        return expirationPercent.isPresent() && expirationPercent.get() == 100;
    }
}
