package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;

public class CheckInEvent extends CreatedTrackingRecordEvent {

    CheckInEvent(TrackingRecord trackingRecord) {
        super(trackingRecord);
    }
}
