package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoEvent;

public class TrackingRecordModifiedEvent implements OttoEvent {

    private TrackingRecord trackingRecord;

    public TrackingRecordModifiedEvent(TrackingRecord trackingRecord) {
        this.trackingRecord = trackingRecord;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }
}