package com.tastybug.timetracker.model.statistics;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.TrackingRecord;

import java.util.ArrayList;
import java.util.List;

public class Duration {

    private org.joda.time.Duration duration = new org.joda.time.Duration(0);

    public Duration(TrackingConfiguration configuration, List<TrackingRecord> trackingRecords, boolean countRunning) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(trackingRecords);

        performCalculation(configuration, trackingRecords, countRunning);

    }

    public Duration(TrackingConfiguration configuration, ArrayList<TrackingRecord> trackingRecords) {
        this(configuration, trackingRecords, true);
    }

    private void performCalculation(TrackingConfiguration configuration,
                                    List<TrackingRecord> trackingRecords,
                                    boolean countRunning) {
        for (TrackingRecord trackingRecord : trackingRecords) {
            if (trackingRecord.isRunning() && !countRunning) {
                continue;
            }
            duration = duration.plus(trackingRecord.toEffectiveDuration(configuration).get());
        }
    }

    public org.joda.time.Duration getDuration() {
        return duration;
    }
}
