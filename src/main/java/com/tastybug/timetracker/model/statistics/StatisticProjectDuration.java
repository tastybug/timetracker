package com.tastybug.timetracker.model.statistics;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.rounding.RoundingStrategy;

import org.joda.time.Duration;

import java.util.ArrayList;

public class StatisticProjectDuration {

    private Duration duration = new Duration(0);

    public StatisticProjectDuration(TrackingConfiguration configuration, ArrayList<TimeFrame> timeFrames) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(timeFrames);

        RoundingStrategy strategy = configuration.getRoundingStrategy().getStrategy();
        for (TimeFrame timeFrame : timeFrames) {
            if (timeFrame.isRunning()) {
                continue;
            }
            duration = duration.plus(strategy.getEffectiveDurationInSeconds(timeFrame.toDuration().get()) * 1000);
        }
    }

    public Duration get() {
        return duration;
    }
}
