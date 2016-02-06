package com.tastybug.timetracker.model.rounding;

import org.joda.time.Duration;

public class FullMinuteUp extends RoundingStrategy {

    @Override
    protected long getDurationInSeconds(Duration duration) {
        long minutes = duration.getStandardMinutes();
        return (duration.getStandardSeconds() % 60 > 0 ? minutes+1 : minutes) * 60;
    }
}
