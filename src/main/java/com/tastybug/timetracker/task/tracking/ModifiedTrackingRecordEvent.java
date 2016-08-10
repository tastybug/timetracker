package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.TrackingRecord;

public class ModifiedTrackingRecordEvent implements OttoEvent {

    private TrackingRecord trackingRecord;
    private boolean wasStopped = false;

    public ModifiedTrackingRecordEvent(TrackingRecord trackingRecord, boolean wasStopped) {
        this.trackingRecord = trackingRecord;
        this.wasStopped = wasStopped;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }

    public boolean wasStopped() {
        return wasStopped;
    }
}