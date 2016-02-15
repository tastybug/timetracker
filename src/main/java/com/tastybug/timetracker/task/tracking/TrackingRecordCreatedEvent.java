package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoEvent;

public class TrackingRecordCreatedEvent implements OttoEvent {

    private TrackingRecord trackingRecord;

    public TrackingRecordCreatedEvent(TrackingRecord trackingRecord) {
        this.trackingRecord = trackingRecord;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }
}