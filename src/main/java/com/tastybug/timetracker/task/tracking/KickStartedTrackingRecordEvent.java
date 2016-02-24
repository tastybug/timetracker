package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;

public class KickStartedTrackingRecordEvent extends CreatedTrackingRecordEvent {

    public KickStartedTrackingRecordEvent(TrackingRecord trackingRecord) {
        super(trackingRecord);
    }
}
