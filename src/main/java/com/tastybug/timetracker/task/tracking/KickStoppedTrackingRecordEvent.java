package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;

public class KickStoppedTrackingRecordEvent extends ModifiedTrackingRecordEvent {

    public KickStoppedTrackingRecordEvent(TrackingRecord trackingRecord) {
        super(trackingRecord);
    }
}
