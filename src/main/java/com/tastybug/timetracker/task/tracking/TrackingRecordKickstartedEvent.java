package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.model.TrackingRecord;

public class TrackingRecordKickstartedEvent extends TrackingRecordCreatedEvent {

    public TrackingRecordKickstartedEvent(TrackingRecord trackingRecord) {
        super(trackingRecord);
    }
}
