package com.tastybug.timetracker.model.statistics;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingConfiguration;

import org.joda.time.LocalDateTime;
import org.joda.time.Period;

import java.util.Date;

public class StatisticProjectExpiration {

    private TrackingConfiguration trackingConfiguration;
    private Date now;


    public StatisticProjectExpiration(TrackingConfiguration trackingConfiguration) {
        this(trackingConfiguration, new Date());
    }


    public StatisticProjectExpiration(TrackingConfiguration trackingConfiguration,
                                      Date now) {
        Preconditions.checkNotNull(trackingConfiguration);
        Preconditions.checkNotNull(now);

        this.trackingConfiguration = trackingConfiguration;
        this.now = now;
    }

    public Optional<Integer> getExpirationPercent() {
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

        Period fullTimeFrame = new Period(new LocalDateTime(startDate.get()), new LocalDateTime(endDate.get()));
        Period expiredTimeFrame = new Period(new LocalDateTime(startDate.get()), new LocalDateTime(now));
        return Optional.of((int)(expiredTimeFrame.toStandardHours().getHours()/(fullTimeFrame.toStandardHours().getHours()/100d)));
    }
}
