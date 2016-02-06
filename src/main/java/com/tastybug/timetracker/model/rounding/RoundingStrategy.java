package com.tastybug.timetracker.model.rounding;

import com.google.common.base.Preconditions;

import org.joda.time.Duration;

public abstract class RoundingStrategy {

    protected abstract long getDurationInSeconds(Duration duration);

    public long getEffectiveDurationInSeconds(Duration duration) {
        Preconditions.checkNotNull(duration);
        return getDurationInSeconds(duration);
    }
}
