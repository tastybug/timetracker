package com.tastybug.timetracker.task.tracking.update;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.TrackingRecord;

public class UpdateTrackingRecordEvent implements OttoEvent {

    private TrackingRecord trackingRecord;
    private boolean wasStopped = false;

    public UpdateTrackingRecordEvent(TrackingRecord trackingRecord, boolean wasStopped) {
        this.trackingRecord = trackingRecord;
        this.wasStopped = wasStopped;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }

    public boolean wasStopped() {
        return wasStopped;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .add("wasStopped", wasStopped())
                .toString();
    }
}