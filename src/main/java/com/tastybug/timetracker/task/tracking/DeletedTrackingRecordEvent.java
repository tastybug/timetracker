package com.tastybug.timetracker.task.tracking;

import com.tastybug.timetracker.infrastructure.otto.OttoEvent;

public class DeletedTrackingRecordEvent implements OttoEvent {

    private String trackingRecordUuid;

    public DeletedTrackingRecordEvent(String uuid) {
        this.trackingRecordUuid = uuid;
    }

    public String getTrackingRecordUuid() {
        return trackingRecordUuid;
    }
}