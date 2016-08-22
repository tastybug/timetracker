package com.tastybug.timetracker.model.statistics;

import com.google.common.base.Optional;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

import org.joda.time.Duration;

import java.util.ArrayList;

public class StatisticProjectCompletion {

    private TrackingConfiguration trackingConfiguration;
    private Duration currentDuration;

    public StatisticProjectCompletion(TrackingConfiguration configuration,
                                      ArrayList<TrackingRecord> trackingRecords,
                                      boolean countRunning) {
        this.currentDuration = new StatisticProjectDuration(configuration, trackingRecords, countRunning).getDuration();
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
                && Duration.standardHours(trackingConfiguration.getHourLimit().get()).isShorterThan(currentDuration);
    }

    public Optional<Duration> getRemainingDuration() {
        Optional<Duration> remainderOpt;
        if (trackingConfiguration.getHourLimit().isPresent()) {
            if (isOverbooked()) {
                // overbooked project
                remainderOpt = Optional.of(Duration.millis(0));
            } else {
                remainderOpt = Optional.of(Duration.standardHours(trackingConfiguration.getHourLimit().get()).minus(currentDuration));
            }
        } else {
            remainderOpt = Optional.absent();
        }
        return remainderOpt;
    }
}
