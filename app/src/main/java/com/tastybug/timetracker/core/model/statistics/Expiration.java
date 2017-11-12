package com.tastybug.timetracker.core.model.statistics;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingConfiguration;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Hours;

import java.util.Calendar;
import java.util.Date;

public class Expiration {

    private TrackingConfiguration trackingConfiguration;
    private Date now;
    private Optional<Long> remainingDays;
    private Optional<Integer> remainingWorkDays;
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
        this.remainingWorkDays = calculateRemainingWorkDays();
        this.expirationPercent = calculateExpirationPercent();
    }

    private Optional<Integer> calculateExpirationPercent() {
        if (!trackingConfiguration.hasCompleteTimeFrame()) {
            return Optional.absent();
        }

        Date startDate = trackingConfiguration.getStart().get();
        Date endDate = trackingConfiguration.getEnd().get();
        if (startDate.after(now)) {
            return Optional.of(0);
        } else if (endDate.before(now)) {
            return Optional.of(100);
        }

        int timeFrameFullHours = Hours.hoursBetween(new DateTime(startDate), new DateTime(endDate)).getHours();
        int expiredTimeFrameHours = Hours.hoursBetween(new DateTime(startDate), new DateTime(now)).getHours();
        return Optional.of((int) (expiredTimeFrameHours / (timeFrameFullHours / 100d)));
    }

    private Optional<Long> calculateRemainingDays() {
        if (!trackingConfiguration.getEnd().isPresent()) {
            return Optional.absent();
        }
        Date start = getRemainderCalculationStartDate();
        if (now.after(trackingConfiguration.getEnd().get())) {
            return Optional.of(0L);
        }
        Duration duration = new Duration(start.getTime(), trackingConfiguration.getEnd().get().getTime());
        return Optional.of(duration.getStandardDays());
    }

    private Optional<Integer> calculateRemainingWorkDays() {
        if (!trackingConfiguration.getEnd().isPresent()) {
            return Optional.absent();
        }
        Date start = getRemainderCalculationStartDate();
        Date timeFrameEnd = trackingConfiguration.getEnd().get();

        Date aDayWithinTimeFrame = start;
        int weekdayCounter = 0;
        while (aDayWithinTimeFrame.before(timeFrameEnd)) {
            if (!isWeekend(aDayWithinTimeFrame)) {
                weekdayCounter++;
            }
            aDayWithinTimeFrame = new DateTime(aDayWithinTimeFrame).plusDays(1).toDate();
        }
        return Optional.of(weekdayCounter);
    }

    private boolean isWeekend(Date date) {
        // for some reason joda`s DateTime returns the weekday for ZULU time, not the given timezone
        // so we have to fall back to clumsy Calendars here
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private Date getRemainderCalculationStartDate() {
        return trackingConfiguration.getStart().isPresent()
                && trackingConfiguration.getStart().get().after(now) ? trackingConfiguration.getStart().get() : now;
    }


    public Optional<Long> getRemainingDays() {
        return remainingDays;
    }

    public Optional<Integer> getExpirationPercent() {
        return expirationPercent;
    }

    public Optional<Integer> getRemainingWorkDays() {
        return remainingWorkDays;
    }

    public boolean isExpired() {
        return expirationPercent.isPresent() && expirationPercent.get() == 100;
    }
}
