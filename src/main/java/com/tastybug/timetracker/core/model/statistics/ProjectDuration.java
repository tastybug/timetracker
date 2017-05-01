package com.tastybug.timetracker.core.model.statistics;

import com.google.common.base.Preconditions;
import com.tastybug.timetracker.core.model.TrackingRecord;

import java.util.List;

public class ProjectDuration {

    private org.joda.time.Duration duration = new org.joda.time.Duration(0);

    public ProjectDuration(List<TrackingRecord> trackingRecords, boolean countRunning) {
        Preconditions.checkNotNull(trackingRecords);

        performCalculation(trackingRecords, countRunning);

    }

    public ProjectDuration(List<TrackingRecord> trackingRecords) {
        this(trackingRecords, true);
    }

    private void performCalculation(List<TrackingRecord> trackingRecords,
                                    boolean countRunning) {
        for (TrackingRecord trackingRecord : trackingRecords) {
            if (trackingRecord.isRunning() && !countRunning) {
                continue;
            }
            duration = duration.plus(trackingRecord.toEffectiveDuration().get());
        }
    }

    public org.joda.time.Duration getDuration() {
        return duration;
    }
}
