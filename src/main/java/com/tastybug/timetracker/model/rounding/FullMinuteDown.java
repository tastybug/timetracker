package com.tastybug.timetracker.model.rounding;

import org.joda.time.Duration;

public class FullMinuteDown extends RoundingStrategy {

    @Override
    protected long getDurationInSeconds(Duration duration) {
        return duration.getStandardMinutes() * 60; // this rounds down, see javadoc
    }
}
