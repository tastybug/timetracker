package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;

public class CheckOutEvent extends ModifiedTrackingRecordEvent {

    public CheckOutEvent(TrackingRecord trackingRecord) {
        super(trackingRecord, true);
    }
}
