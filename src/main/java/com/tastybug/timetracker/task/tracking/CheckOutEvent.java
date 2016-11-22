package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;

public class CheckOutEvent extends ModifiedTrackingRecordEvent {

    CheckOutEvent(TrackingRecord trackingRecord) {
        super(trackingRecord, true);
    }
}
