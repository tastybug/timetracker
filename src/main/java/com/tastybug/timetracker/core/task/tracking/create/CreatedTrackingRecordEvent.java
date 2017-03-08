package com.tastybug.timetracker.core.task.tracking.create;

import com.google.common.base.MoreObjects;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class CreatedTrackingRecordEvent implements OttoEvent {

    private TrackingRecord trackingRecord;

    public CreatedTrackingRecordEvent(TrackingRecord trackingRecord) {
        this.trackingRecord = trackingRecord;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("trackingRecord", getTrackingRecord())
                .toString();
    }
}