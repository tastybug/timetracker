package com.tastybug.timetracker.core.model.rounding;

import org.joda.time.Duration;

class FullMinuteDown extends RoundingStrategy {

    @Override
    long getDurationInSeconds(Duration duration) {
        return duration.getStandardMinutes() * 60; // this rounds down, see javadoc
    }
}
