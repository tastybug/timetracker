package com.tastybug.timetracker.core.model.statistics;

import com.google.common.base.Optional;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;

import java.util.List;

public class Completion {

    private TrackingConfiguration trackingConfiguration;
    private org.joda.time.Duration currentDuration;

    public Completion(TrackingConfiguration configuration,
                      List<TrackingRecord> trackingRecords,
                      boolean countRunning) {
        this.currentDuration = new ProjectDuration(trackingRecords, countRunning).getDuration();
        this.trackingConfiguration = configuration;
    }

    public Optional<Double> getCompletionPercent() {
        if (trackingConfiguration.getHourLimit().isPresent()) {
            double completionPercent = currentDuration.getStandardSeconds() / (trackingConfiguration.getHourLimit().get() * 60 * 60 / 100);
            return Optional.of(completionPercent);
        } else {
            return Optional.absent();
        }
    }

    public boolean isOverbooked() {
        return trackingConfiguration.getHourLimit().isPresent()
                && org.joda.time.Duration.standardHours(trackingConfiguration.getHourLimit().get()).isShorterThan(currentDuration);
    }

    Optional<org.joda.time.Duration> getRemainingDuration() {
        Optional<org.joda.time.Duration> remainderOpt;
        if (trackingConfiguration.getHourLimit().isPresent()) {
            if (isOverbooked()) {
                // overbooked project
                remainderOpt = Optional.of(org.joda.time.Duration.millis(0));
            } else {
                remainderOpt = Optional.of(org.joda.time.Duration.standardHours(trackingConfiguration.getHourLimit().get()).minus(currentDuration));
            }
        } else {
            remainderOpt = Optional.absent();
        }
        return remainderOpt;
    }
}
