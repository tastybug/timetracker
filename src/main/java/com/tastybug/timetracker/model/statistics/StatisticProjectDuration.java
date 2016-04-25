package com.tastybug.timetracker.model.statistics;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.model.rounding.RoundingStrategy;

import org.joda.time.Duration;

import java.util.ArrayList;

public class StatisticProjectDuration {

    private Duration duration = new Duration(0);

    public StatisticProjectDuration(TrackingConfiguration configuration, ArrayList<TrackingRecord> trackingRecords, boolean countRunning) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(trackingRecords);

        performCalculation(configuration, trackingRecords, countRunning);

    }

    public StatisticProjectDuration(TrackingConfiguration configuration, ArrayList<TrackingRecord> trackingRecords) {
        this(configuration, trackingRecords, true);
    }

    private void performCalculation(TrackingConfiguration configuration,
                                    ArrayList<TrackingRecord> trackingRecords,
                                    boolean countRunning) {
        for (TrackingRecord trackingRecord : trackingRecords) {
            if (trackingRecord.isRunning() && !countRunning) {
                continue;
            }
            duration = duration.plus(trackingRecord.toEffectiveDuration(configuration).get());
        }
    }

    public Duration get() {
        return duration;
    }
}
