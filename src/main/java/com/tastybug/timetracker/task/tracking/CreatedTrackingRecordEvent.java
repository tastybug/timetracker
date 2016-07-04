package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;
import com.tastybug.timetracker.model.TrackingRecord;

public class CreatedTrackingRecordEvent implements OttoEvent {

    private TrackingRecord trackingRecord;

    public CreatedTrackingRecordEvent(TrackingRecord trackingRecord) {
        this.trackingRecord = trackingRecord;
    }

    public TrackingRecord getTrackingRecord() {
        return trackingRecord;
    }
}