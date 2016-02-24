package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;

public class KickstoppedTrackingRecordEvent extends TrackingRecordModifiedEvent {

    public KickstoppedTrackingRecordEvent(TrackingRecord trackingRecord) {
        super(trackingRecord);
    }
}
