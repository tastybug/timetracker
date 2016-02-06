package com.tastybug.timetracker.model.rounding;

import org.joda.time.Duration;

public class NoRounding extends RoundingStrategy {

    @Override
    long getDurationInSeconds(Duration duration) {
        return duration.getStandardSeconds();
    }
}
