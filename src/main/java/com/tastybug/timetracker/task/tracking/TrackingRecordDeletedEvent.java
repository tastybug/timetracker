package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.task.OttoEvent;

public class TrackingRecordDeletedEvent implements OttoEvent {

    private String trackingRecordUuid;

    public TrackingRecordDeletedEvent(String uuid) {
        this.trackingRecordUuid = uuid;
    }

    public String getTrackingRecordUuid() {
        return trackingRecordUuid;
    }
}